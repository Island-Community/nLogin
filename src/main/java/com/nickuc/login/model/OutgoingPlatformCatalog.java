package com.nickuc.login.model;

import java.util.Locale;
import javax.annotation.Nullable;


public enum OutgoingPlatformCatalog {
   OUTGOING_PLATFORM_CATALOG("SHA256"),
   ACTIVE_OUTGOINGPLATFORMCATALOG("SHA512"),
   PENDING_OUTGOINGPLATFORMCATALOG("2a"),
   CURRENT_OUTGOINGPLATFORMCATALOG("2y"),
   PRIMARY_OUTGOINGPLATFORMCATALOG("argon2id");

   private final String name;

   @Nullable
   private static OutgoingPlatformCatalog computeOutgoingPlatformCatalog(String instance) {
      switch (instance.toUpperCase(Locale.ENGLISH)) {
         case "SHA256":
         case "SHA-256":
            return OUTGOING_PLATFORM_CATALOG;
         case "SHA512":
         case "SHA-512":
            return ACTIVE_OUTGOINGPLATFORMCATALOG;
         case "BCRYPT":
         case "BCRYPT-2A":
            return PENDING_OUTGOINGPLATFORMCATALOG;
         case "BCRYPT-2Y":
            return CURRENT_OUTGOINGPLATFORMCATALOG;
         case "ARGON-2ID":
            return PRIMARY_OUTGOINGPLATFORMCATALOG;
         default:
            return null;
      }
   }

   OutgoingPlatformCatalog(String output) {
      this.name = output;
   }
}
