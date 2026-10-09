package com.nickuc.login.auth.login;

import com.nickuc.login.model.StrictPlatformCatalog;

public class CachedLoginGate {
   static {
      try {
         values[StrictPlatformCatalog.STRICT_PLATFORM_CATALOG.ordinal()] = 1;
      } catch (NoSuchFieldError data) {
      }

      try {
         values[StrictPlatformCatalog.ACTIVE_STRICTPLATFORMCATALOG.ordinal()] = 2;
      } catch (NoSuchFieldError context) {
      }

      try {
         values[StrictPlatformCatalog.PENDING_STRICTPLATFORMCATALOG.ordinal()] = 3;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[StrictPlatformCatalog.CURRENT_STRICTPLATFORMCATALOG.ordinal()] = 4;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[StrictPlatformCatalog.PRIMARY_STRICTPLATFORMCATALOG.ordinal()] = 5;
      } catch (NoSuchFieldError target) {
      }
   }
}
