package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class SafePasswordRepository extends StoredSettingsGateway {
   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      int input = target.a("data-storage.port", 3306);
      String output = target.a("data-storage.host", "localhost");
      String context = target.a("data-storage.database", "minecraft");
      String data = target.a("data-storage.username", "root");
      String value = target.a("data-storage.password", "1234");
      this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
         this.passwordStore, PasswordRepository.computePasswordRepository(output, input, context, data, value, new Properties())
      );
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("name");
      UUID input = DeadLoginFlow.buildUniqueId(target.getString("uuid"));
      String output = target.getString("password");
      int context = target.getInt("registered") == 1 ? 1 : 0;
      if (context == 0) {
         output = null;
      }

      String data = target.getString("hash").toUpperCase(Locale.ENGLISH);
      switch (data) {
         case "MD5":
         case "SHA256":
         case "SHA512":
            output = "$" + data + "$" + output;
            String record = target.getString("ip");
            long item = target.getLong("first_login");
            long response = target.getLong("last_login");
            this.executeMessage(this.activeName, output, record, input, contextValue -> contextValue.executeLong(item, response));
            return;
         default:
            this.executeMessage(this.activeName, output, data);
      }
   }

   public SafePasswordRepository(PasswordStore target) {
      super(target, MessageOption.AUTHENTICATED_MESSAGEOPTION, "config.yml", "pixellogin");
   }

   @Override
   public boolean isAvailable() {
      if (!super.isAvailable()) {
         return false;
      }

      PasswordHashLoader target = this.computePasswordHashLoader("config.yml");
      return target.canState("data-storage.enable")
         && target.d("data-storage.enable")
         && target.canState("data-storage.host")
         && target.canState("data-storage.port")
         && target.canState("data-storage.database")
         && target.canState("data-storage.username")
         && target.canState("data-storage.password");
   }
}
