package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PasswordTable;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;

public class LocaleProvider extends StoredSettingsGateway {
   private void sendMessage(String target) {
      if (target == null) {
         throw new IllegalArgumentException("Player name cannot be null!");
      }

      if (target.isEmpty()) {
         throw new IllegalArgumentException("Player name cannot be empty!");
      }

      IndirectPasswordResolver input = this.passwordStore.findIndirectPasswordResolver();
      SpawnLookup output = input.computeSpawnLookup(target, null, null, true);
      if (output == null) {
         throw new RuntimeException("Unable to load the " + target + "'s account.");
      }

      if (!output.fetchStateAndState()) {
         output.performTask();
         if (input.checkState(this.loginBarrier, output)) {
            this.activeTimestamp++;
         }
      }
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("Username");
      boolean input = Boolean.parseBoolean(target.getString("Premium"));
      if (input) {
         this.sendMessage(this.activeName);
      }
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("DB-Database", "mydb_nyx");
      if (input.isEmpty()) {
         throw new IllegalArgumentException("Database cannot be empty!");
      }

      String output = target.b("Storage-Type").trim().toLowerCase(Locale.ENGLISH);
      int context = target.a("DB-Port", 3306);
      String data = target.a("DB-Hostname", "127.0.0.1");
      String value = target.a("DB-Username", "user");
      String result = target.a("DB-Password", "password");
      boolean request = target.a("DB-Use-SSL", false);
      Properties response = new Properties();
      response.setProperty("useSSL", Boolean.toString(request));
      response.setProperty("verifyServerCertificate", "false");
      if (output.contains("mariadb")) {
         this.sharedListenerContract = PasswordTable.loadPasswordTable(
            this.passwordStore, PasswordRepository.computePasswordRepository(data, context, input, value, result, response)
         );
      } else {
         if (!output.contains("mysql")) {
            throw new UnsupportedOperationException("Unsupported Nyx storage type! " + output);
         }

         this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
            this.passwordStore, PasswordRepository.computePasswordRepository(data, context, input, value, result, response)
         );
      }
   }

   public LocaleProvider(PasswordStore target) {
      super(target, MessageOption.ACTIVE_PRIVATE_MESSAGEOPTION, "nyx.yml", "nyx_users");
   }
}
