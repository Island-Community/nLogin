package com.nickuc.login.auth.password;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.MojangCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.Locale;

public class PasswordCheckpoint extends LoginSource {
   public PasswordCheckpoint(PasswordStore target) {
      super(target, "debug", null, true, true);
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (target instanceof VerifiedServerAdapter) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.FAST_LOUDPROXYSTATE);
      } else if (input.length == 1) {
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cMissing parameters.");
      } else {
         try {
            String output = input[1].toLowerCase(Locale.ENGLISH);
            switch (output) {
               case "get-password":
                  if (input.length != 3) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§c" + output + " <account>");
                  } else {
                     SpawnLookup holder = this.passwordStore.findIndirectPasswordResolver().loadSpawnLookup(target, super.internalLoginOption, input, input[2]);
                     if (holder == null) {
                        return;
                     }

                     if (!holder.resolveStateForState()) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cAccount not found.");
                     } else {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§e" + holder.resolveMessage() + " (" + holder.findUpstreamSpawnState() + ")");
                     }
                  }
                  break;
               case "verify-password":
                  if (input.length != 4) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§c" + output + " <account> <password>");
                  } else {
                     SpawnLookup payload = this.passwordStore.findIndirectPasswordResolver().loadSpawnLookup(target, super.internalLoginOption, input, input[2]);
                     if (payload == null) {
                        return;
                     }

                     if (!payload.resolveStateForState()) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cAccount not found.");
                     } else if (!payload.retrieveState()) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cAccount does not have password.");
                     } else {
                        String result = input[3];
                        long request = System.currentTimeMillis();
                        boolean source = this.passwordStore.findIndirectPasswordResolver().checkState(payload, result);
                        long entry = System.currentTimeMillis() - request;
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target, (source ? "§aPassword matches." : "§cPassword does not match.") + " §e(⚑ " + OpenLocaleBarrier.resolveMessage(entry) + " ms)"
                        );
                     }
                  }
                  break;
               case "get-session":
                  if (input.length != 3) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§c" + output + " <player>");
                  } else {
                     VerifiedServerAdapter content = this.passwordStore.b().resolveVerifiedServerAdapter(input[2]);
                     if (content == null) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cPlayer is offline.");
                     } else {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, this.passwordStore.loadLimboRegistry().loadLimboCoordinator(content).toString());
                     }
                  }
                  break;
               case "del-session":
                  if (input.length != 3) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§c" + output + " <player>");
                  } else {
                     VerifiedServerAdapter element = this.passwordStore.b().resolveVerifiedServerAdapter(input[2]);
                     if (element == null) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cPlayer is offline.");
                     } else {
                        this.passwordStore.loadLimboRegistry().saveVerifiedServerAdapter(element);
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§aDone.");
                     }
                  }
                  break;
               case "print-nickname":
                  int value = MojangCoordinator.pendingEnabled = (boolean)(!MojangCoordinator.pendingEnabled ? 1 : 0);
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, value != 0 ? "§aEnabled." : "§cDisabled.");
            }
         } catch (Exception item) {
            PasswordHashContainer.handleMessage("Unexpected error in debug command", item);
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cError: " + item.getLocalizedMessage());
         }
      }
   }
}
