package com.nickuc.login.auth.login;

import com.nickuc.login.platform.listener.SharedListenerContract;
import java.sql.Connection;

public abstract class LocalLoginHandler<T> extends StrictLoginHandler<T> implements AutoCloseable {
   public final Connection connection;
   public final SharedListenerContract sharedListenerContract;

   private LocalLoginHandler(SharedListenerContract target, Connection input, T output) {
      super(output, null);
      this.sharedListenerContract = target;
      this.connection = input;
   }
}
