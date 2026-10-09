package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class LoudPasswordResolver extends StoredSettingsGateway {
   private boolean enabled = false;

   public LoudPasswordResolver(PasswordStore target) {
      super(target, MessageOption.INCOMING_MESSAGEOPTION, "config.yml", "aegisauth_users");
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("name");
      String input = target.getString("uuid");
      if (!input.isEmpty() && input.charAt(input.length() - 1) == '.') {
         input = input.substring(0, input.length() - 1);
      }

      UUID output = DeadLoginFlow.buildUniqueId(input);
      boolean context = target.getBoolean("premium");
      String data = target.getString("onlineId");
      if (!data.isEmpty() && data.charAt(data.length() - 1) == '.') {
         data = data.substring(0, data.length() - 1);
      }

      UUID value = context ? DeadLoginFlow.buildUniqueId(data) : null;
      String result = target.getBoolean("registered") ? target.getString("password") : null;
      if (result != null && !result.startsWith("$2a")) {
         if (!result.startsWith("$SHA$")) {
            this.executeMessage(this.activeName, result, null);
            return;
         }

         String[] request = result.split("\\$");
         String response = request[2];
         String source = request[3];
         result = "$SHA256$" + source + "$" + response;
      }

      if (this.enabled && context && value != null) {
         output = value;
      }

      if (context) {
         this.executeMessage(this.activeName, result, null, output, value);
      } else {
         this.updateMessage(this.activeName, result, null, output);
      }
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("auth.mysql.type", "mysql").toUpperCase(Locale.ENGLISH);
      switch (input) {
         case "SQLITE":
            File data = new File(this.retrieveFile(), "auth_database.db");
            this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, data, new Properties());
            break;
         case "MYSQL":
            int value = target.a("auth.mysql.port", 3306);
            String result = target.b("auth.mysql.hostname");
            String request = target.b("auth.mysql.database");
            String response = target.b("auth.mysql.user");
            String source = target.b("auth.mysql.password");
            boolean entry = target.d("auth.mysql.use-ssl");
            Properties record = new Properties();
            record.put("useSSL", Boolean.toString(entry));
            this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
               this.passwordStore, PasswordRepository.computePasswordRepository(result, value, request, response, source, record)
            );
      }

      this.enabled = target.d("aegis-settings.online-uuids-support");
   }

   @Override
   public File retrieveFile() {
      return this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.ACTIVE_SILENTPROXYSTATE
         ? new File(this.passwordStore.resolveFile().getParentFile().getParentFile(), "Aegis")
         : super.retrieveFile();
   }
}
