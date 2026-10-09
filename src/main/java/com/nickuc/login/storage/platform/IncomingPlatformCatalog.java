package com.nickuc.login.storage.platform;

import java.util.Locale;
import javax.annotation.Nullable;

public enum IncomingPlatformCatalog {
   INCOMING_PLATFORM_CATALOG,
   ACTIVE_INCOMINGPLATFORMCATALOG,
   PENDING_INCOMINGPLATFORMCATALOG;

   @Nullable
   public static IncomingPlatformCatalog computeIncomingPlatformCatalog(String instance) {
      switch (instance) {
         case "mysql {":
            return INCOMING_PLATFORM_CATALOG;
         case "postgresql {":
            return ACTIVE_INCOMINGPLATFORMCATALOG;
         case "sqlite {":
            return PENDING_INCOMINGPLATFORMCATALOG;
         default:
            return null;
      }
   }

   @Nullable
   public static IncomingPlatformCatalog loadIncomingPlatformCatalog(String instance) {
      switch (instance.toLowerCase(Locale.ENGLISH)) {
         case "librelogin-mysql":
            return INCOMING_PLATFORM_CATALOG;
         case "librelogin-postgresql":
            return ACTIVE_INCOMINGPLATFORMCATALOG;
         case "librelogin-sqlite":
            return PENDING_INCOMINGPLATFORMCATALOG;
         default:
            return null;
      }
   }
}
