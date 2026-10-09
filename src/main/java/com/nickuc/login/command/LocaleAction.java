package com.nickuc.login.command;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.Locale;

public class LocaleAction extends LoginSource {
   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (target instanceof VerifiedServerAdapter) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.FAST_LOUDPROXYSTATE);
      } else if (input.length != 2) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " <player>"
         );
      } else {
         VerifiedServerAdapter output = this.passwordStore.b().resolveVerifiedServerAdapter(input[1]);
         if (output == null) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.OUTGOING_LOUDPROXYSTATE);
         } else {
            LimboRegistry context = this.passwordStore.loadLimboRegistry();
            LimboCoordinator data = context.loadLimboCoordinator(output);
            SpawnLookup value = data.loadSpawnLookup();
            if (!value.fetchState()) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE, output.getName());
            } else if (context.canState(output)) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.LIVE_LOUDPROXYSTATE, output.getName());
            } else {
               this.passwordStore.findSettingsLinker().saveSpawnLookup(value, output, true, true);
               String result = value.retrieveMessage();
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§a✔ You forced the §f" + result + "'s §alogin.");
               PasswordHashContainer.dispatchMessage("The " + result + " player had his account authenticated by " + target.getName() + ".");
            }
         }
      }
   }

   public LocaleAction(PasswordStore target) {
      super(target, "forcelogin", "nlogin.admin", true, false);
   }
}
