package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.SettingsDefinition;

public class FastSettingsTable extends SettingsDefinition {
   public FastSettingsTable(PasswordStore target) {
      super(target, MessageOption.ACTIVE_CACHED_MESSAGEOPTION);
   }
}
