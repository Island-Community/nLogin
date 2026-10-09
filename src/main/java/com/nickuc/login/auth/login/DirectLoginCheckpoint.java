package com.nickuc.login.auth.login;

import com.nickuc.login.account.StrictMessageKind;

public class DirectLoginCheckpoint {
   static {
      try {
         values[StrictMessageKind.STRICT_MESSAGE_KIND.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
