package com.nickuc.login.auth.login;

import java.security.KeyPair;


public class InternalLoginHandler {
   private final KeyPair keyPair;

   private InternalLoginHandler(KeyPair target) {
      this.keyPair = target;
   }

   public KeyPair findKeyPair() {
      return this.keyPair;
   }
}
