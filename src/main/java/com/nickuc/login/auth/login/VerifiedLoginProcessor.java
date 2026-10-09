package com.nickuc.login.auth.login;

import com.nickuc.login.account.LoudProxyState;

public class VerifiedLoginProcessor {
   static {
      try {
         values[LoudProxyState.PRIMARY_LOUDPROXYSTATE.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[LoudProxyState.MAIN_LOUDPROXYSTATE.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
