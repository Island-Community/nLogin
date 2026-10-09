package com.nickuc.login.auth.login;

import com.nickuc.login.model.PrivateLoginOption;

public class IncomingLoginCheckpoint {
   static {
      try {
         values[PrivateLoginOption.VERIFIED_PRIVATELOGINOPTION.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[PrivateLoginOption.AUTHENTICATED_PRIVATELOGINOPTION.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
