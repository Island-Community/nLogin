package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import org.json.JSONObject;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.function.Consumer;

public class QuickPasswordResolver extends StoredSettingsGateway {
   @Override
   public void performSet(ResultSet target) {
      JSONObject input = new JSONObject(target.getString("json"));
      this.activeName = input.getString("realName");
      long output = input.getLong("firstLogin");
      long data = input.has("lastLogin") ? input.getLong("lastLogin") : System.currentTimeMillis();
      boolean result = input.getBoolean("optionPremium");
      String request = input.has("ip") ? input.getString("ip") : null;
      String response = input.has("password") ? input.getString("password") : null;
      String source = "$SHA256$" + response;
      Consumer entry = context -> context.executeLong(output, data);
      if (result) {
         this.updateMessage(this.activeName, source, request, null, null, entry);
      } else {
         this.executeMessage(this.activeName, source, request, null, entry);
      }
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("Database.Tipo", "SQLITE");
      if ("MONGODB".equals(input)) {
         throw new UnsupportedOperationException("Conversão para MongoDB não suportada. Por favor, contate-nos: www.nickuc.com/discord ou support@nickuc.com");
      }

      boolean output = "SQLITE".equals(input);
      if (output) {
         File context = new File(this.retrieveFile(), "database.db");
         this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, context, new Properties());
      } else {
         String request = target.a("Database.IP", "localhost:3306");
         String data = target.b("Database.DB");
         String value = target.b("Database.User");
         String result = target.b("Database.Pass");
         this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
            this.passwordStore,
            PasswordRepository.processPasswordRepository(request, data, value, result, new Properties(), DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount())
         );
      }
   }

   public QuickPasswordResolver(PasswordStore target) {
      super(target, MessageOption.PENDING_MESSAGEOPTION, "config.yml", "dont.login");
   }
}
