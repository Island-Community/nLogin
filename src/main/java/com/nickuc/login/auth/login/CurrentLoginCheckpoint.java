package com.nickuc.login.auth.login;

import com.nickuc.login.platform.proxy.SilentProxyState;

public class CurrentLoginCheckpoint {
   static {
      try {
         values[SilentProxyState.PENDING_SILENTPROXYSTATE.ordinal()] = 1;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[SilentProxyState.SILENT_PROXY_STATE.ordinal()] = 2;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[SilentProxyState.ACTIVE_SILENTPROXYSTATE.ordinal()] = 3;
      } catch (NoSuchFieldError target) {
      }
   }
}
