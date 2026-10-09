package com.nickuc.login.premium;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.login.OpenLoginBarrier;
import com.nickuc.login.auth.login.PrivateLoginCheckpoint;
import com.nickuc.login.auth.login.StoredLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.OutgoingPlatformCatalog;
import com.nickuc.login.storage.platform.IncomingPlatformCatalog;
import com.nickuc.login.storage.password.LocalPasswordTable;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordArchive;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import com.nickuc.login.storage.converter.OpenPasswordTranslator;
import com.nickuc.login.storage.converter.PasswordTranslator;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;

public class RootPasswordLookup extends StoredSettingsGateway {
   private Boolean value;
   private Boolean activeValue;

   @Override
   public File retrieveFile() {
      File target = super.retrieveFile();
      if (!target.exists()) {
         target = new File(this.passwordStore.resolveFile().getParentFile(), this.messageOption.resolveMessage().toLowerCase(Locale.ENGLISH));
      }

      return target;
   }

   public RootPasswordLookup(PasswordStore target) {
      super(target, MessageOption.VERIFIED_MESSAGEOPTION, "config.conf", "librepremium_data");
   }

   @Override
   public PasswordHashLoader computePasswordHashLoader(String target) {
      return null;
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      File input = new File(this.retrieveFile(), this.name);
      OpenLoginBarrier output = StoredLoginFlow.loadOpenLoginBarrier(input);

      try {
         EnumMap context = new EnumMap<>(IncomingPlatformCatalog.class);
         PasswordTranslator data = null;
         IncomingPlatformCatalog value = null;
         byte request = 0;

         String result;
         while ((result = output.findMessage()) != null) {
            String response = result.trim();
            if (data != null) {
               if (response.equals("}")) {
                  OpenPasswordTranslator source = data.fetchOpenPasswordTranslator();
                  context.put(OpenPasswordTranslator.loadIncomingPlatformCatalog(source), source);
                  data = null;
               } else if (response.startsWith("database=")) {
                  data.resolvePasswordTranslator(this.resolveMessage(response, "database="));
               } else if (response.startsWith("host=")) {
                  data.resolvePasswordTranslatorForPasswordTranslator(this.resolveMessage(response, "host="));
               } else if (response.startsWith("password=")) {
                  data.createPasswordTranslator(this.resolveMessage(response, "password="));
               } else if (response.startsWith("port=")) {
                  data.buildPasswordTranslator(PrivateLoginCheckpoint.handleInteger(this.resolveMessage(response, "port="), 3306));
               } else if (response.startsWith("user=")) {
                  data.processPasswordTranslator(this.resolveMessage(response, "user="));
               } else if (response.startsWith("path=")) {
                  data.loadPasswordTranslator(this.resolveMessage(response, "path="));
               }
            } else if (response.equals("database {")) {
               request = 1;
            } else if (request != 0) {
               if (response.startsWith("type=")) {
                  String option = this.resolveMessage(response, "type=");
                  if ((value = IncomingPlatformCatalog.loadIncomingPlatformCatalog(option)) == null) {
                     throw new UnsupportedOperationException("Unsupported database type! \"" + option + "\"");
                  }

                  request = 0;
               } else {
                  IncomingPlatformCatalog setting = IncomingPlatformCatalog.computeIncomingPlatformCatalog(response);
                  if (setting != null) {
                     data = OpenPasswordTranslator.getPasswordTranslator().processPasswordTranslator(setting);
                  }
               }
            }
         }

         if (value == null) {
            throw new IllegalStateException("Cannot load the database type from LibreLogin config!");
         }

         OpenPasswordTranslator subject = (OpenPasswordTranslator)context.get(value);
         if (subject == null) {
            throw new IllegalStateException("Cannot load the database config (for \"" + value + "\") from LibreLogin config!");
         }

         switch (value) {
            case INCOMING_PLATFORM_CATALOG:
            case ACTIVE_INCOMINGPLATFORMCATALOG:
               int property = OpenPasswordTranslator.computeCount(subject);
               String entry = OpenPasswordTranslator.resolveMessage(subject);
               String record = OpenPasswordTranslator.computeMessage(subject);
               String item = OpenPasswordTranslator.createMessage(subject);
               String element = OpenPasswordTranslator.computeMessageForMessage(subject);
               this.sharedListenerContract = value == IncomingPlatformCatalog.INCOMING_PLATFORM_CATALOG
                  ? PrimaryPasswordDao.loadPrimaryPasswordDao(
                     this.passwordStore, PasswordRepository.computePasswordRepository(entry, property, record, item, element, new Properties())
                  )
                  : PasswordArchive.buildPasswordArchive(
                     this.passwordStore, PasswordRepository.computePasswordRepository(entry, property, record, item, element, new Properties())
                  );
               break;
            case PENDING_INCOMINGPLATFORMCATALOG:
               String content = OpenPasswordTranslator.buildMessage(subject);
               if (content == null) {
                  throw new IllegalStateException("Cannot load database path (for \"" + value + "\") from LibreLogin config!");
               }

               File payload = new File(this.retrieveFile(), content);
               this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, payload, new Properties());
         }
      } catch (Throwable reference) {
         if (output != null) {
            try {
               output.close();
            } catch (Throwable holder) {
               reference.addSuppressed(holder);
            }
         }

         throw reference;
      }

