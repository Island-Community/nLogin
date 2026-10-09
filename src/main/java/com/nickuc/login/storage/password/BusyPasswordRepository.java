package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.proxy.SilentProxyState;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Consumer;

public class BusyPasswordRepository extends InternalSettingsGateway {
   private final String[] names = new String[4];

   private void updatePasswordHashLoader(PasswordHashLoader target, String input) {
      String output = target.a("database." + input + ".name", "name");
      String context = target.a("database." + input + ".password", "password");
      String data = target.a("database." + input + ".ips", "ips");
      String value = target.a("database." + input + ".lastAction", "lastAction");
      this.names[0] = output;
      this.names[1] = context;
      this.names[2] = data;
      this.names[3] = value;
   }

   @Override
   public void performPasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.FLAT.filePath", "accounts.db");
      File output = new File(this.retrieveFile(), input);
      int context = 0;
      BufferedReader data = new BufferedReader(new FileReader(output));

      try {
         while (data.readLine() != null) {
            context++;
         }
      } catch (Throwable property) {
         try {
            data.close();
         } catch (Throwable reference) {
            property.addSuppressed(reference);
         }

         throw property;
      }

      data.close();
      this.timestamp = Math.max(0, context % 2 == 0 ? context / 2 : (context - 1) / 2);
      BufferedReader value = new BufferedReader(new FileReader(output));

      try {
         while ((attribute = value.readLine()) != null) {
            if (!attribute.isEmpty()) {
               String[] result = attribute.split("\\|");
               if (result.length >= 2 && !"name".equalsIgnoreCase(result[0])) {
                  String request = result[0];

                  try {
                     String response = result[1];
                     this.handleMessage(request, response);
                  } catch (Exception subject) {
                     PasswordHashContainer.processMessage("[" + this.messageOption.getName() + "] " + request + "'s data could not be converted.", subject);
                  } finally {
                     this.pendingTimestamp++;
                  }
               }
            }
         }
      } catch (Throwable setting) {
         try {
            value.close();
         } catch (Throwable holder) {
            setting.addSuppressed(holder);
         }

         throw setting;
      }

      value.close();
   }

   public BusyPasswordRepository(PasswordStore target) {
      super(
         target, MessageOption.DIRECT_MESSAGEOPTION, "config.yml", target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE
      );
   }

   @Override
   public void dispatchSet(ResultSet target) {
      String input = null;

      try {
         input = target.getString(this.names[0]);
         String output = target.getString(this.names[1]);
         this.handleMessage(input, output);
      } catch (Exception context) {
         PasswordHashContainer.processMessage(
            "[" + this.messageOption.getName() + "] " + (input == null ? "Unknown" : input + "'s") + " data could not be converted", context
         );
      }
   }

   @Override
   public void dispatchPasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.saveType", "FLAT");
      switch (input.toLowerCase(Locale.ENGLISH)) {
         case "config":
            throw new UnsupportedOperationException(
               "Conversion with save type CONFIG is not supported. Please contact us: www.nickuc.com/discord or support@nickuc.com"
            );
         case "flat":
            break;
         case "sqlite":
            String source = target.a("database.SQLITE.connection.path", "plugins/CrazyLogin/accounts.sqlite");
            File entry = new File(source);
            entry.getParentFile().mkdirs();
            this.updatePasswordHashLoader(target, input.toUpperCase(Locale.ENGLISH));
            this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, entry, new Properties());
            this.activeName = target.a("database.SQLITE.tableName", "CrazyLogin_accounts");
            break;
         case "mysql":
            int data = target.a("database.MYSQL.connection.port", 3306);
            String value = target.b("database.MYSQL.connection.host");
            String result = target.a("database.MYSQL.connection.dbname", "Crazy");
            String request = target.a("database.MYSQL.connection.user", "root");
            String response = target.a("database.MYSQL.connection.password", "");
            this.updatePasswordHashLoader(target, input.toUpperCase(Locale.ENGLISH));
            this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
               this.passwordStore, PasswordRepository.computePasswordRepository(value, data, result, request, response, new Properties())
            );
            this.activeName = target.a("database.MYSQL.tableName", "CrazyLogin_accounts");
            break;
         default:
            throw new UnsupportedOperationException("Unsupported database type! " + input);
      }
   }

   private void handleMessage(String target, String input) {
      if (input != null && !input.isEmpty()) {
         Consumer output = instance -> {
            instance.processTask();
            instance.handleUniqueId(null);
         };
         String[] context = input.split("\\$");
         if (context.length == 4 && "SHA".equals(context[1])) {
            this.executeMessage(target, input, null, null, output);
         } else {
            this.executeMessage(target, "$CRAZYLOGIN$" + target + "$" + input, null, null, output);
         }
      }
   }
}
