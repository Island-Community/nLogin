package com.nickuc.login.security.hashing;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;

public class PasswordProvider extends StoredSettingsGateway {
   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      File input = new File(this.retrieveFile(), "data.db");
      this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, input, new Properties());
   }

   public PasswordProvider(PasswordStore target) {
      super(target, MessageOption.OPEN_MESSAGEOPTION, "config.yml", "nexauth_users");
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("name");
      String input = target.getString("password");
      String output = target.getString("encrypt").toUpperCase(Locale.ENGLISH);
      switch (output) {
         case "MD5":
         case "SHA256":
         case "SHA512":
            input = "$" + output + "$" + input;
         case "BCRYPT":
            String value = target.getString("ip");
            this.updateMessage(this.activeName, input, value, null);
            return;
         default:
            this.executeMessage(this.activeName, input, output);
      }
   }
}
