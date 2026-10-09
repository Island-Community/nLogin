package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.proxy.SilentProxyState;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

public class CurrentSettingsGateway extends InternalSettingsGateway {
   private static final Pattern pattern = Pattern.compile("/^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$/");

   @Override
   public void dispatchSet(ResultSet target) {
      String input = null;
      byte output = 0;

      try {
         input = target.getString("uuid");
         output = pattern.matcher(input).matches();
         String context = target.getString("pin");
         this.dispatchMessage(input, context, (boolean)output);
      } catch (Exception data) {
         PasswordHashContainer.processMessage(
            "[" + this.messageOption.getName() + "] " + (input == null ? "Unknown" : input + "'s") + " data could not be converted [uuid: " + output + "].", data
         );
      }
   }

   private void dispatchMessage(String target, String input, boolean output) {
      if (target != null && input != null) {
         UUID data = null;
         String context;
         if (output) {
            data = UUID.fromString(target);
            OfflinePlayer value = ((Server)this.passwordStore.b().c()).getOfflinePlayer(data);
            context = value.getName();
         } else {
            context = target;
         }

         if (context != null) {
            this.updateMessage(context, input, null, data);
         }
      }
   }

   @Override
   public void dispatchPasswordHashLoader(PasswordHashLoader target) {
      boolean input = target.d("MySQL.UseMySQL");
      if (input) {
         String output = target.b("MySQL.IP");
         String context = target.b("MySQL.DB-Name");
         String data = target.b("MySQL.Username");
         String value = target.b("MySQL.Password");
         this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
            this.passwordStore,
            PasswordRepository.processPasswordRepository(output, context, data, value, new Properties(), DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount())
         );
         this.activeName = "PinCodes";
      }
   }

   public CurrentSettingsGateway(PasswordStore target) {
      super(
         target, MessageOption.SECURE_MESSAGEOPTION, "config.yml", target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE
      );
   }

   @Override
   public void performPasswordHashLoader(PasswordHashLoader target) {
      PasswordHashLoader input = new PasswordHashLoader("codes.yml", this.retrieveFile());

      for (String data : input.<FileConfiguration>findObject().getKeys(false)) {
         boolean value = pattern.matcher(data).matches();

         try {
            String result = input.b(data);
            this.dispatchMessage(data, result, value);
         } catch (Exception entry) {
            PasswordHashContainer.processMessage(
               "[" + this.messageOption.getName() + "] " + (data == null ? "Unknown" : data + "'s") + " data could not be converted [uuid: " + value + "].",
               entry
            );
         } finally {
            this.pendingTimestamp++;
         }
      }
   }
}
