package com.nickuc.login.auth.login;

import com.github.benmanes.caffeine.cache.Cache;


public class FastLoginCheckpoint {
   private final Cache<String, Long> cache;

   public FastLoginCheckpoint(Cache<String, Long> target) {
      this.cache = target;
   }

   public boolean isState(String target) {
      return hasState(target, this.cache);
   }

   public static boolean hasState(String instance, Cache<String, Long> target) {
      long input = System.currentTimeMillis();
      long context = (Long)target.get(instance, inputValue -> input);
      return context == input;
   }
}
