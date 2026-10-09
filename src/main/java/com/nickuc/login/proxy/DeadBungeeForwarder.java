package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.QuickLoginBarrier;
import com.nickuc.login.auth.login.RemoteLoginBarrier;
import com.nickuc.login.auth.sha.Sha256Barrier;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.config.PasswordHashDefinition;
import com.nickuc.login.platform.server.AuthenticatedServerAdapter;
import com.nickuc.login.platform.server.NestedServerAdapter;
import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.security.hashing.PasswordHashDigest;
import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

public class DeadBungeeForwarder implements ParentAccountHandler {
   private final ProxyServer proxyServer;
   private final PasswordHashDefinition passwordHashDefinition;
   private final AuthenticatedServerAdapter authenticatedServerAdapter;

   @Override
   public NestedServerAdapter processNestedServerAdapter(PasswordHashCommand<?> target) {
      return new SharedBungeeLink(this.proxyServer, target);
   }

   @Nullable
   @Override
   public QuickLoginBarrier loadQuickLoginBarrier(String target) {
      Plugin input = this.proxyServer.getPluginManager().getPlugin(target);
      if (input == null) {
         return null;
      }

      PluginDescription output = input.getDescription();
      return output == null ? null : new QuickLoginBarrier(output.getName(), output.getVersion(), input);
   }

   @Override
   public Collection<VerifiedServerAdapter> fetchCollection() {
      return this.proxyServer.getPlayers().stream().map(this::processVerifiedServerAdapter).collect(Collectors.toList());
   }

   @Override
   public VerifiedServerAdapter resolveVerifiedServerAdapter(String target) {
      return PasswordHashDigest.processPasswordHashDigest(this.passwordHashDefinition, this.proxyServer, target);
   }

   @Override
   public VerifiedServerAdapter createVerifiedServerAdapter(UUID target) {
      ProxiedPlayer input = this.proxyServer.getPlayer(target);
      return input != null ? this.processVerifiedServerAdapter(input) : null;
   }

   @Override
   public VerifiedServerAdapter processVerifiedServerAdapter(Object target) {
      return PasswordHashDigest.processPasswordHashDigest(this.passwordHashDefinition, this.proxyServer, (Object)target);
   }

   @Override
   public AuthenticatedServerAdapter findAuthenticatedServerAdapter() {
      return this.authenticatedServerAdapter;
   }

   @Override
   public <T> T findObject() {
      return (T)this.proxyServer;
   }

   @Override
   public boolean canState(String target) {
      return this.proxyServer.getPluginManager().getPlugin(target) != null;
   }

   @Override
   public void executeTask() {
      this.proxyServer.stop();
   }

   @Override
   public Sha256Barrier[] retrieveValues() {
      Collection target = this.proxyServer.getPluginManager().getPlugins();
      Sha256Barrier[] input = new Sha256Barrier[target.size()];
      int output = 0;

      for (Plugin data : target) {
         PluginDescription value = data.getDescription();
         File result = data.getFile();
         input[output++] = new Sha256Barrier(
            value.getName(), value.getVersion(), RemoteLoginBarrier.loadRemoteLoginBarrier(value.getAuthor()), result != null ? result.toPath() : null
         );
      }

      return input;
   }

   public DeadBungeeForwarder(PasswordHashDefinition target, ProxyServer input, AuthenticatedServerAdapter output) {
      this.passwordHashDefinition = target;
      this.proxyServer = input;
      this.authenticatedServerAdapter = output;
   }
}
