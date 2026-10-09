package com.nickuc.login.auth.login;

import com.nickuc.login.model.PrivateLoginOption;

public class LowLoginCheckpoint {
   static {
      try {
         values[PrivateLoginOption.ACTIVE_PRIVATELOGINOPTION.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[PrivateLoginOption.PENDING_PRIVATELOGINOPTION.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
