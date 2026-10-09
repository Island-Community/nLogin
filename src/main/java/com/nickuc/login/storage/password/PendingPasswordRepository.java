package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import org.json.JSONObject;
import java.sql.ResultSet;
import java.util.Properties;

public class PendingPasswordRepository extends StoredSettingsGateway {
   public PendingPasswordRepository(PasswordStore target) {
      super(target, MessageOption.CURRENT_MESSAGEOPTION, "config.yml", "lobbyusers");
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      String input = target.a("Mysql.Host", "localhost:3306");
      String output = target.b("Mysql.Database");
      String context = target.b("Mysql.Usuario");
      String data = target.b("Mysql.Senha");
      this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
         this.passwordStore,
         PasswordRepository.processPasswordRepository(input, output, context, data, new Properties(), DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount())
      );
   }

   @Override
   public void performSet(ResultSet target) {
      JSONObject input = new JSONObject(target.getString("json"));
      this.activeName = input.getString("username");
      String output = input.getString("password");
      this.updateMessage(this.activeName, "$MD5$" + output, null, null);
   }
}
