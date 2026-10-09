package com.nickuc.login.storage.converter;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordArchive;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PasswordTable;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class DeadPasswordConverter extends StoredSettingsGateway {
   public DeadPasswordConverter(PasswordStore target) {
      super(target, MessageOption.UPSTREAM_MESSAGEOPTION, "general.yml", "navauth_users");
   }

   @Override
   public PrimaryLoginHandler computePrimaryLoginHandler(SharedListenerContract target) {
      return target.buildPrimaryLoginHandler(
         "SELECT u.uuid, u.mojang_uuid, u.username, c.passwordHash, c.algo FROM " + this.pendingName + " u LEFT JOIN navauth_credentials c ON u.uuid = c.uuid;"
      );
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("databaseConfig.driverType", "H2_FILE").toUpperCase(Locale.ENGLISH);
      switch (input) {
         case "H2_FILE":
            throw new UnsupportedOperationException("H2 conversion not supported. Please contact us: www.nickuc.com/discord or support@nickuc.com");
         case "SQLITE":
            this.sharedListenerContract = LoginGateway.handleLoginGateway(
               this.passwordStore, new File(target.retrieveFile().getParentFile(), "default.db"), new Properties()
            );
            return;
         case "MYSQL":
         case "MARIADB":
         case "POSTGRESQL":
            boolean data = target.a("databaseConfig.ssl", false);
            String value = target.b("databaseConfig.hostname");
            String result = target.b("databaseConfig.username");
            String request = target.b("databaseConfig.password");
            String response = target.b("databaseConfig.base");
            int source = target.a("databaseConfig.port");
            if (source > 0 && source < 65535) {
               value = value + ":" + source;
            }

            Properties entry = new Properties();
            entry.setProperty("useSSL", Boolean.toString(data));
            switch (input) {
               case "MYSQL":
                  this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(value, response, result, request, entry, DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount())
                  );
                  return;
               case "MARIADB":
                  this.sharedListenerContract = PasswordTable.loadPasswordTable(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(value, response, result, request, entry, DirectNoticeCatalog.DIRECT_NOTICE_CATALOG.loadCount())
                  );
                  return;
               case "POSTGRESQL":
                  this.sharedListenerContract = PasswordArchive.buildPasswordArchive(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(value, response, result, request, entry, DirectNoticeCatalog.PENDING_DIRECTNOTICECATALOG.loadCount())
                  );
                  return;
               default:
                  return;
            }
         default:
            throw new IllegalArgumentException("Unsupported backend type: " + input);
      }
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("username");
      String input = target.getString("passwordHash");
      String output = target.getString("algo");
      UUID context = DeadLoginFlow.buildUniqueId(target.getString("uuid"));
      UUID data = DeadLoginFlow.buildUniqueId(target.getString("mojang_uuid"));
      switch (output) {
         case "BCRYPT":
         case "ARGON2":
            if (data != null) {
               this.executeMessage(this.activeName, input, null, context, data);
            } else {
               this.updateMessage(this.activeName, input, null, context);
            }

            return;
         default:
            this.executeMessage(this.activeName, input, output);
      }
   }
}
