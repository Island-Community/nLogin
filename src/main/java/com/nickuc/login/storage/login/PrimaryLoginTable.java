package com.nickuc.login.storage.login;

import com.nickuc.login.account.MessageOption;

public class PrimaryLoginTable extends SettingsTable {
   public PrimaryLoginTable(PasswordStore target) {
      super(target, MessageOption.SAFE_MESSAGEOPTION, "logins.yml", "Login", "senha", null);
   }
}
