package com.nickuc.login.auth.login;

import com.nickuc.login.model.PrivateLoginOption;

public class BusyLoginCheckpoint {
   static {
      try {
         values[PrivateLoginOption.CURRENT_PRIVATELOGINOPTION.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[PrivateLoginOption.PRIMARY_PRIVATELOGINOPTION.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
