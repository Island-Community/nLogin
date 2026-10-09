package com.nickuc.login.storage.password;

import com.nickuc.login.model.TightSpawnState;
import com.zaxxer.hikari.HikariConfig;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import java.util.Properties;
import java.util.function.Consumer;

public class PrimaryPasswordDao extends BusySettingsArchive {
   private PrimaryPasswordDao(IndirectSessionHandler<?> target, PasswordRepository input, Consumer<HikariConfig> output, TightSpawnState context) {
      super(DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG, target, input, output, context);
   }

   @Override
   public void processHikariConfig(HikariConfig target, PasswordRepository input) {
      target.setDriverClassName(this.retrieveMessage());
      target.setJdbcUrl(this.loadMessage(input));
      target.setUsername(input.loadMessage());
      target.setPassword(input.resolveMessage());
   }

   public static PrimaryPasswordDao loadPrimaryPasswordDao(IndirectSessionHandler<?> instance, PasswordRepository target) {
      return new PrimaryPasswordDao(instance, target, null, TightSpawnState.ACTIVE_TIGHTSPAWNSTATE);
   }

   public static PrimaryPasswordDao resolvePrimaryPasswordDao(IndirectSessionHandler<?> instance, PasswordRepository target, Consumer<HikariConfig> input) {
      return new PrimaryPasswordDao(instance, target, input, TightSpawnState.TIGHT_SPAWN_STATE);
   }

   @Override
   public String loadMessage(PasswordRepository target) {
      return "jdbc:mysql://" + target.fetchMessage() + ":" + target.findCount() + "/" + target.getMessage();
   }

   @Override
   public void dispatchProperties(Properties target, boolean input) {
      super.dispatchProperties(target, input);
      if (input) {
         target.putIfAbsent("cachePrepStmts", "true");
         target.putIfAbsent("prepStmtCacheSize", "250");
         target.putIfAbsent("prepStmtCacheSqlLimit", "2048");
         target.putIfAbsent("useServerPrepStmts", "true");
         target.putIfAbsent("useLocalSessionState", "true");
         target.putIfAbsent("rewriteBatchedStatements", "true");
         target.putIfAbsent("cacheResultSetMetadata", "true");
         target.putIfAbsent("cacheServerConfiguration", "true");
         target.putIfAbsent("elideSetAutoCommits", "true");
         target.putIfAbsent("maintainTimeStats", "false");
         target.putIfAbsent("alwaysSendSetIsolation", "false");
         target.putIfAbsent("cacheCallableStmts", "true");
      }
   }

   public static PrimaryPasswordDao buildPrimaryPasswordDao(IndirectSessionHandler<?> instance, PasswordRepository target) {
      return resolvePrimaryPasswordDao(instance, target, null);
   }

   @Override
   public String retrieveMessage() {
      return "com.mysql.cj.jdbc.Driver";
   }
}
