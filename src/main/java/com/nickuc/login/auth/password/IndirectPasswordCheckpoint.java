package com.nickuc.login.auth.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.storage.login.LoginGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.settings.StoredSettingsGateway;
import java.io.File;
import java.sql.ResultSet;
import java.util.Properties;

public class IndirectPasswordCheckpoint extends StoredSettingsGateway {
   public IndirectPasswordCheckpoint(PasswordStore target) {
      super(target, MessageOption.PRIMARY_MESSAGEOPTION, "config.yml", "openlogin");
   }

   @Override
   public File retrieveFile() {
      return super.retrieveFile();
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      File input = new File(this.retrieveFile(), "accounts.db");
      this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, input, new Properties());
   }

   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("realname");
      String input = target.getString("password");
      String output = target.getString("address");
      long context = target.getLong("lastlogin");
      long value = target.getLong("regdate");
      this.executeMessage(this.activeName, input, output, null, contextValue -> contextValue.executeLong(value, context));
   }
}
