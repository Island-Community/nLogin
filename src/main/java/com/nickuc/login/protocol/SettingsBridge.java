package com.nickuc.login.protocol;

import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.nickuc.login.platform.proxy.SilentProxyState;

public class SettingsBridge {
   static {
      try {
         activeValues[ConnectionState.CONFIGURATION.ordinal()] = 1;
      } catch (NoSuchFieldError data) {
      }

      try {
         activeValues[ConnectionState.PLAY.ordinal()] = 2;
      } catch (NoSuchFieldError context) {
      }

      values = new int[SilentProxyState.values().length];

      try {
         values[SilentProxyState.SILENT_PROXY_STATE.ordinal()] = 1;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[SilentProxyState.PENDING_SILENTPROXYSTATE.ordinal()] = 2;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[SilentProxyState.ACTIVE_SILENTPROXYSTATE.ordinal()] = 3;
      } catch (NoSuchFieldError target) {
      }
   }
}
