package com.nickuc.login.storage.settings;

import com.nickuc.login.auth.login.PrivateLoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;

public class SettingsDao extends LoginSource {
   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, -1 != -1 ? "§cTask cancelled." : "§aRestart task started.");
      PasswordHashContainer.processMessage(target.getName() + (-1 != -1 ? " canceled" : " started") + " a restart process.");
      int output = 60;
      if (input.length > 1) {
         output = PrivateLoginCheckpoint.handleInteger(input[1], output);
      }

      LoginStore.handlePasswordStore(this.passwordStore, output > 0 ? output : 60);
   }

   public SettingsDao(PasswordStore target) {
      super(target, "restart", "nlogin.admin", false, true);
   }
}
