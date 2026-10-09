package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.LinkedSpawnOption;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordArchive;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PasswordTable;
import com.nickuc.login.storage.login.PrimaryLoginSource;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class FastSettingsResolver extends StoredSettingsGateway {
   private boolean enabled;

   public FastSettingsResolver(PasswordStore target) {
      super(target, MessageOption.ACTIVE_SHARED_MESSAGEOPTION, "config.yml", "premium");
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("Name");
      boolean input = target.getBoolean("Premium");
      if (input) {
         UUID output = DeadLoginFlow.buildUniqueId(target.getString("UUID"));
         if (output != null) {
            this.updateMessage(this.activeName, output);
         }
      }
   }

   private File resolveFile(String target) {
      String input = target.replace("{pluginDir}", "");
      if (input.length() >= 2) {
         input = input.substring(1);
         return new File(this.retrieveFile(), input);
      } else {
         throw new IllegalArgumentException("Invalid FastLogin database! " + target);
      }
   }

   private void updateMessage(String target, UUID input) {
      if (target == null) {
         throw new IllegalArgumentException("Player name cannot be null!");
      }

      if (target.isEmpty()) {
         throw new IllegalArgumentException("Player name cannot be empty!");
      }

      if (input == null) {
         throw new IllegalArgumentException("Unique ID cannot be null!");
      }

      if (input.version() != 4) {
         throw new IllegalStateException("Unique ID is not premium for " + target + "! uuid = " + DeadLoginFlow.processMessage(input));
      }

      IndirectPasswordResolver output = this.passwordStore.findIndirectPasswordResolver();
      SpawnLookup context = output.computeSpawnLookup(target, input, null, false);
      if (context == null) {
         throw new RuntimeException("Unable to load the " + target + "'s account.");
      }

      if (!context.fetchState()) {
         context = output.computeSpawnLookup(target, null, null, false);
         if (context == null) {
            throw new RuntimeException("Unable to load the " + target + "'s account.");
         }
      }

      context.processUniqueId(input);
      UUID data = context.getUniqueId();
      if (data == null) {
         data = this.enabled ? input : DeadLoginFlow.computeUniqueId(target);
      } else if (data.version() == 3 && this.enabled) {
         data = input;
      }

      context.handleUniqueId(data);
      if (output.checkState(this.loginBarrier, context)) {
         this.activeTimestamp++;
      }
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database", "");
      if (input.isEmpty()) {
         throw new IllegalArgumentException("Database cannot be empty!");
      }

      this.enabled = target.a("premiumUuid", true);
      String output = target.b("driver").trim().toLowerCase(Locale.ENGLISH);
      if (output.contains("sqlite")) {
         this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, this.resolveFile(input), new Properties());
      } else if (output.contains("h2")) {
         this.sharedListenerContract = PrimaryLoginSource.createPrimaryLoginSource(
            this.passwordStore, LinkedSpawnOption.ACTIVE_LINKEDSPAWNOPTION, this.resolveFile(input), new Properties()
         );
      } else {
         int context = target.a("port", 3306);
         String data = target.b("host");
         String value = target.a("username", "");
         String result = target.b("password");
         boolean request = target.a("useSSL", false);
         Properties response = new Properties();
         if (request) {
            boolean source = target.a("allowPublicKeyRetrieval", false);
            String entry = target.b("ServerRSAPublicKeyFile");
            String record = target.a("sslMode", "Required");
            response.put("allowPublicKeyRetrieval", source);
            response.put("serverRSAPublicKeyFile", entry);
            response.put("sslMode", record);
         }

         if (output.contains("mariadb")) {
            this.sharedListenerContract = PasswordTable.loadPasswordTable(
               this.passwordStore, PasswordRepository.computePasswordRepository(data, context, input, value, result, response)
            );
         } else if (output.contains("mysql")) {
            this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
               this.passwordStore, PasswordRepository.computePasswordRepository(data, context, input, value, result, response)
            );
         } else {
            if (!output.contains("postgre")) {
               throw new UnsupportedOperationException("Unsupported FastLogin database driver! " + output);
            }

            this.sharedListenerContract = PasswordArchive.buildPasswordArchive(
               this.passwordStore, PasswordRepository.computePasswordRepository(data, context, input, value, result, response)
            );
         }
      }
   }
}
