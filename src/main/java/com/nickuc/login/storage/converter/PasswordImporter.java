package com.nickuc.login.storage.converter;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.SafeLoginBarrier;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.password.PrimaryPasswordDao;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Properties;

public class PasswordImporter extends StoredSettingsGateway {
   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("name");
      String input = SafeLoginBarrier.computeMessage(target.getString("password"));
      this.updateMessage(this.activeName, input, null, null);
   }

   public PasswordImporter(PasswordStore target) {
      super(target, MessageOption.FAST_MESSAGEOPTION, "config.yml", "tlogin");
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      boolean input = target.a("backend.type", "SQLITE").equalsIgnoreCase("MYSQL");
      if (input) {
         int output = target.a("backend.port", 3306);
         String context = target.b("backend.host");
         String data = target.b("backend.username");
         String value = target.b("backend.password");
         String result = target.b("backend.database");
         this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
            this.passwordStore, PasswordRepository.computePasswordRepository(context, output, result, data, value, new Properties())
         );
      } else {
         File request = new File(this.retrieveFile(), "accounts.db");
         this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, request, new Properties());
      }
   }
}
