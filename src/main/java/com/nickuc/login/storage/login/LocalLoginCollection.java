package com.nickuc.login.storage.login;

import com.nickuc.login.account.MessageOption;

public class LocalLoginCollection extends SettingsTable {
   public LocalLoginCollection(PasswordStore target) {
      super(target, MessageOption.ACTIVE_CURRENT_MESSAGEOPTION, "data.yml", "Contas", "Senha", "IP");
   }
}
