package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.premium.SpawnLookup;
import java.io.File;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;

public class SafePasswordGateway extends StoredSettingsGateway {
   public SafePasswordGateway(PasswordStore target) {
      super(target, MessageOption.OUTGOING_MESSAGEOPTION, "config.yml", "players");
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.type", "sqlite").toLowerCase(Locale.ENGLISH);
      switch (input) {
         case "sqlite":
            File data = new File(this.retrieveFile(), "data.db");
            this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, data, new Properties());
            break;
         case "mysql":
            String value = target.a("database.credentials.host", "127.0.0.1:3306");
            String result = target.a("database.credentials.user", "root");
            String request = target.a("database.credentials.password", "root");
            String response = target.a("database.credentials.database", "authy");
            this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
               this.passwordStore,
               PasswordRepository.processPasswordRepository(
                  value, response, result, request, new Properties(), DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount()
               )
            );
            break;
         default:
            throw new IllegalArgumentException("Unsupported backend type: " + input);
      }
   }

   @Override
   public void performSet(ResultSet target) {
      String input = target.getString("username");
      String output = target.getString("password");
      String context = target.getString("ip");
      UUID data = DeadLoginFlow.buildUniqueId(target.getString("uuid"));
      this.executeMessage(input, "$SHA256$" + output, context, data, (Consumer<SpawnLookup>)null);
   }
}
