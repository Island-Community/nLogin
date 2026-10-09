package com.nickuc.login.auth.login;

import com.nickuc.login.model.PrivateLoginOption;

public class LinkedLoginCheckpoint {
   static {
      try {
         values[PrivateLoginOption.LOCAL_PRIVATELOGINOPTION.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[PrivateLoginOption.REMOTE_PRIVATELOGINOPTION.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
