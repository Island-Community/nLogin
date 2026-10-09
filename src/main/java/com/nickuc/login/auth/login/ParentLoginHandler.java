package com.nickuc.login.auth.login;

import com.nickuc.login.platform.listener.InternalListenerContract;


public class ParentLoginHandler {
   private final Object object;
   private final SecureLoginGate secureLoginGate;
   private final InternalListenerContract internalListenerContract;

   private ParentLoginHandler(InternalListenerContract target, SecureLoginGate input, Object output) {
      this.internalListenerContract = target;
      this.secureLoginGate = input;
      this.object = output;
   }
}
