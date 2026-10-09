package com.nickuc.login.protocol;

import io.netty.util.AttributeKey;


public class Sha256Bridge {
   private final String name;
   private final Object object;
   private final int count;
   public static final AttributeKey<Sha256Bridge> attributeKey = BusyLoginListener.loadAttributeKey("nlogin-handshake-data");

   public Sha256Bridge(int target, String input, Object output) {
      this.count = target;
      this.name = input;
      this.object = output;
   }

   public String loadMessage() {
      return this.name;
   }

   public Object resolveObject() {
      return this.object;
   }

   public int resolveCount() {
      return this.count;
   }
}
