package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
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
import java.util.UUID;
import java.util.function.Consumer;

public class LimboVerifier extends StoredSettingsGateway {
   public LimboVerifier(PasswordStore target) {
      super(target, MessageOption.CACHED_MESSAGEOPTION, "config.yml", "AUTH");
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.storage-type", "h2").toLowerCase(Locale.ROOT);
      switch (input) {
         case "h2":
            this.pendingName = "AUTH";
            this.sharedListenerContract = PrimaryLoginSource.createPrimaryLoginSource(
               this.passwordStore, LinkedSpawnOption.ACTIVE_LINKEDSPAWNOPTION, new File(target.retrieveFile().getParentFile(), "limboauth-v2"), new Properties()
            );
            break;
         case "sqlite":
            this.pendingName = "AUTH";
            this.sharedListenerContract = LoginGateway.handleLoginGateway(
               this.passwordStore, new File(target.retrieveFile().getParentFile(), "limboauth.db"), new Properties()
            );
            break;
         case "mysql":
         case "mariadb":
         case "postgresql":
            String data = target.a("database.hostname", "127.0.0.1:3306");
            String value = target.a("database.user", "user");
            String result = target.a("database.password", "password");
            String request = target.a("database.database", "password");
            Properties response = new Properties();
            String source = target.a("database.connection-parameters", "?autoReconnect=true&initialTimeout=1&useSSL=false");
            if (source.length() >= 2 && source.charAt(0) == '?') {
               source = source.substring(1);

               for (String element : source.split("&")) {
                  String[] content = element.split("=");
                  if (content.length == 2) {
                     response.put(content[0], content[1]);
                  }
               }
            }

            switch (input) {
               case "mariadb":
                  this.sharedListenerContract = PasswordTable.loadPasswordTable(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(data, request, value, result, response, DirectNoticeCatalog.DIRECT_NOTICE_CATALOG.loadCount())
                  );
                  return;
               case "mysql":
                  this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(data, request, value, result, response, DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount())
                  );
                  return;
               case "postgresql":
                  this.sharedListenerContract = PasswordArchive.buildPasswordArchive(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(data, request, value, result, response, DirectNoticeCatalog.PENDING_DIRECTNOTICECATALOG.loadCount())
                  );
                  return;
               default:
                  return;
            }
         default:
            throw new UnsupportedOperationException("Unsupported database type! " + input);
      }
   }

   @Override
   public File retrieveFile() {
      return new File(this.passwordStore.resolveFile().getParentFile(), this.messageOption.resolveMessage().toLowerCase(Locale.ENGLISH));
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("NICKNAME");
      String input = target.getString("HASH");
      String output = target.getString("IP");
      long context = target.getLong("REGDATE");
      UUID value = DeadLoginFlow.buildUniqueId(target.getString("UUID"));
      UUID result = DeadLoginFlow.buildUniqueId(target.getString("PREMIUMUUID"));
      if (input != null) {
         input = input.replace("BCRYPT$", "$2a$");
      }

      Consumer request = inputValue -> inputValue.executeLong(context, context);
      if (result != null && result.version() == 4) {
         this.updateMessage(this.activeName, input, output, value, result, request);
      } else {
         this.executeMessage(this.activeName, input, output, value, request);
      }
   }
}
