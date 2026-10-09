package com.nickuc.login.auth.login;

import com.nickuc.login.model.PrivateLoginOption;

public class CachedLoginCheckpoint {
   static {
      try {
         values[PrivateLoginOption.SHARED_PRIVATELOGINOPTION.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[PrivateLoginOption.PRIVATE_PRIVATELOGINOPTION.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
