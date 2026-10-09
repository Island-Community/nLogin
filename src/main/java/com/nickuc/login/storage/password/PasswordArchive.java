package com.nickuc.login.storage.password;

import com.nickuc.login.model.TightSpawnState;
import com.zaxxer.hikari.HikariConfig;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import java.util.Properties;
import java.util.function.Consumer;

public class PasswordArchive extends BusySettingsArchive {
   @Override
   public String retrieveMessage() {
      return "org.postgresql.Driver";
   }

   @Override
   public void processHikariConfig(HikariConfig target, PasswordRepository input) {
      target.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
      target.addDataSourceProperty("serverName", input.fetchMessage());
      target.addDataSourceProperty("portNumber", Integer.toString(input.findCount()));
      target.addDataSourceProperty("databaseName", input.getMessage());
      target.addDataSourceProperty("user", input.loadMessage());
      target.addDataSourceProperty("password", input.resolveMessage());
   }

   public static PasswordArchive buildPasswordArchive(IndirectSessionHandler<?> instance, PasswordRepository target) {
      return new PasswordArchive(instance, target, null, TightSpawnState.ACTIVE_TIGHTSPAWNSTATE);
   }

   @Override
   public void dispatchProperties(Properties target, boolean input) {
      super.dispatchProperties(target, input);
      target.remove("useUnicode");
      target.remove("characterEncoding");
   }

   public static PasswordArchive processPasswordArchive(IndirectSessionHandler<?> instance, PasswordRepository target) {
      return computePasswordArchive(instance, target, null);
   }

   @Override
   public String loadMessage(PasswordRepository target) {
      return "jdbc:postgresql://" + target.fetchMessage() + ":" + target.findCount() + "/" + target.getMessage();
   }

   private PasswordArchive(IndirectSessionHandler<?> target, PasswordRepository input, Consumer<HikariConfig> output, TightSpawnState context) {
      super(DirectNoticeCatalog.PENDING_DIRECTNOTICECATALOG, target, input, output, context);
   }

   public static PasswordArchive computePasswordArchive(IndirectSessionHandler<?> instance, PasswordRepository target, Consumer<HikariConfig> input) {
      return new PasswordArchive(instance, target, input, TightSpawnState.TIGHT_SPAWN_STATE);
   }
}
