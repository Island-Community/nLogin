package com.nickuc.login.auth.login;

import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;


public class DeadLoginGate implements SharedListenerContract {
   private final String name;
   private final Driver driver;
   private final Properties properties;
   private final DirectNoticeCatalog directNoticeCatalog;

   private DeadLoginGate(DirectNoticeCatalog target, Driver input, String output, Properties context) {
      this.directNoticeCatalog = target;
      this.driver = input;
      this.name = output;
      this.properties = context;
   }

   @Override
   public void executeTask() {
   }

   @Override
   public Connection resolveConnection() {
      return this.driver.connect(this.name, this.properties);
   }

   @Override
   public void sendConnection(Connection target) {
      target.close();
   }

   @Override
   public DirectNoticeCatalog resolveDirectNoticeCatalog() {
      return this.directNoticeCatalog;
   }
}
