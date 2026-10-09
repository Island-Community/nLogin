package com.nickuc.login.model;

import javax.annotation.Nullable;


public enum StrictPlatformCatalog {
   STRICT_PLATFORM_CATALOG("cn"),
   ACTIVE_STRICTPLATFORMCATALOG("en"),
   PENDING_STRICTPLATFORMCATALOG("es"),
   CURRENT_STRICTPLATFORMCATALOG("pt"),
   PRIMARY_STRICTPLATFORMCATALOG("ru");

   private final String name;

   @Nullable
   public static StrictPlatformCatalog createStrictPlatformCatalog(String instance) {
      for (StrictPlatformCatalog context : values()) {
         if (context.name.equals(instance)) {
            return context;
         }
      }

      return null;
   }

   StrictPlatformCatalog(String output) {
      this.name = output;
   }

   public String getMessage() {
      return this.name;
   }
}
