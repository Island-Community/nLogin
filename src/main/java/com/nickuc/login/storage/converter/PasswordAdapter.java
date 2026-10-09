package com.nickuc.login.storage.converter;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PasswordTable;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;

public class PasswordAdapter extends StoredSettingsGateway {
   private final String[] names = new String[6];

   public PasswordAdapter(PasswordStore target) {
      super(target, MessageOption.MESSAGE_OPTION, "config.yml");
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("DataSource.backend", "SQLITE").toUpperCase(Locale.ENGLISH);
      String output = target.a("DataSource.mySQLDatabase", "authme");
      switch (input) {
         case "SQLITE":
            File value = new File(this.retrieveFile(), output + ".db");
            this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, value, new Properties());
            break;
         case "MYSQL":
         case "MARIADB":
            int result = target.a("DataSource.mySQLPort", 3306);
            String request = target.b("DataSource.mySQLHost");
            String response = target.b("DataSource.mySQLUsername");
            String source = target.b("DataSource.mySQLPassword");
            boolean entry = target.d("DataSource.mySQLUseSSL");
            boolean record = target.d("DataSource.mySQLCheckServerCertificate");
            boolean item = target.d("DataSource.mySQLAllowPublicKeyRetrieval");
            Properties element = new Properties();
            element.setProperty("useSSL", Boolean.toString(entry));
            if (!record) {
               element.setProperty("verifyServerCertificate", Boolean.toString(false));
            }

            if (item) {
               element.setProperty("allowPublicKeyRetrieval", Boolean.toString(true));
            }

            PasswordRepository content = PasswordRepository.computePasswordRepository(request, result, output, response, source, element);
            this.sharedListenerContract = "MARIADB".equals(input)
               ? PasswordTable.loadPasswordTable(this.passwordStore, content)
               : PrimaryPasswordDao.loadPrimaryPasswordDao(this.passwordStore, content);
            break;
         default:
            throw new IllegalArgumentException("Unsupported backend type: " + input);
      }

      this.pendingName = target.b("DataSource.mySQLTablename");
      this.names[0] = target.b("DataSource.mySQLRealName");
      this.names[1] = target.b("DataSource.mySQLColumnPassword");
      this.names[2] = target.b("DataSource.mySQLColumnIp");
      this.names[3] = target.b("DataSource.mySQLColumnLastLogin");
      this.names[4] = target.b("DataSource.mySQLColumnRegisterDate");
      this.names[5] = target.b("DataSource.mySQLColumnEmail");
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString(this.names[0]);
      String input = target.getString(this.names[1]);
      String output = target.getString(this.names[2]);
      long context = target.getLong(this.names[3]);
      long value = target.getLong(this.names[4]);
      String request = target.getString(this.names[5]);
      this.executeMessage(this.activeName, input, output, null, data -> {
         if (request != null) {
            data.fetchQuickDiscordHandler().saveMessage(request);
         }

         data.executeLong(value, context);
      });
   }
}
