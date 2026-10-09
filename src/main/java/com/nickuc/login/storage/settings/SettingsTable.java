package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;

public class SettingsTable extends PendingSettingsGateway {
   private final String name;
   private final String activeName;

   @Override
   public void handlePasswordHashLoader(PasswordHashLoader target, String input, String output) {
      String context = target.b(output + this.name);
      String data = null;
      if (this.activeName != null) {
         data = target.b(output + this.activeName);
         if ("null".equals(data)) {
            data = null;
         }
      }

      this.updateMessage(input, context, data, null);
      this.pendingTimestamp++;
   }

   public SettingsTable(PasswordStore target, MessageOption input, String output, String context, String data, String value) {
      super(target, input, output, context);
      this.name = data;
      this.activeName = value;
   }
}