      if (output != null) {
         output.close();
      }
   }

   private String resolveMessage(String target, String input) {
      if (target.length() == input.length()) {
         return "";
      }

      String output = target.substring(input.length());
      char context = output.charAt(0);
      char data = output.charAt(output.length() - 1);
      if (context == '"' && data == '"' || context == '\'' && data == '\'') {
         switch (output.length()) {
            case 1:
               return output;
            case 2:
               return "";
            default:
               return output.substring(1, output.length() - 1);
         }
      } else {
         return output;
      }
   }

   @Override
   public void performSet(ResultSet target) {
      if (this.activeValue == null) {
         this.activeValue = LocalPasswordTable.isState(target, "salt");
      }

      if (this.value == null) {
         this.value = LocalPasswordTable.isState(target, "algo");
      }

      this.activeName = target.getString("last_nickname");
      if (this.activeName != null) {
         UUID input = DeadLoginFlow.buildUniqueId(target.getString("uuid"));
         UUID output = DeadLoginFlow.buildUniqueId(target.getString("premium_uuid"));
         String context = target.getString("hashed_password");
         String data;
         if (context == null || context.isEmpty()) {
            data = null;
         } else if (this.activeValue) {
            String result = target.getString("salt");
            OutgoingPlatformCatalog value;
            if (this.value) {
               value = OutgoingPlatformCatalog.loadOutgoingPlatformCatalog(target.getString("algo"));
            } else {
               switch (context.length()) {
                  case 64:
                     value = OutgoingPlatformCatalog.OUTGOING_PLATFORM_CATALOG;
                     break;
                  case 128:
                     value = OutgoingPlatformCatalog.ACTIVE_OUTGOINGPLATFORMCATALOG;
                     break;
                  default:
                     value = null;
               }
            }

            if (value == null) {
               this.executeMessage(this.activeName, context, null);
               return;
            }

            switch (value) {
               case OUTGOING_PLATFORM_CATALOG:
               case ACTIVE_OUTGOINGPLATFORMCATALOG:
                  data = "$" + OutgoingPlatformCatalog.handleMessage(value) + "$" + context + "$" + result;
                  break;
               case PENDING_OUTGOINGPLATFORMCATALOG:
               case CURRENT_OUTGOINGPLATFORMCATALOG:
                  String[] subject = context.split("\\$");
                  data = "$" + OutgoingPlatformCatalog.handleMessage(value) + "$" + subject[0] + "$" + result + subject[1];
                  break;
               case PRIMARY_OUTGOINGPLATFORMCATALOG:
                  String[] request = context.split("\\$");
                  String[] response = request[0].split(",");
                  int source = Integer.parseInt(response[0]);
                  int entry = Integer.parseInt(response[1]);
                  int record = Integer.parseInt(response[2]);
                  byte item = 1;
                  byte[] element = Base64.getDecoder().decode(result);
                  byte[] content = Base64.getDecoder().decode(request[1]);
                  data = String.format(
                     "$argon2id$v=%s$m=%s,t=%s,p=%s$%s$%s",
                     source,
                     record,
                     entry,
                     Integer.valueOf(item),
                     Base64.getEncoder().withoutPadding().encodeToString(element),
                     Base64.getEncoder().withoutPadding().encodeToString(content)
                  );
                  break;
               default:
                  throw new UnsupportedOperationException("Missing implementation for algorithm " + value + "!");
            }
         } else {
            int option = context.charAt(0) == '$' ? 1 : 0;
            String[] property = context.split("\\$");
            if (property.length != 3 && property.length != 4) {
               this.executeMessage(this.activeName, context, null);
               return;
            }

            OutgoingPlatformCatalog payload = OutgoingPlatformCatalog.loadOutgoingPlatformCatalog(property[option].toUpperCase(Locale.ENGLISH));
            if (payload == null) {
               this.executeMessage(this.activeName, context, null);
               return;
            }

            switch (payload) {
               case OUTGOING_PLATFORM_CATALOG:
               case ACTIVE_OUTGOINGPLATFORMCATALOG:
                  data = "$" + OutgoingPlatformCatalog.handleMessage(payload) + "$" + property[option + 2] + "$" + property[option + 1];
                  break;
               case PENDING_OUTGOINGPLATFORMCATALOG:
               case CURRENT_OUTGOINGPLATFORMCATALOG:
                  data = "$" + OutgoingPlatformCatalog.handleMessage(payload) + context.substring("BCRYPT".length() + option + 1);
                  break;
               default:
                  throw new UnsupportedOperationException("Missing implementation for algorithm " + payload + "!");
            }
         }

         Timestamp holder = target.getTimestamp("joined");
         Timestamp reference = target.getTimestamp("last_seen");
         Consumer setting = inputValue -> inputValue.executeLong(holder != null ? holder.getTime() : null, reference != null ? reference.getTime() : null);
         if (output != null && output.version() != 4) {
            output = null;
         }

         if (output != null) {
            this.updateMessage(this.activeName, data, null, input, output, setting);
         } else {
            this.executeMessage(this.activeName, data, null, input, setting);
         }
      }
   }
}
