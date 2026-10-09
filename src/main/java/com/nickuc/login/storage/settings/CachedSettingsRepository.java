package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.SettingsDefinition;

public class CachedSettingsRepository extends SettingsDefinition {
   public CachedSettingsRepository(PasswordStore target) {
      super(target, MessageOption.ACTIVE_VERIFIED_MESSAGEOPTION);
   }
}
