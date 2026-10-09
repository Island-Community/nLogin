package com.nickuc.login.auth.login;

import com.nickuc.login.platform.listener.SharedListenerContract;
import java.sql.Connection;


public class PrivateLoginHandler implements AutoCloseable {
   private final Connection connection;
   public final SharedListenerContract sharedListenerContract;

   @Override
   public void close() {
      this.sharedListenerContract.sendConnection(this.connection);
   }

   public Connection retrieveConnection() {
      return this.connection;
   }

   private PrivateLoginHandler(SharedListenerContract target, Connection input) {
      this.sharedListenerContract = target;
      this.connection = input;
   }
}
