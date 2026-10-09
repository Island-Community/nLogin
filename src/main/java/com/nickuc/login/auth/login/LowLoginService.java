package com.nickuc.login.auth.login;

import com.nickuc.login.platform.listener.SharedListenerContract;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class LowLoginService extends LocalLoginHandler<PreparedStatement> {
   private LowLoginService(SharedListenerContract target, Connection input, PreparedStatement output) {
      super(target, input, output, null);
   }

   @Override
   public void close() {
      this.resolveObject().close();
      this.sharedListenerContract.sendConnection(this.connection);
   }
}
