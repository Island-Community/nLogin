package com.nickuc.login.auth.login;

import com.nickuc.login.model.PrivateLoginOption;

public class StrictLoginCheckpoint {
   static {
      try {
         values[PrivateLoginOption.CACHED_PRIVATELOGINOPTION.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[PrivateLoginOption.STORED_PRIVATELOGINOPTION.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
