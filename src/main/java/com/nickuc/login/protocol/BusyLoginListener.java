package com.nickuc.login.protocol;

import io.netty.util.AttributeKey;

public class BusyLoginListener {
   public static <T> AttributeKey<T> loadAttributeKey(String instance) {
      try {
         return AttributeKey.valueOf(instance);
      } catch (NoSuchMethodError context) {
         try {
            return AttributeKey.class.getConstructor(String.class).newInstance(instance);
         } catch (ReflectiveOperationException output) {
            throw new RuntimeException("Cannot create attribute key called \"" + instance + "\"!", output);
         }
      }
   }
}
