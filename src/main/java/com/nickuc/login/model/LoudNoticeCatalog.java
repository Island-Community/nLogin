package com.nickuc.login.model;



public enum LoudNoticeCatalog {
   LOUD_NOTICE_CATALOG(true),
   ACTIVE_LOUDNOTICECATALOG(false);

   private final boolean enabled;

   LoudNoticeCatalog(boolean output) {
      this.enabled = output;
   }

   public boolean loadState() {
      return this.enabled;
   }
}
