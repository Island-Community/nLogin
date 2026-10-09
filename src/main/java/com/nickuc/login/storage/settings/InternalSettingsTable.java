package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.SettingsDefinition;

public class InternalSettingsTable extends SettingsDefinition {
   public InternalSettingsTable(PasswordStore target) {
      super(target, MessageOption.ACTIVE_STORED_MESSAGEOPTION);
   }
}
