package com.nickuc.login.auth.login;

import com.nickuc.login.platform.proxy.SilentProxyState;

public class SecureLoginCheckpoint {
   static {
      try {
         values[SilentProxyState.SILENT_PROXY_STATE.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[SilentProxyState.ACTIVE_SILENTPROXYSTATE.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
