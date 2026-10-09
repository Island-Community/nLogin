package com.nickuc.login.storage.converter;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import org.json.JSONObject;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Properties;

public class Sha256Adapter extends StoredSettingsGateway {
   @Override
   public void performSet(ResultSet target) {
      JSONObject input = new JSONObject(target.getString("json"));
      this.activeName = input.getString("jogador");
      String output = input.getString("senha");
      if (!output.startsWith("$2a") && !output.startsWith("$2y")) {
         int context = output.length();
         switch (context) {
            case 32:
               output = "$MD5$" + output;
               break;
            case 64:
               output = "$SHA256$" + output;
               break;
            case 128:
               output = "$SHA512$" + output;
               break;
            default:
               this.executeMessage(this.activeName, output, null);
               return;
         }
      }

      String response = input.getString("ip");
      Long data = null;
      Long value = null;
      if (input.has("regdate")) {
         data = input.getLong("regdate");
      }

      if (input.has("lastlogin")) {
         value = input.getLong("lastlogin");
      }

      Long result = data;
      Long request = value;
      this.executeMessage(this.activeName, output, response, null, inputValue -> {
         if (result != null) {
            inputValue.executeLong(result, request);
         }
      });
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      boolean input = target.d("MySQL.ativado");
      if (input) {
         int output = target.a("MySQL.port", 3306);
         String context = target.a("MySQL.host", "localhost");
         String data = target.a("MySQL.db", "lobby");
         String value = target.a("MySQL.user", "root");
         String result = target.a("MySQL.pass", "");
         this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
            this.passwordStore, PasswordRepository.computePasswordRepository(context, output, data, value, result, new Properties())
         );
      } else {
         File request = new File(this.retrieveFile(), "dados.db");
         this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, request, new Properties());
      }
   }

   public Sha256Adapter(PasswordStore target) {
      super(target, MessageOption.ROOT_MESSAGEOPTION, "config.yml", "storm_login");
   }
}
