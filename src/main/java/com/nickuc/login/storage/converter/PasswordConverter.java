package com.nickuc.login.storage.converter;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordArchive;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class PasswordConverter extends StoredSettingsGateway {
   public PasswordConverter(PasswordStore target) {
      super(
         target,
         MessageOption.CACHED_MESSAGEOPTION,
         "config.yml",
         "lightlogin",
         target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE
      );
   }

   private static String processMessage(long instance) {
      StringBuilder input = new StringBuilder();

      for (int output = 3; output >= 0; output--) {
         input.append(instance >> output * 8 & 255L);
         if (output > 0) {
            input.append('.');
         }
      }

      return input.toString();
   }

   @Override
   public void performSet(ResultSet target) {
      UUID input = DeadLoginFlow.buildUniqueId(target.getString("uuid"));
      OfflinePlayer output = ((Server)this.passwordStore.b().c()).getOfflinePlayer(input);
      this.activeName = output.getName();
      String context = target.getString("password");
      byte[] data = Base64.getDecoder().decode(target.getString("salt"));
      String value = Base64.getEncoder().withoutPadding().encodeToString(data);
      String result = String.format("$%s$v=%s$m=%s,t=%s,p=%s$%s$%s", "argon2id", 19, 65336, 4, 4, value, context);
      String request = target.getString("email");
      String response = processMessage(target.getLong("last_ipv4"));
      long source = target.getLong("last_login");
      Consumer record = outputValue -> {
         outputValue.executeLong(source, source);
         outputValue.fetchQuickDiscordHandler().saveMessage(request);
      };
      this.executeMessage(this.activeName, result, response, input, record);
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("database.type", "sqlite").toLowerCase(Locale.ROOT);
      switch (input) {
         case "sqlite":
            this.sharedListenerContract = LoginGateway.handleLoginGateway(
               this.passwordStore, new File(target.retrieveFile().getParentFile(), "lightlogin.sqlite"), new Properties()
            );
            return;
         case "mysql":
         case "postgresql":
            int data = target.a("database.port", 3306);
            String value = target.a("database.address", "127.0.0.1");
            String result = target.a("database.username", "root");
            String request = target.a("database.password", "password");
            String response = target.a("database.db-name", "local");
            Properties source = new Properties();
            switch (input) {
               case "mysql":
                  this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
                     this.passwordStore, PasswordRepository.computePasswordRepository(value, data, response, result, request, source)
                  );
                  return;
               case "postgresql":
                  this.sharedListenerContract = PasswordArchive.buildPasswordArchive(
                     this.passwordStore, PasswordRepository.computePasswordRepository(value, data, response, result, request, source)
                  );
                  return;
               default:
                  return;
            }
         default:
            throw new UnsupportedOperationException("Unsupported database type! " + input);
      }
   }
}
