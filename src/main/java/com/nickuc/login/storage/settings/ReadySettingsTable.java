package com.nickuc.login.storage.settings;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class ReadySettingsTable {
   private final HikariDataSource hikariDataSource;

   public Connection getConnection() {
      try {
         return this.hikariDataSource.getConnection();
      } catch (Exception input) {
         throw new SQLException("[Database] Unable to get a pooled connection.", input);
      }
   }

   public HikariDataSource resolveHikariDataSource() {
      return this.hikariDataSource;
   }

   public ReadySettingsTable(HikariConfig target) {
      this.hikariDataSource = new HikariDataSource(target);
   }

   public void executeTask() {
      if (this.hikariDataSource.isClosed()) {
         throw new IllegalStateException("Data source already closed!");
      }

      this.hikariDataSource.close();
   }
}
