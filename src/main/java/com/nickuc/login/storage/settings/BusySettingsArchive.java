package com.nickuc.login.storage.settings;

import com.nickuc.login.auth.login.DeadLoginGate;
import com.nickuc.login.model.TightSpawnState;
import com.zaxxer.hikari.HikariConfig;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public abstract class BusySettingsArchive implements SharedListenerContract {
   private final SharedListenerContract sharedListenerContract;

   @Override
   public void sendConnection(Connection target) {
      this.sharedListenerContract.sendConnection(target);
   }

   public void dispatchProperties(Properties target, boolean input) {
      if (input) {
         target.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30L)));
      }
   }

   public BusySettingsArchive(
      DirectNoticeCatalog target, IndirectSessionHandler<?> input, PasswordRepository output, @Nullable Consumer<HikariConfig> context, TightSpawnState data
   ) {
      if (!input.fetchPasswordHashBridge().verifyState(new TightSenderAdapter[]{target.findInternalSpawnState()})) {
         throw new RuntimeException("Unable to load " + target.resolveMessage() + " driver dependency!");
      }

      Properties value = output.retrieveProperties();
      value.putIfAbsent("useUnicode", "true");
      value.putIfAbsent("characterEncoding", "utf8");
      switch (data) {
         case TIGHT_SPAWN_STATE:
            HikariConfig record = new HikariConfig();
            record.setPoolName(input.retrieveMessage().toLowerCase(Locale.ENGLISH) + "-hikaricp");
            this.processHikariConfig(record, output);
            record.setMaximumPoolSize(10);
            record.setMinimumIdle(10);
            record.setMaxLifetime(TimeUnit.MINUTES.toMillis(30L));
            record.setConnectionTimeout(TimeUnit.SECONDS.toMillis(5L));
            this.dispatchProperties(value, true);
            record.setDataSourceProperties(value);
            if (context != null) {
               context.accept(record);
            }

            ReadySettingsTable item = new ReadySettingsTable(record);
            this.sharedListenerContract = new LoginTable(target, item, null);
            break;
         case ACTIVE_TIGHTSPAWNSTATE:
            ClassLoader request = this.getClass().getClassLoader();

            Driver result;
            try {
               result = (Driver)request.loadClass(this.retrieveMessage()).newInstance();
            } catch (ClassNotFoundException source) {
               throw new RuntimeException(
                  "JDBC driver \"" + this.retrieveMessage() + "\" not found in current class loader " + request.getClass().getCanonicalName()
               );
            } catch (InstantiationException | IllegalAccessException entry) {
               throw new RuntimeException("JDBC driver \"" + this.retrieveMessage() + "\" instance cannot be created!", entry);
            }

            value.putIfAbsent("user", output.loadMessage());
            value.putIfAbsent("password", output.resolveMessage());
            this.dispatchProperties(value, false);
            this.savePasswordRepository(output);
            String response = this.loadMessage(output);
            this.sharedListenerContract = new DeadLoginGate(target, result, response, value, null);
            break;
         default:
            throw new UnsupportedOperationException("Unsupported mode! " + data);
      }

      this.updateTask();
   }

   static {
      try {
         Class.forName("org.slf4j.Logger");
      } catch (ClassNotFoundException target) {
         throw new RuntimeException("Missing SLF4J library for HikariCP!", target);
      }
   }

   public abstract String loadMessage(PasswordRepository target);

   @Override
   public void executeTask() {
      this.sharedListenerContract.executeTask();
   }

   public abstract String retrieveMessage();

   @Override
   public Connection resolveConnection() {
      return this.sharedListenerContract.resolveConnection();
   }

   public abstract void processHikariConfig(HikariConfig target, PasswordRepository input);

   @Override
   public DirectNoticeCatalog resolveDirectNoticeCatalog() {
      return this.sharedListenerContract.resolveDirectNoticeCatalog();
   }

   public void savePasswordRepository(PasswordRepository target) {
      try {
         Class.forName(this.retrieveMessage());
      } catch (ClassNotFoundException output) {
         throw new RuntimeException("Unable to load " + this.resolveDirectNoticeCatalog().resolveMessage() + " driver!", output);
      }
   }

   public void updateTask() {
      Enumeration target = DriverManager.getDrivers();

      while (target.hasMoreElements()) {
         Driver input = (Driver)target.nextElement();
         if (input.getClass().getName().equals(this.retrieveMessage())) {
            try {
               DriverManager.deregisterDriver(input);
            } catch (SQLException context) {
            }
         }
      }
   }
}
