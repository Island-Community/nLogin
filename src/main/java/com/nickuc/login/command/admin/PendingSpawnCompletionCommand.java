package com.nickuc.login.command.admin;

import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;

public class PendingSpawnCompletionCommand extends LoginSource {
   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cUse the command \"%s\" instead of \"%s\".", "nloginc dump", "nlogin dump");
   }

   public PendingSpawnCompletionCommand(PasswordStore target) {
      super(target, "dump", null, false, true);
   }
}
