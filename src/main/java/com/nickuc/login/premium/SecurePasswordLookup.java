package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.LinkedSpawnOption;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
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
import java.util.function.Consumer;

public class SecurePasswordLookup extends StoredSettingsGateway {
   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("realName");
      String input = target.getString("password");
      String output = target.getString("lastIp");
      long context = target.getLong("lastLogin");
      long value = target.getLong("regDate");
      boolean request = target.getBoolean("premium");
      Consumer response = contextValue -> contextValue.executeLong(value, context);
      String[] source = input.split("\\$");
      if (source.length > 1) {
         String entry = source[1].toUpperCase(Locale.ENGLISH);
         switch (entry) {
            case "SHA":
               if (source.length != 4) {
                  throw new IllegalArgumentException("Unsupported hash parts length for SHA256! " + source.length);
               }

               input = String.format("$SHA256$%s$%s", source[3], source[2]);
            case "ARGON2I":
            case "ARGON2D":
            case "ARGON2ID":
            case "2A":
               break;
            default:
               this.executeMessage(this.activeName, input, entry);
               return;
         }
      } else {
         if (input.length() != 64) {
            this.executeMessage(this.activeName, input, null);
            return;
         }

         input = "$SHA256$" + input;
      }

      if (request) {
         this.updateMessage(this.activeName, input, output, null, null, response);
      } else {
         this.executeMessage(this.activeName, input, output, null, response);
      }
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.type", "H2").toUpperCase(Locale.ENGLISH);
      switch (input) {
         case "H2":
            this.sharedListenerContract = PrimaryLoginSource.createPrimaryLoginSource(
               this.passwordStore,
               LinkedSpawnOption.PENDING_LINKEDSPAWNOPTION,
               new File(target.retrieveFile().getParentFile(), "auth-v2"),
               new Properties(),
               "sa",
               null
            );
            break;
         case "SQLITE":
            this.sharedListenerContract = LoginGateway.handleLoginGateway(
               this.passwordStore, new File(target.retrieveFile().getParentFile(), "auth.db"), new Properties()
            );
            break;
         case "MYSQL":
         case "MARIADB":
         case "POSTGRESQL":
            String data = target.a("database.host", "localhost");
            String value = target.a("database.user", "root");
            String result = target.a("database.password", "");
            String request = target.b("database.database");
            int response = target.a("database.port");
            if (response > 0 && response < 65535) {
               data = data + ":" + response;
            }

            Properties source = new Properties();
            switch (input) {
               case "MYSQL":
                  this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(data, request, value, result, source, DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount())
                  );
                  return;
               case "MARIADB":
                  this.sharedListenerContract = PasswordTable.loadPasswordTable(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(data, request, value, result, source, DirectNoticeCatalog.DIRECT_NOTICE_CATALOG.loadCount())
                  );
                  return;
               case "POSTGRESQL":
                  this.sharedListenerContract = PasswordArchive.buildPasswordArchive(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(data, request, value, result, source, DirectNoticeCatalog.PENDING_DIRECTNOTICECATALOG.loadCount())
                  );
                  return;
               default:
                  return;
            }
         default:
            throw new IllegalArgumentException("Unsupported backend type: " + input);
      }
   }

   public SecurePasswordLookup(PasswordStore target) {
      super(target, MessageOption.INTERNAL_MESSAGEOPTION, "config.yml", "auth_users");
   }
}
