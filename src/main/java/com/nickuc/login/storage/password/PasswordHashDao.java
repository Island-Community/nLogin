package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.ProxyCatalog;
import java.util.Locale;

public class PasswordHashDao extends PendingSettingsGateway {
   @Override
   public void handlePasswordHashLoader(PasswordHashLoader target, String input, String output) {
      String context = target.b(output + "type");
      if (context != null) {
         String data = target.b(output + "hash");
         ProxyCatalog value = ProxyCatalog.valueOf(context.toUpperCase(Locale.ENGLISH));
         this.updateMessage(input, value.createMessage(data), null, null);
      }
   }

   public PasswordHashDao(PasswordStore target) {
      super(target, MessageOption.READY_MESSAGEOPTION, "accounts.yml", "accounts");
   }
}
