package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.SettingsDefinition;

public class SettingsSource extends SettingsDefinition {
   public SettingsSource(PasswordStore target) {
      super(target, MessageOption.ACTIVE_REMOTE_MESSAGEOPTION);
   }
}
