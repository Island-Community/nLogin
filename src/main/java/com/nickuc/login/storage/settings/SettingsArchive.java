package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashLoader;
import java.io.File;
import java.sql.ResultSet;
import java.util.Properties;

public class SettingsArchive extends StoredSettingsGateway {
   @Override
   public void performSet(ResultSet target) {
      this.activeName = target.getString("player");
      String input = target.getString("senha");
      String output = target.getString("ip");
      this.updateMessage(this.activeName, "$MD5$" + input, output, null);
   }

   @Override
   public void updatePasswordHashLoader(PasswordHashLoader target) {
      int input = target.a("Config.SQL.Modo") == 2 ? 1 : 0;
      if (input != 0) {
         String output = target.b("Config.SQL.Host");
         String context = target.b("Config.SQL.DataBase");
         String data = target.b("Config.SQL.User");
         String value = target.b("Config.SQL.Pass");
         this.sharedListenerContract = PrimaryPasswordDao.loadPrimaryPasswordDao(
            this.passwordStore,
            PasswordRepository.processPasswordRepository(output, context, data, value, new Properties(), DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.loadCount())
         );
      } else {
         File result = new File(this.retrieveFile(), "registros.db");
         this.sharedListenerContract = LoginGateway.handleLoginGateway(this.passwordStore, result, new Properties());
      }
   }

   public SettingsArchive(PasswordStore target) {
      super(target, MessageOption.LIVE_MESSAGEOPTION, "config.yml", "tg_registros");
   }
}
