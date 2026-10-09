package com.nickuc.login.auth.login;

import javax.annotation.Nullable;


public class CachedLoginProcessor {
   @Nullable
   private final String name;
   private final String activeName;

   public String fetchMessage() {
      return this.activeName;
   }

   public CachedLoginProcessor(String target, @Nullable String input) {
      this.activeName = target;
      this.name = input;
   }

   @Nullable
   public String getMessage() {
      return this.name;
   }
}
