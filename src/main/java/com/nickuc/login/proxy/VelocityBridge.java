package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.QuickLoginBarrier;
import com.nickuc.login.auth.sha.Sha256Barrier;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.platform.server.AuthenticatedServerAdapter;
import com.nickuc.login.platform.server.NestedServerAdapter;
import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.PasswordHashCodec;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.proxy.ProxyServer;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;


public class VelocityBridge implements ParentAccountHandler {
   private final DiscordForwarder discordForwarder;
   private final AuthenticatedServerAdapter authenticatedServerAdapter;
   private final ProxyServer proxyServer;

   @Override
   public VerifiedServerAdapter createVerifiedServerAdapter(UUID target) {
      return this.proxyServer.getPlayer(target).map(this::processVerifiedServerAdapter).orElse(null);
   }

   @Override
   public Sha256Barrier[] retrieveValues() {
      Collection target = this.proxyServer.getPluginManager().getPlugins();
      Sha256Barrier[] input = new Sha256Barrier[target.size()];
      int output = 0;

      for (PluginContainer data : target) {
         PluginDescription value = data.getDescription();
         input[output++] = new Sha256Barrier(value.getId(), value.getVersion().orElse("unknown"), value.getAuthors(), (Path)value.getSource().orElse(null));
      }

      return input;
   }

   public VelocityBridge(DiscordForwarder target, ProxyServer input, AuthenticatedServerAdapter output) {
      this.discordForwarder = target;
      this.proxyServer = input;
      this.authenticatedServerAdapter = output;
   }

   @Override
   public VerifiedServerAdapter resolveVerifiedServerAdapter(String target) {
      return PasswordHashCodec.processPasswordHashCodec(this.discordForwarder, this.proxyServer, target);
   }

   @Override
   public NestedServerAdapter processNestedServerAdapter(PasswordHashCommand<?> target) {
      return new VelocityForwarder(this.proxyServer, target);
   }

   @Override
   public Collection<VerifiedServerAdapter> fetchCollection() {
      return this.proxyServer.getAllPlayers().stream().map(this::processVerifiedServerAdapter).collect(Collectors.toList());
   }

   @Override
   public <T> T findObject() {
      return (T)this.proxyServer;
   }

   @Nullable
   @Override
   public QuickLoginBarrier loadQuickLoginBarrier(String target) {
      return this.proxyServer.getPluginManager().getPlugin(target).map(instance -> {
         PluginDescription targetValue = instance.getDescription();
         Object input = instance.getInstance().map(instanceValue -> (PluginContainer)instanceValue).orElse(instance);
         return new QuickLoginBarrier((String)targetValue.getName().orElse(null), (String)targetValue.getVersion().orElse(null), input);
      }).orElse(null);
   }

   @Override
   public void executeTask() {
      this.proxyServer.shutdown();
   }

   @Override
   public AuthenticatedServerAdapter findAuthenticatedServerAdapter() {
      return this.authenticatedServerAdapter;
   }

   @Override
   public VerifiedServerAdapter processVerifiedServerAdapter(Object target) {
      return PasswordHashCodec.processPasswordHashCodec(this.discordForwarder, this.proxyServer, target);
   }

   @Override
   public boolean canState(String target) {
      return this.proxyServer.getPluginManager().isLoaded(target);
   }
}
