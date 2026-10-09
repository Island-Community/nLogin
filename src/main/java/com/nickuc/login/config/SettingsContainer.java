package com.nickuc.login.config;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.model.LoudNoticeCatalog;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;

public class SettingsContainer extends LoginSource {
   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      int output = input.length > 1 && input[1].equalsIgnoreCase("--reset") ? 1 : 0;
      LoudNoticeCatalog context = output != 0 ? Pbkdf2Linker.computeLoudNoticeCatalog(this.passwordStore) : Pbkdf2Linker.loadLoudNoticeCatalog(this.passwordStore);
      if (!context.loadState()) {
         CachedSettingsGateway.handleOutgoingSenderAdapter(
            target, output != 0 ? "§cUnable to reset the configuration." : "§cUnable to load the configuration, please review it. No changes will be made."
         );
         CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
      } else {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.SHARED_LOUDPROXYSTATE);
         CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE);
      }
   }

   public SettingsContainer(PasswordStore target) {
      super(target, "reload", "nlogin.admin", false, false, "r", "rl");
   }
}
