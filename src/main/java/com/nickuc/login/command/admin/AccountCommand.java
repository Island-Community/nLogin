package com.nickuc.login.command.admin;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.concurrent.TimeUnit;

public class AccountCommand extends LoginSource {
   private static float factor = Float.intBitsToFloat(1077936128);
   private static float activeFactor = Float.intBitsToFloat(1097859072);

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 2) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage() + " <player>");
      } else {
         LiveLoginCheckpoint output = new LiveLoginCheckpoint();
         String context = input[1];
         IndirectPasswordResolver data = this.passwordStore.findIndirectPasswordResolver();
         SpawnLookup value = data.loadSpawnLookup(target, super.internalLoginOption, input, context);
         if (value != null) {
            synchronized (value.object) {
               if (!value.resolveStateForState()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else if (!data.verifyState(value)) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else {
                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, activeFactor, factor);
                  String request = value.retrieveMessage();
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§a✔ You have successfully deleted the §f" + request + "'s §aaccount.");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§e⚑ This operation took §f" + output.loadMessage(TimeUnit.MILLISECONDS, 2) + "ms§e.");
                  PasswordHashContainer.dispatchMessage("The " + request + " player had his account deleted by " + target.getName() + ".");
               }
            }
         }
      }
   }

   public AccountCommand(PasswordStore target) {
      super(target, "delete", "nlogin.command.nlogin.delete", true, true);
   }
}
