package com.nickuc.login.auth.login;

import com.nickuc.login.model.PrivateLoginOption;

public class UpstreamLoginCheckpoint {
   static {
      try {
         values[PrivateLoginOption.INTERNAL_PRIVATELOGINOPTION.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[PrivateLoginOption.UPSTREAM_PRIVATELOGINOPTION.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
