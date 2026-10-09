package com.nickuc.login.auth.login;

import com.nickuc.login.storage.notice.DirectNoticeCatalog;

public class LenientLoginCheckpoint {
   static {
      try {
         values[DirectNoticeCatalog.DIRECT_NOTICE_CATALOG.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
