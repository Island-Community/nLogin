package com.nickuc.login.model;

public enum TightPlatformCatalog {
   TIGHT_PLATFORM_CATALOG,
   ACTIVE_TIGHTPLATFORMCATALOG,
   PENDING_TIGHTPLATFORMCATALOG,
   CURRENT_TIGHTPLATFORMCATALOG,
   PRIMARY_TIGHTPLATFORMCATALOG,
   MAIN_TIGHTPLATFORMCATALOG,
   LOCAL_TIGHTPLATFORMCATALOG;

   public boolean checkState(TightPlatformCatalog target) {
      return this.ordinal() <= target.ordinal();
   }

   public boolean hasState(TightPlatformCatalog target) {
      return this.ordinal() > target.ordinal();
   }

   public boolean canState(TightPlatformCatalog target) {
      return this.ordinal() < target.ordinal();
   }

   public boolean isState(TightPlatformCatalog target) {
      return this.ordinal() >= target.ordinal();
   }
}
