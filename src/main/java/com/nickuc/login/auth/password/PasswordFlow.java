package com.nickuc.login.auth.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.settings.SettingsTable;

public class PasswordFlow extends SettingsTable {
   public PasswordFlow(PasswordStore target) {
      super(target, MessageOption.ACTIVE_PENDING_MESSAGEOPTION, "Accounts.yml", "Login", "password", null);
   }
}
