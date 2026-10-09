package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.premium.Pbkdf2Linker;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

public class MainPasswordDao extends InternalSettingsGateway {
   private static final Pattern pattern = Pattern.compile("\\$2a\\$[0-9]+\\$[A-Za-z0-9/+._-]{53}");

   @Override
   public void dispatchSet(ResultSet target) {
      String input = null;

      try {
         input = target.getString("uuid");
         String output = target.getString("password");
         this.saveMessage(input, null, output);
      } catch (Exception context) {
         PasswordHashContainer.processMessage(
            "[" + this.messageOption.getName() + "] " + (input == null ? "Unknown" : input + "'s") + " data could not be converted", context
         );
      }
   }

   public MainPasswordDao(PasswordStore target) {
      super(
         target, MessageOption.LINKED_MESSAGEOPTION, "config.yml", target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE
      );
   }

   @Override
   public void performPasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.yaml.file", "playerData.yml");
      PasswordHashLoader output = new PasswordHashLoader(input, this.retrieveFile());
      FileConfiguration context = output.findObject();
      Set data = context.getKeys(false);
      this.timestamp = data.size();

      for (String result : data) {
         try {
            String request;
            if (context.isConfigurationSection(result)) {
               request = context.getString(result + ".password");
            } else {
               request = context.getString(result);
            }

            String response = context.getString(result + ".name");
            this.saveMessage(result, response, request);
         } catch (Exception item) {
            PasswordHashContainer.processMessage(
               "[" + this.messageOption.getName() + "] " + (result == null ? "Unknown" : result + "'s") + " data could not be converted.", item
            );
         } finally {
            this.pendingTimestamp++;
         }
      }
   }

   @Override
   public void dispatchPasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.type", "yaml");
      switch (input.toLowerCase(Locale.ENGLISH)) {
         case "sqlite":
            String item = target.a("database.sqlite.database", "C:/sqlite/db/userlogin.db");
            File content = new File(item);
            content.getParentFile().mkdirs();
            this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, content, new Properties());
            this.activeName = target.a("database.sqlite.table", "player_data").replace("`", "");
            break;
         case "mysql":
            int record = target.a("database.mysql.port", 3306);
            String element = target.b("database.mysql.host");
            String payload = target.a("database.mysql.database", "userlogin_data");
            String holder = target.a("database.mysql.username", "root");
            String reference = target.a("database.mysql.password", "password");
            boolean subject = target.d("database.mysql.ssl");
            Properties option = new Properties();
            option.setProperty("useSSL", Boolean.toString(subject));
            this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
               this.passwordStore, PasswordRepository.computePasswordRepository(element, record, payload, holder, reference, option)
            );
            this.activeName = target.a("database.mysql.table", "player_data").replace("`", "");
            break;
         case "postgresql":
         case "postgres":
            int data = target.a("database.postgresql.port", 3306);
            String value = target.b("database.postgresql.host");
            String result = target.a("database.postgresql.database", "userlogin_data");
            String request = target.a("database.postgresql.username", "root");
            String response = target.a("database.postgresql.password", "password");
            boolean source = target.d("database.postgresql.ssl");
            Properties entry = new Properties();
            entry.setProperty("useSSL", Boolean.toString(source));
            this.sharedListenerContract = PasswordArchive.buildPasswordArchive(
               this.passwordStore, PasswordRepository.computePasswordRepository(value, data, result, request, response, entry)
            );
            this.activeName = target.a("database.postgresql.table", "player_data").replace("`", "");
         case "yaml":
            break;
         case "mongodb":
         case "mongo":
            throw new UnsupportedOperationException("MongoDB conversion not supported. Please contact us: www.nickuc.com/discord or support@nickuc.com");
         default:
            throw new UnsupportedOperationException("Unsupported database type! " + input);
      }
   }

   private void saveMessage(String target, String input, String output) {
      if (output != null) {
         UUID context = null;
         if (input == null && target != null) {
            context = UUID.fromString(target);
            OfflinePlayer data = ((Server)this.passwordStore.b().c()).getOfflinePlayer(context);
            input = data.getName();
         }

         if (input != null) {
            if (output.charAt(0) == 167) {
               try {
                  byte[] subject = Base64.getDecoder().decode(output.replaceAll("^§", ""));
                  ByteArrayInputStream value = new ByteArrayInputStream(subject);

                  try {
                     ObjectInputStream result = new ObjectInputStream(value);

                     try {
                        output = (String)result.readObject();
                     } finally {
                        if (Collections.singletonList(result).get(0) != null) {
                           result.close();
                        }
                     }
                  } finally {
                     if (Collections.singletonList(value).get(0) != null) {
                        value.close();
                     }
                  }
               } catch (Exception reference) {
                  PasswordHashContainer.processMessage("[" + this.messageOption.getName() + "] Unable to process password for " + input, reference);
               }
            } else if (!pattern.matcher(output).matches()) {
               output = Pbkdf2Linker.loadUpstreamSpawnState().computeMessage(output);
            }

            this.updateMessage(input, output, null, context);
         }
      }
   }
}
