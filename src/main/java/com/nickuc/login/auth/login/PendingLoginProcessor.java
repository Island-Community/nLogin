package com.nickuc.login.auth.login;

import com.nickuc.login.model.SecondaryPlatformCatalog;

public class PendingLoginProcessor {
   static {
      try {
         values[SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[SecondaryPlatformCatalog.PENDING_SECONDARYPLATFORMCATALOG.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
