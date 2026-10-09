package com.nickuc.login.updater;

import javax.annotation.Nullable;


public enum AuthenticatedNoticeKind {
   AUTHENTICATED_NOTICE_KIND("stable"),
   ACTIVE_AUTHENTICATEDNOTICEKIND("latest"),
   PENDING_AUTHENTICATEDNOTICEKIND("dev");

   private final String name;

   @Nullable
   public static AuthenticatedNoticeKind loadAuthenticatedNoticeKind(int instance) {
      AuthenticatedNoticeKind[] target = values();
      return target.length > instance ? target[instance] : null;
   }

   AuthenticatedNoticeKind(String output) {
      this.name = output;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public static AuthenticatedNoticeKind processAuthenticatedNoticeKind(String instance) {
      for (AuthenticatedNoticeKind context : values()) {
         if (context.name.equalsIgnoreCase(instance)) {
            return context;
         }
      }

      return null;
   }
}
