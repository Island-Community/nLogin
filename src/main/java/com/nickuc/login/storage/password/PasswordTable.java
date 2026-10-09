package com.nickuc.login.storage.password;

import com.nickuc.login.model.TightSpawnState;
import com.zaxxer.hikari.HikariConfig;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import java.util.function.Consumer;

public class PasswordTable extends BusySettingsArchive {
   @Override
   public void processHikariConfig(HikariConfig target, PasswordRepository input) {
      target.setDriverClassName(this.retrieveMessage());
      target.setJdbcUrl(this.loadMessage(input));
      target.setUsername(input.loadMessage());
      target.setPassword(input.resolveMessage());
   }

   public static PasswordTable buildPasswordTable(IndirectSessionHandler<?> instance, PasswordRepository target) {
      return resolvePasswordTable(instance, target, null);
   }

   public static PasswordTable loadPasswordTable(IndirectSessionHandler<?> instance, PasswordRepository target) {
      return new PasswordTable(instance, target, null, TightSpawnState.ACTIVE_TIGHTSPAWNSTATE);
   }

   @Override
   public String retrieveMessage() {
      return "org.mariadb.jdbc.Driver";
   }

   private PasswordTable(IndirectSessionHandler<?> target, PasswordRepository input, Consumer<HikariConfig> output, TightSpawnState context) {
      super(DirectNoticeCatalog.DIRECT_NOTICE_CATALOG, target, input, output, context);
   }

   public static PasswordTable resolvePasswordTable(IndirectSessionHandler<?> instance, PasswordRepository target, Consumer<HikariConfig> input) {
      return new PasswordTable(instance, target, input, TightSpawnState.TIGHT_SPAWN_STATE);
   }

   @Override
   public String loadMessage(PasswordRepository target) {
      return "jdbc:mariadb://" + target.fetchMessage() + ":" + target.findCount() + "/" + target.getMessage();
   }
}
