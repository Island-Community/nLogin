package com.nickuc.login.listener.bukkit;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.auth.login.IncomingLoginGate;
import com.nickuc.login.command.SettingsAction;
import com.nickuc.login.command.completion.VerifiedUnregisterBackupCommand;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.session.CachedSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.connection.QuickConnectionContract;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.command.SilentCommandHandler;
import com.nickuc.login.platform.premium.SilentPremiumOption;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.connection.TightConnectionContract;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.protocol.PacketListener;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;

public final class SecondarySettingsGuard implements CachedSessionHandler {
   private final DiscordGuard discordGuard;

   @Override
   public void processTask() {
      this.discordGuard.processTask();
   }

   @Override
   public OutgoingAccountHandler getOutgoingAccountHandler() {
      return new VerifiedUnregisterBackupCommand(this.discordGuard);
   }

   @Override
   public void sendTask() {
      try {
         if (LoginGuard.resolveLoginGuard().canState(LoginGuard.localLoginGuard)) {
            LoginGuard.resolveLoginGuard();
            QuickConnectionContract.retrieveQuickConnectionContract();
            SilentCommandHandler.resolveSilentCommandHandler();
            SilentPremiumOption.fetchSilentPremiumOption();
            QuickProxyState.values();
            PacketListener.processTask();
         }

         BungeeWriter.performDiscordGuard(this.discordGuard);
      } catch (Throwable input) {
         throw new RuntimeException("Unable to initialize Bukkit apis.", input);
      }
   }

   @Override
   public ParentAccountHandler resolveParentAccountHandler() {
      Server target = this.discordGuard.getServer();
      return new PacketGuard(this.discordGuard, target, SettingsAction.processAuthenticatedServerAdapter(target, target.getConsoleSender()));
   }

   @Override
   public void handleTask() {
      LoginGuard target = LoginGuard.resolveLoginGuard();
      int input = target.resolveCount() == 1 && target.retrieveCount() <= 13 ? 1 : 0;
      int output = target.resolveCount() == 1 && target.retrieveCount() <= 17 ? 1 : 0;
      PasswordHashContainer.handleIndirectSessionHandler(this.discordGuard, (boolean)input, (boolean)output);
   }

   @Override
   public void dispatchTask() {
      this.discordGuard.dispatchTask();
   }

   @Override
   public void performTask() {
      this.discordGuard.performTask();
   }

   @Override
   public void updateTask() {
      this.discordGuard.updateTask();
   }

   public SecondarySettingsGuard(DiscordGuard target) {
      this.discordGuard = target;
   }

   @Override
   public TightConnectionContract retrieveTightConnectionContract() {
      return new MessageListener(this.discordGuard);
   }

   @Override
   public LinkedSessionHandler computeLinkedSessionHandler(boolean target) {
      return BungeeWriter.fetchState()
         ? new PendingLoginFilter(this.discordGuard.retrieveBukkitLoader(), target)
         : new CachedLoginFilter(this.discordGuard.retrieveBukkitLoader(), target);
   }

   @Override
   public IncomingLoginGate loadIncomingLoginGate() {
      Server target = this.discordGuard.getServer();
      return new IncomingLoginGate(
         target.getName(),
         target.getVersion(),
         "Bukkit: " + target.getBukkitVersion() + " | Server: " + target.getVersion(),
         SilentProxyState.SILENT_PROXY_STATE,
         SilentPremiumOption.fetchSilentPremiumOption().retrieveState()
      );
   }

   @Override
   public TightSenderAdapter[] findValues() {
      return this.discordGuard.findValues();
   }

   @Override
   public OutgoingAccountHandler loadOutgoingAccountHandler() {
      return LoginGuard.resolveLoginGuard().canState(LoginGuard.outgoingLoginGuard)
            && ChildLoginCheckpoint.validateState("io.papermc.paper.event.player.PlayerServerFullCheckEvent")
         ? new PrimaryPacketGuard(this.discordGuard, this.discordGuard.getServer())
         : new FastPacketGuard(this.discordGuard, this.discordGuard.getServer());
   }

   @Override
   public boolean verifyState(String target) {
      byte input = 0;
      PluginDescriptionFile output = this.discordGuard.retrieveBukkitLoader().getDescription();
      if (!output.getName().equals(target)) {
         PasswordHashContainer.updateMessage(
            "Unable to start the plugin: the name has been changed to '" + this.discordGuard.retrieveBukkitLoader().getName() + "' :c"
         );
         input = 1;
      }

      if (!output.getAuthors().contains("NickUC")) {
         PasswordHashContainer.updateMessage("Unable to start the plugin: the list of authors has been modified :c");
         input = 1;
      }

      if (output.getWebsite() != null && !output.getWebsite().equals("https://www.nickuc.com")) {
         PasswordHashContainer.updateMessage("Unable to start the plugin: the url of the official website has been changed to '" + output.getWebsite() + "' :c");
         input = 1;
      }

      return (boolean)input;
   }
}
