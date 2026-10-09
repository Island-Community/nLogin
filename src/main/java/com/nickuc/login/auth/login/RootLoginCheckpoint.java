package com.nickuc.login.auth.login;

import com.nickuc.login.model.PrivateLoginOption;

public class RootLoginCheckpoint {
   static {
      try {
         values[PrivateLoginOption.PRIVATE_LOGIN_OPTION.ordinal()] = 1;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[PrivateLoginOption.ACTIVE_PRIVATELOGINOPTION.ordinal()] = 2;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[PrivateLoginOption.PENDING_PRIVATELOGINOPTION.ordinal()] = 3;
      } catch (NoSuchFieldError target) {
      }
   }
}
