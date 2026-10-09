package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.SettingsDefinition;

public class SettingsCollection extends SettingsDefinition {
   public SettingsCollection(PasswordStore target) {
      super(target, MessageOption.ACTIVE_PRIMARY_MESSAGEOPTION, "Senhas.yml");
   }
}
