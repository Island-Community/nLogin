package com.nickuc.login.auth.login;

import java.util.concurrent.TimeUnit;


public class CachedLoginHandler implements Cloneable {
   private String name;
   public static CachedLoginHandler cachedLoginHandler = new CachedLoginHandler(
      "- {pos}. [{id}]:  {start}ms ... {end}ms  ({took}ms)", "-> Total: %sms", TimeUnit.MILLISECONDS, 3
   );
   private String activeName;
   private int count;
   private TimeUnit timeUnit;

   public CachedLoginHandler processCachedLoginHandler(int target) {
      this.count = target;
      return this;
   }

   public CachedLoginHandler handleCachedLoginHandler(TimeUnit target) {
      this.timeUnit = target;
      return this;
   }

   public CachedLoginHandler buildCachedLoginHandler(String target) {
      this.activeName = target;
      return this;
   }

   public CachedLoginHandler processCachedLoginHandler(String target) {
      this.name = target;
      return this;
   }

   public static CachedLoginHandler findCachedLoginHandler() {
      try {
         return (CachedLoginHandler)cachedLoginHandler.clone();
      } catch (CloneNotSupportedException target) {
         throw new RuntimeException(target);
      }
   }

   public CachedLoginHandler(String target, String input, TimeUnit output, int context) {
      this.name = target;
      this.activeName = input;
      this.timeUnit = output;
      this.count = context;
   }
}
