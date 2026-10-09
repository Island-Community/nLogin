package com.nickuc.login.auth.login;

import com.nickuc.login.platform.listener.SharedListenerContract;
import java.sql.Connection;


public abstract class NestedLoginHandler implements SharedListenerContract {
   private Connection connection;

   @Override
   public synchronized void executeTask() {
      if (this.connection != null) {
         this.connection.close();
         this.connection = null;
      }
   }

   @Override
   public void sendConnection(Connection target) {
   }

   @Override
   public synchronized Connection resolveConnection() {
      if (this.connection == null || this.connection.isClosed()) {
         this.connection = this.fetchConnection();
      }

      return this.connection;
   }

   public abstract Connection fetchConnection();
}
