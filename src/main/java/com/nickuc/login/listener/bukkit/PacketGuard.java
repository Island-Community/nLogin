package com.nickuc.login.listener.bukkit;

import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.auth.login.QuickLoginBarrier;
import com.nickuc.login.auth.sha.Sha256Barrier;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.command.completion.RootPasswordCommand;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.platform.server.AuthenticatedServerAdapter;
import com.nickuc.login.platform.server.NestedServerAdapter;
import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.PasswordHashListener;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class PacketGuard implements ParentAccountHandler {
   private final DiscordGuard discordGuard;
   private final AuthenticatedServerAdapter authenticatedServerAdapter;
   private final Server server;

   @Override
   public VerifiedServerAdapter processVerifiedServerAdapter(Object target) {
      return PasswordHashListener.processPasswordHashListener(this.discordGuard, this.server, target);
   }

   @Override
   public <T> T findObject() {
      return (T)this.server;
   }

   @Nullable
   @Override
   public QuickLoginBarrier loadQuickLoginBarrier(String target) {
      Plugin input = this.server.getPluginManager().getPlugin(target);
      if (input == null) {
         return null;
      }

      PluginDescriptionFile output = input.getDescription();
      return new QuickLoginBarrier(input.getName(), output != null ? output.getVersion() : null, input);
   }

   @Override
   public Collection<VerifiedServerAdapter> fetchCollection() {
      return BungeeWriter.retrieveCollection().stream().map(this::processVerifiedServerAdapter).collect(Collectors.toList());
   }

   @Override
   public Sha256Barrier[] retrieveValues() {
      Plugin[] target = this.server.getPluginManager().getPlugins();
      Sha256Barrier[] input = new Sha256Barrier[target.length];
      int output = 0;

      for (Plugin result : target) {
         PluginDescriptionFile request = result.getDescription();

         Path response;
         try {
            response = MessageProcessor.handleFile(result.getClass()).toPath();
         } catch (Exception entry) {
            response = null;
         }

         input[output++] = new Sha256Barrier(result.getName(), request.getVersion(), request.getAuthors(), response);
      }

      return input;
   }

   public PacketGuard(DiscordGuard target, Server input, AuthenticatedServerAdapter output) {
      this.discordGuard = target;
      this.server = input;
      this.authenticatedServerAdapter = output;
   }

   @Override
   public boolean canState(String target) {
      return this.server.getPluginManager().getPlugin(target) != null;
   }

   @Override
   public VerifiedServerAdapter resolveVerifiedServerAdapter(String target) {
      return PasswordHashListener.processPasswordHashListener(this.discordGuard, this.server, target);
   }

   @Override
   public NestedServerAdapter processNestedServerAdapter(PasswordHashCommand<?> target) {
      return new RootPasswordCommand(this.server, target);
   }

   @Override
   public VerifiedServerAdapter createVerifiedServerAdapter(UUID target) {
      Player input = this.server.getPlayer(target);
      return input != null ? this.processVerifiedServerAdapter(input) : null;
   }

   @Override
   public AuthenticatedServerAdapter findAuthenticatedServerAdapter() {
      return this.authenticatedServerAdapter;
   }

   @Override
   public void executeTask() {
      this.server.shutdown();
   }
}
