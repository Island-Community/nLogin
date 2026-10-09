package com.nickuc.login.auth.settings;

import com.nickuc.login.api.enums.ServerConnectType;

public class RootSettingsBarrier {
   static {
      try {
         values[ServerConnectType.WITH_LAST_SERVER.ordinal()] = 1;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[ServerConnectType.WITH_PLATFORM_SERVER.ordinal()] = 2;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[ServerConnectType.WITH_CONFIGURED_SERVER.ordinal()] = 3;
      } catch (NoSuchFieldError target) {
      }
   }
}
