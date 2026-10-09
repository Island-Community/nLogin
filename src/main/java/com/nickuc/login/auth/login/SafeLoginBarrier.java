package com.nickuc.login.auth.login;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class SafeLoginBarrier {
   public static String computeMessage(String instance) {
      if (instance == null) {
         throw new IllegalArgumentException("Input cannot be null!");
      }

      byte[] target = loadPayload(instance.getBytes(StandardCharsets.UTF_8));
      return new String(target);
   }

   public static byte[] loadPayload(byte[] instance) {
      try {
         return Base64.getDecoder().decode(instance);
      } catch (Exception input) {
         throw new RuntimeException("Unable to decode from Base64", input);
      }
   }

   public static String resolveMessage(byte[] instance) {
      if (instance == null) {
         throw new IllegalArgumentException("Bytes cannot be null!");
      } else {
         return new String(Base64.getEncoder().encode(instance));
      }
   }

   public static String createMessage(String instance) {
      if (instance == null) {
         throw new IllegalArgumentException("Input cannot be null!");
      } else {
         return resolveMessage(instance.getBytes(StandardCharsets.UTF_8));
      }
   }
}
