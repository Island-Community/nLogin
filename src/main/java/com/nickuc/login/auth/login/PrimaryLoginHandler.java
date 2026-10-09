package com.nickuc.login.auth.login;

import com.nickuc.login.platform.listener.SharedListenerContract;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PrimaryLoginHandler extends LocalLoginHandler<ResultSet> {
   private final PreparedStatement preparedStatement;

   private PrimaryLoginHandler(SharedListenerContract target, Connection input, PreparedStatement output, ResultSet context) {
      super(target, input, context, null);
      this.preparedStatement = output;
   }

   @Override
   public void close() {
      this.resolveObject().close();
      this.preparedStatement.close();
      this.sharedListenerContract.sendConnection(this.connection);
   }
}
