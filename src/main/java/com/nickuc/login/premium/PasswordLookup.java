package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PasswordTable;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;

public class PasswordLookup extends StoredSettingsGateway {
   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("lastNickname");
      if (this.activeName != null) {
         UUID input = DeadLoginFlow.buildUniqueId(target.getString("uniqueId"));
         UUID output = DeadLoginFlow.buildUniqueId(target.getString("premiumId"));
         String context = target.getString("hashedPassword");
         if (context != null) {
            String[] data = context.split("\\$");
            if (data.length != 3 && data.length != 4) {
               this.executeMessage(this.activeName, context, null);
               return;
            }

            int value = context.charAt(0) == '$' ? 1 : 0;
            String result = data[value].toUpperCase(Locale.ENGLISH);
            switch (result) {
               case "SHA256":
               case "SHA512":
                  context = "$" + result + "$" + data[value + 2] + "$" + data[value + 1];
                  break;
               case "BCRYPT":
                  context = "$2a" + context.substring(result.length() + value);
                  break;
               default:
                  this.executeMessage(this.activeName, context, result);
                  return;
            }
         }

         String item = target.getString("lastAddress");
         String element = target.getString("mailAddress");
         Timestamp content = null;
         Timestamp payload = null;

         try {
            content = target.getTimestamp("lastSeen");
            payload = target.getTimestamp("firstSeen");
         } catch (Exception record) {
         }

         Timestamp holder = payload;
         Timestamp source = content;
         Consumer entry = outputValue -> {
            if (element != null) {
               outputValue.fetchQuickDiscordHandler().saveMessage(element);
            }

            outputValue.executeLong(holder != null ? holder.getTime() : null, source != null ? source.getTime() : null);
         };
         if (output != null) {
            this.updateMessage(this.activeName, context, item, input, output, entry);
         } else {
            this.executeMessage(this.activeName, context, item, input, entry);
         }
      }
   }

   @Override
   public boolean isAvailable() {
      if (!super.isAvailable()) {
         return false;
      }

      PasswordHashLoader target = this.computePasswordHashLoader("configuration.yml");
      return target.canState("storageHost")
         && target.canState("storagePort")
         && target.canState("storageDatabase")
         && target.canState("storageUser")
         && target.canState("storagePassword");
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("storageType", "MYSQL").toUpperCase(Locale.ENGLISH);
      switch (input) {
         case "SQLITE":
            File record = new File(this.retrieveFile(), "database.db");
            this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, record, new Properties());
            break;
         case "MYSQL":
         case "MARIADB":
            String data = target.a("storageDatabase", "");
            if (data.isEmpty()) {
               throw new IllegalArgumentException("Database cannot be empty!");
            }

            int value = target.a("storagePort", 3306);
            String result = target.b("storageHost");
            String request = target.b("storageUser");
            String response = target.b("storagePassword");
            List source = target.processCollection("storageProperties");
            Properties entry = new Properties();
            source.forEach(targetValue -> {
               String[] inputValue = targetValue.split("=");
               if (inputValue.length == 2) {
                  entry.setProperty(inputValue[0], inputValue[1]);
               }
            });
            if ("MARIADB".equals(input)) {
               this.sharedListenerContract = PasswordTable.loadPasswordTable(
                  this.passwordStore, PasswordRepository.computePasswordRepository(result, value, data, request, response, entry)
               );
            } else {
               this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
                  this.passwordStore, PasswordRepository.computePasswordRepository(result, value, data, request, response, entry)
               );
            }
            break;
         default:
            throw new IllegalArgumentException("Unsupported backend type: " + input);
      }
   }

   public PasswordLookup(PasswordStore target) {
      super(target, MessageOption.REMOTE_MESSAGEOPTION, "configuration.yml", "user_profiles");
   }
}
