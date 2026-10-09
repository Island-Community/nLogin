package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class PasswordSource extends StoredSettingsGateway {
   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("username");
      UUID input = DeadLoginFlow.buildUniqueId(target.getString("uuid"));
      String output = target.getString("password");
      String context = target.getString("salt");
      String data = target.getString("lastJoinAddress");
      boolean value = "1".equals(target.getString("paid"));
      if (output != null) {
         String[] result = output.split("\\$");
         if (result.length == 4) {
            String request = result[1].toUpperCase(Locale.ENGLISH);
            if (!"2a".equalsIgnoreCase(request)) {
               this.executeMessage(this.activeName, output, request);
               return;
            }
         } else {
            String entry = context != null ? "$" + context : "";
            switch (output.length()) {
               case 32:
                  output = "$MD5$" + output + entry;
                  break;
               case 64:
                  output = "$SHA256$" + output + entry;
                  break;
               case 128:
                  output = "$SHA512$" + output + entry;
            }
         }
      }

      Long source = target.getLong("registerTime");
      Long record = target.getLong("lastJoinTime");
      String response = target.getString("email");
      this.executeMessage(this.activeName, output, data, input, contextValue -> {
         if (response != null) {
            contextValue.fetchQuickDiscordHandler().saveMessage(response);
         }

         if (value) {
            contextValue.performTask();
         }

         contextValue.executeLong(source, record);
      });
   }

   public PasswordSource(PasswordStore target) {
      super(target, MessageOption.SECONDARY_MESSAGEOPTION, "configuration.yml", "mineLogin");
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.data-type", "").toUpperCase(Locale.ENGLISH);
      switch (input) {
         case "MYSQL":
            int data = target.a("database.port", DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount());
            String value = target.b("database.hostname");
            String result = target.b("database.username");
            String request = target.b("database.password");
            String response = target.b("database.base");
            this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
               this.passwordStore, PasswordRepository.computePasswordRepository(value, data, response, result, request, new Properties())
            );
            return;
         case "H2":
            throw new UnsupportedOperationException("H2 conversion not supported. Please contact us: www.nickuc.com/discord or support@nickuc.com");
         case "MONGODB":
            throw new UnsupportedOperationException("MongoDB conversion not supported. Please contact us: www.nickuc.com/discord or support@nickuc.com");
         default:
            throw new IllegalArgumentException("Unsupported backend type: " + input);
      }
   }
}
