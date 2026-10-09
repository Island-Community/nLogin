package com.nickuc.login.auth.login;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.annotation.Nullable;

public class LoginCheckpoint {
   public static <T> T computeObject(Method instance, Object... target) {
      return loadObject(instance, null, target);
   }

   public static <T> T computeObjectForObject(Method instance, Object... target) {
      if (instance != null) {
         try {
            return computeObject(instance, target);
         } catch (InvocationTargetException | IllegalAccessException | ClassCastException output) {
         }
      }

      return null;
   }

   public static <T> Constructor<T> buildConstructor(Class<?> instance, Class<?>... target) {
      Constructor input = instance.getDeclaredConstructor(target);
      input.setAccessible(true);
      return input;
   }

   @Nullable
   public static Method loadMethod(Class<?> instance, @Nullable String target, @Nullable Class<?> input, Class<?>... output) {
      for (Method result : instance.getDeclaredMethods()) {
         if ((target == null || result.getName().equals(target))
            && (input == null || result.getReturnType().equals(input))
            && Arrays.equals(result.getParameterTypes(), output)) {
            result.setAccessible(true);
            return result;
         }
      }

      return null;
   }

   public static <T> T loadObject(Method instance, Object target, Object... input) {
      return (T)instance.invoke(target, input);
   }

   @Nullable
   public static Field processField(Class<?> instance, int target) {
      return handleField(instance, null, target);
   }

   @Nullable
   public static Method handleMethod(Class<?> instance, String target, Class<?>... input) {
      try {
         return processMethod(instance, target, input);
      } catch (NoSuchMethodException context) {
         return null;
      }
   }

   public static Method processMethod(Class<?> instance, String target, Class<?>... input) {
      Method output = instance.getDeclaredMethod(target, input);
      output.setAccessible(true);
      return output;
   }

   @Nullable
   public static Field handleField(Class<?> instance, @Nullable Class<?> target, int input) {
      if (input < 0) {
         throw new IllegalArgumentException("Negative index! " + input);
      }

      Field[] output = instance.getDeclaredFields();
      if (output.length > 0) {
         int context = 0;

         for (Field request : output) {
            if (target != null) {
               Class response = request.getType();
               if (target == Object.class ? response != Object.class : response == Object.class || !target.isAssignableFrom(response)) {
                  continue;
               }
            }

            if (context == input) {
               request.setAccessible(true);
               return request;
            }

            context++;
         }
      }

      return null;
   }

   @Nullable
   public static <T> T buildObject(Method instance, Object target, Object... input) {
      if (instance != null) {
         try {
            return loadObject(instance, target, input);
         } catch (InvocationTargetException | IllegalAccessException | ClassCastException context) {
         }
      }

      return null;
   }

   @Nullable
   public static Field loadField(Class<?> instance, String... target) {
      if (target.length == 0) {
         throw new IllegalArgumentException("Fields cannot be empty!");
      }

      for (String data : target) {
         try {
            return resolveField(instance, data);
         } catch (NoSuchFieldException result) {
         }
      }

      return null;
   }

   @Nullable
   public static <T> Constructor<T> createConstructor(Class<?> instance, Class<?>... target) {
      try {
         return buildConstructor(instance, target);
      } catch (NoSuchMethodException | ClassCastException output) {
         return null;
      }
   }

   public static Field resolveField(Class<?> instance, String target) {
      Field input = instance.getDeclaredField(target);
      input.setAccessible(true);
      return input;
   }
}
