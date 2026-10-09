package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import java.io.File;

public class SecondarySettingsSource extends SettingsGateway {
   public SecondarySettingsSource(PasswordStore target) {
      super(target, MessageOption.TOP_MESSAGEOPTION, "playerdata", true);
   }

   @Override
   public void saveFile(File target) {
      PasswordHashLoader input = new PasswordHashLoader(target);
      if (input.canState("Nick") && input.canState("Senha") && input.canState("Registrado")) {
         boolean output = input.d("Registrado");
         if (output) {
            String context = input.b("Nick");
            String data = input.b("Senha");
            this.updateMessage(context, "$MD5$" + data, null, null);
         }
      }
   }
}
