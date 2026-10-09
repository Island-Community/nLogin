package com.nickuc.login.model;

import javax.annotation.Nullable;


public enum PlatformState {
   PLATFORM_STATE(null),
   ACTIVE_PLATFORMSTATE(null),
   PENDING_PLATFORMSTATE(null),
   CURRENT_PLATFORMSTATE("MD5"),
   PRIMARY_PLATFORMSTATE("SHA256"),
   MAIN_PLATFORMSTATE(null),
   LOCAL_PLATFORMSTATE(null),
   REMOTE_PLATFORMSTATE("SHA512"),
   CACHED_PLATFORMSTATE("SHA256"),
   STORED_PLATFORMSTATE(null);

   public final String name;

   PlatformState(String output) {
      this.name = output;
   }

   @Nullable
   public static PlatformState resolvePlatformState(int instance) {
      return instance > 0 && instance < values().length ? values()[instance] : null;
   }
}
