package com.nickuc.login.command.auth;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PasswordAction extends LoginSource {
   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 3) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " <player> <password>"
         );
      } else {
         LiveLoginCheckpoint output = new LiveLoginCheckpoint();
         SpawnLookup context = this.passwordStore.findIndirectPasswordResolver().handleSpawnLookup(target, super.internalLoginOption, input, input[1], true);
         if (context != null) {
            if (context.retrieveState()) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ACTIVE_CURRENT_LOUDPROXYSTATE, context.retrieveMessage());
               CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
            } else {
               String data = input[2];
               if (data.length() <= SpawnState.ACTIVE_OUTGOING_SPAWNSTATE.r()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ACTIVE_REMOTE_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else if (data.length() >= SpawnState.ACTIVE_SECONDARY_SPAWNSTATE.r()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ACTIVE_LOCAL_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else {
                  VerifiedServerAdapter value = this.passwordStore.b().resolveVerifiedServerAdapter(context.retrieveMessage());
                  String result = context.retrieveMessage();
                  String request = null;
                  if (value != null) {
                     request = value.resolveMessage();
                  }

                  synchronized (context.object) {
                     if (!this.passwordStore.findIndirectPasswordResolver().checkState(context, result, data, null, request)) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                     } else {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§a✔ You have successfully registered the player §f" + result + "§a.");
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target, "§e⚑ This operation took §f" + output.loadMessage(TimeUnit.MILLISECONDS, 2) + "ms§e."
                        );
                        PasswordHashContainer.dispatchMessage(
                           "The " + result + " account (" + context.loadSpawnOption() + ") was registered by " + target.getName() + "."
                        );
                     }
                  }
               }
            }
         }
      }
   }

   public PasswordAction(PasswordStore target) {
      super(target, "register", "nlogin.command.nlogin.register", false, false);
   }
}
