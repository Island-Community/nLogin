package com.nickuc.login.auth.login;

import javax.annotation.Nullable;

public class ChildLoginCheckpoint {
   public static boolean canState(Class<?> instance, String target, Class<?>... input) {
      try {
         instance.getDeclaredMethod(target, input);
         return true;
      } catch (NoSuchMethodException context) {
         return false;
      }
   }

   public static boolean validateState(String instance, String... target) {
      return loadClass(instance, target) != null;
   }

   @Nullable
   public static Class<?> loadClass(String instance, String... target) {
      try {
         return Class.forName(instance);
      } catch (ClassNotFoundException | NoClassDefFoundError response) {
         for (String value : target) {
            try {
               return Class.forName(value);
            } catch (ClassNotFoundException | NoClassDefFoundError request) {
            }
         }

         return null;
      }
   }

   public static boolean validateState(Class<?> instance, String target) {
      try {
         instance.getDeclaredField(target);
         return true;
      } catch (NoSuchFieldException output) {
         return false;
      }
   }
}
