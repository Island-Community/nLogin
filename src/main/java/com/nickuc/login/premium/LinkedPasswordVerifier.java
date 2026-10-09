package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class LinkedPasswordVerifier extends StoredSettingsGateway {
   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("name");
      UUID input = DeadLoginFlow.buildUniqueId(target.getString("uuid"));
      String output = target.getString("password");
      String context = target.getString("log_ip");
      int data = target.getInt("premium") == 1 ? 1 : 0;
      String value = null;
      if (output != null && !"null".equals(output)) {
         String[] result = output.split("\\$");
         if (result.length >= 3) {
            String request = result[1].toUpperCase(Locale.ENGLISH);
            switch (request) {
               case "2A":
               case "2Y":
               case "SHA":
                  value = output;
                  break;
               default:
                  this.executeMessage(this.activeName, value, request);
            }
         } else {
            String entry = target.getString("salt");
            int record = output.length();
            switch (record) {
               case 64:
                  value = "$SHA256$" + output + "$" + entry;
                  break;
               case 128:
                  value = "$SHA512$" + output + "$" + entry;
                  break;
               default:
                  this.executeMessage(this.activeName, value, "output size " + record);
            }
         }
      }

      if (data != 0) {
         this.executeMessage(this.activeName, value, context, input, input != null && input.version() == 4 ? input : null);
      } else if (value != null) {
         this.updateMessage(this.activeName, value, context, input);
      }
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      int input = target.a("MySQL.port", 3306);
      String output = target.b("MySQL.ip");
      String context = target.b("MySQL.database");
      String data = target.b("MySQL.user");
      String value = target.b("MySQL.password");
      Properties result = new Properties();
      String request = target.b("MySQL.extra");
      if (request != null) {
         String[] response = request.split("&");
         if (response.length >= 2) {
            for (String item : response) {
               String[] element = item.split("=");
               if (element.length == 2) {
                  result.setProperty(element[0], element[1]);
               }
            }
         } else {
            String[] payload = request.split("=");
            if (payload.length == 2) {
               result.setProperty(payload[0], payload[1]);
            }
         }
      }

      boolean content = target.d("MySQL.ssl");
      result.setProperty("useSSL", Boolean.toString(content));
      this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
         this.passwordStore, PasswordRepository.computePasswordRepository(output, input, context, data, value, result)
      );
   }

   public LinkedPasswordVerifier(PasswordStore target) {
      super(target, MessageOption.PRIVATE_MESSAGEOPTION, "config.yml", "playerdata");
      if (new File(this.retrieveFile(), "Config.yml").exists()) {
         this.name = "Config.yml";
      }
   }
}
