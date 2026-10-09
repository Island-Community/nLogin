package com.nickuc.login.storage.login;

import com.nickuc.login.account.MessageOption;

public class CachedLoginTable extends SettingsTable {
   public CachedLoginTable(PasswordStore target) {
      super(target, MessageOption.ACTIVE_LOCAL_MESSAGEOPTION, "data.yml", "Login", "Senha", "IP");
   }
}
