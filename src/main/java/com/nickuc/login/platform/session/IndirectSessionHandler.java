package com.nickuc.login.platform.session;

import com.nickuc.login.auth.login.IncomingLoginGate;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.discord.DiscordNotifier;
import com.nickuc.login.discord.ParentDiscordNotifier;
import com.nickuc.login.discord.PasswordHashBridge;
import com.nickuc.login.premium.ParentSettingsLookup;
import com.nickuc.login.premium.UpdateLookup;
import java.io.File;

public interface IndirectSessionHandler<Plugin> {
   Object buildObject(int target);

   default IncomingLoginGate findIncomingLoginGate() {
      return this.getParentDiscordNotifier().findIncomingLoginGate();
   }

   default PasswordHashBridge fetchPasswordHashBridge() {
      return this.getParentDiscordNotifier().fetchPasswordHashBridge();
   }

   default <T extends DiscordNotifier> T findDiscordNotifier() {
      return this.getParentDiscordNotifier().loadDiscordNotifier();
   }

   String retrieveMessage();

   boolean resolveState();

   TightConnectionContract resolveTightConnectionContract();

   Plugin loadObject();

   default PasswordHashLoader retrievePasswordHashLoader() {
      return this.getParentDiscordNotifier().retrievePasswordHashLoader();
   }

   SenderAdapter fetchSenderAdapter();

   File resolveFile();

   default UpdateLookup retrieveUpdateLookup() {
      return this.getParentDiscordNotifier().retrieveUpdateLookup();
   }

   default Object getObject() {
      return this.getParentDiscordNotifier().getObject();
   }

   void executeTask();

   String getMessage();

   LinkedSessionHandler processLinkedSessionHandler(boolean target);

   default ParentSettingsLookup fetchParentSettingsLookup() {
      return this.getParentDiscordNotifier().fetchParentSettingsLookup();
   }

   ParentDiscordNotifier getParentDiscordNotifier();

   default ParentAccountHandler retrieveParentAccountHandler() {
      return this.getParentDiscordNotifier().retrieveParentAccountHandler();
   }

   default void savePasswordHashCommand(PasswordHashCommand<?> target, PasswordHashCommand<?>... input) {
      this.getParentDiscordNotifier().savePasswordHashCommand(target, input);
   }

   CachedSessionHandler resolveCachedSessionHandler();

   default File fetchFile() {
      return this.getParentDiscordNotifier().fetchFile();
   }

   default void saveOutgoingAccountHandler(OutgoingAccountHandler target, OutgoingAccountHandler... input) {
      this.getParentDiscordNotifier().saveOutgoingAccountHandler(target, input);
   }
}
