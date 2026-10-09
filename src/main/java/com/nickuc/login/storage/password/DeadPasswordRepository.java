package com.nickuc.login.storage.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import java.io.File;
import java.sql.ResultSet;
import java.util.Properties;

public class DeadPasswordRepository extends StoredSettingsGateway {
   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("last_name");
      String input = target.getString("password");
      int output = target.getInt("hashing_algorithm");
      if (output != 7) {
         this.executeMessage(this.activeName, input, Integer.toString(output));
      }

      String context = target.getString("ip_address");
      this.updateMessage(this.activeName, input, context, null);
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      boolean input = target.d("mysql.enabled");
      if (input) {
         String output = target.b("mysql.host");
         String context = target.b("mysql.username");
         String data = target.b("mysql.password");
         String value = target.b("mysql.database");
         this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
            this.passwordStore,
            PasswordRepository.processPasswordRepository(output, value, context, data, new Properties(), DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount())
         );
      } else {
         File result = new File(this.retrieveFile(), "LoginSecurity.db");
         this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, result, new Properties());
      }
   }

   public DeadPasswordRepository(PasswordStore target) {
      super(target, MessageOption.ACTIVE_MESSAGEOPTION, "database.yml", "ls_players");
   }
}
