package com.nickuc.login.auth.login;

import com.nickuc.login.model.ProxyState;

public class TopLoginCheckpoint {
   static {
      try {
         values[ProxyState.PENDING_PROXYSTATE.ordinal()] = 1;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[ProxyState.ACTIVE_PROXYSTATE.ordinal()] = 2;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[ProxyState.PROXY_STATE.ordinal()] = 3;
      } catch (NoSuchFieldError target) {
      }
   }
}
