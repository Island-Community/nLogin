package com.nickuc.login.auth.login;

import com.nickuc.login.storage.notice.DirectNoticeCatalog;

public class VerifiedLoginCheckpoint {
   static {
      try {
         values[DirectNoticeCatalog.DIRECT_NOTICE_CATALOG.ordinal()] = 1;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.ordinal()] = 2;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG.ordinal()] = 3;
      } catch (NoSuchFieldError target) {
      }
   }
}
