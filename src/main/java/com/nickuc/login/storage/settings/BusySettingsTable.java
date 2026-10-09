package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.SettingsDefinition;

public class BusySettingsTable extends SettingsDefinition {
   public BusySettingsTable(PasswordStore target) {
      super(target, MessageOption.ACTIVE_MAIN_MESSAGEOPTION);
   }
}
