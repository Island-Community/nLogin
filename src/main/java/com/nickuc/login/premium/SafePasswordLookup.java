package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.LinkedSpawnOption;
import com.nickuc.login.model.PlatformState;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
import com.nickuc.login.storage.password.PasswordArchive;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PasswordTable;
import com.nickuc.login.storage.login.PrimaryLoginSource;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

public class SafePasswordLookup extends StoredSettingsGateway {
   public static byte[] buildPayload(InputStream instance, int target) {
      byte[] input = new byte[target];
      int output = instance.read(input);
      if (output != target) {
         throw new IllegalArgumentException("Wanted to read " + target + " bytes but was able to read only " + output);
      } else {
         return input;
      }
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("name");
      byte[] input = target.getBytes("password");
      if (input.length != 0) {
         String output;
         try {
            DataInputStream context = new DataInputStream(new ByteArrayInputStream(input));

            label49: {
               try {
                  int data = Math.toIntExact(context.readLong());
                  PlatformState value = PlatformState.resolvePlatformState(data);
                  if (value != null) {
                     switch (value) {
                        case MAIN_PLATFORMSTATE:
                        case PLATFORM_STATE:
                        case ACTIVE_PLATFORMSTATE:
                        case PENDING_PLATFORMSTATE:
                           output = new String(buildPayload(context, context.available()), StandardCharsets.UTF_8);
                           break label49;
                        case CURRENT_PLATFORMSTATE:
                        case PRIMARY_PLATFORMSTATE:
                           String content = new String(buildPayload(context, context.available()), StandardCharsets.UTF_8);
                           output = "$" + value.name + "$" + content;
                           break label49;
                        case REMOTE_PLATFORMSTATE:
                        case CACHED_PLATFORMSTATE:
                           String element = context.readUTF();
                           String request = context.readUTF();
                           output = "$" + value.name + "$" + element + "$" + request;
                           break label49;
                        case LOCAL_PLATFORMSTATE:
                        case STORED_PLATFORMSTATE:
                           context.reset();
                           String result = Base64.getEncoder().withoutPadding().encodeToString(buildPayload(context, context.available()));
                           output = "$BARONESS$" + result;
                           break label49;
                        default:
                           throw new UnsupportedOperationException("Missing implementation for algorithm " + value + "!");
                     }
                  }

                  this.executeMessage(this.activeName, input.length + " bytes", Integer.toString(data));
               } catch (Throwable source) {
                  try {
                     context.close();
                  } catch (Throwable response) {
                     source.addSuppressed(response);
                  }

                  throw source;
               }

               context.close();
               return;
            }

            context.close();
         } catch (IOException entry) {
            throw new IllegalArgumentException("Unable to decode password data for " + this.activeName);
         }

         boolean record = target.getBoolean("premium");
         UUID item = DeadLoginFlow.buildUniqueId(target.getString("uuid"));
         if (record) {
            this.executeMessage(this.activeName, output, null, item, (UUID)null);
         } else {
            this.updateMessage(this.activeName, output, null, item);
         }
      }
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.type", "h2").toLowerCase(Locale.ROOT);
      File output = new File(this.retrieveFile(), "internal");
      PasswordHashLoader context = new PasswordHashLoader("advanced.yml", new File(this.retrieveFile() + File.separator + "config"), false);
      context.hasState(true);
      switch (input) {
         case "h2":
            this.sharedListenerContract = PrimaryLoginSource.createPrimaryLoginSource(
               this.passwordStore,
               LinkedSpawnOption.PENDING_LINKEDSPAWNOPTION,
               new File(output, "H2" + File.separator + "BaronessAuth"),
               new Properties(),
               "sa",
               null
            );
            return;
         case "mysql":
         case "mariadb":
         case "postgresql":
            String result = target.a("database.address", "localhost");
            String request = target.a("database.username", "user");
            String response = target.a("database.password", "password");
            String source = target.a("database.database", "mydatabase");
            List entry = context.processCollection("connection-args");
            int record = target.a("database.port", 0);
            if (record > 0 && record < 65535) {
               result = result + ":" + record;
            }

            Properties item = new Properties();
            entry.forEach(targetValue -> {
               String[] inputValue = targetValue.split("=");
               if (inputValue.length == 2) {
                  item.setProperty(inputValue[0], inputValue[1]);
               }
            });
            switch (input) {
               case "mysql":
                  this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(result, source, request, response, item, DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount())
                  );
                  return;
               case "mariadb":
                  this.sharedListenerContract = PasswordTable.loadPasswordTable(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(result, source, request, response, item, DirectNoticeCatalog.DIRECT_NOTICE_CATALOG.loadCount())
                  );
                  return;
               case "postgresql":
                  this.sharedListenerContract = PasswordArchive.buildPasswordArchive(
                     this.passwordStore,
                     PasswordRepository.processPasswordRepository(result, source, request, response, item, DirectNoticeCatalog.PENDING_DIRECTNOTICECATALOG.loadCount())
                  );
                  return;
               default:
                  return;
            }
         default:
            throw new UnsupportedOperationException("Unsupported database type! " + input);
      }
   }

   public SafePasswordLookup(PasswordStore target) {
      super(target, MessageOption.LOCAL_MESSAGEOPTION, "general.yml", "baronessauth_profiles");
   }

   @Override
   public PasswordHashLoader computePasswordHashLoader(String target) {
      PasswordHashLoader input = new PasswordHashLoader(target, new File(this.retrieveFile() + File.separator + "config"), false);
      input.hasState(true);
      return input;
   }
}
