package com.nickuc.login.protocol;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nickuc.login.auth.login.LenientLoginFlow;
import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import com.nickuc.login.listener.bukkit.PrimaryLoginListener;
import java.lang.reflect.Field;
import java.util.UUID;
import javax.annotation.Nullable;

public class MojangInterceptor {
   private static boolean enabled;
   private static Field field;
   private static Field activeField;

   private static boolean canState(Object instance) {
      if (enabled) {
         return activeField != null && field != null;
      }

      try {
         for (Field context : instance.getClass().getDeclaredFields()) {
            Class data = context.getType();
            if (!data.isPrimitive() && data.getPackage() != null && !data.getPackage().getName().startsWith("java.")) {
               try {
                  context.setAccessible(true);
               } catch (Throwable record) {
                  continue;
               }

               Object value = context.get(instance);
               if (value != null) {
                  Field result = LoginCheckpoint.handleField(value.getClass(), SpigotReflectionUtil.GAME_PROFILE_CLASS, 0);
                  if (result != null) {
                     activeField = context;
                     field = result;
                     return true;
                  }
               }
            }
         }

         return false;
      } finally {
         enabled = true;
      }
   }

   private MojangInterceptor() {
   }

   @Nullable
   public static Runnable computeRunnable(Object instance, String target, UUID input, @Nullable LenientLoginFlow output) {
      try {
         Field context = LoginCheckpoint.handleField(instance.getClass(), UUID.class, 0);
         if (context != null) {
            context.set(instance, input);
         }

         if (!PrimaryLoginListener.enabled) {
            if (!canState(instance) && context == null) {
               throw new RuntimeException("Cannot spoof GameProfile " + input + " for " + target + "!");
            }

            if (enabled && activeField != null && field != null) {
               return () -> {
                  try {
                     Object contextValue = activeField.get(instance);
                     GameProfile dataValue = new GameProfile(input, target);
                     if (output != null) {
                        dataValue.getProperties().put("textures", new Property("textures", output.activeName, output.name));
                     }

                     field.set(contextValue, dataValue);
                  } catch (IllegalAccessException value) {
                     throw new RuntimeException("Cannot set new game profile using GameProfile workaround", value);
                  }
               };
            }
         }
      } catch (Exception data) {
         PasswordHashContainer.handleMessage("Failed to update unique id of " + target, data);
      }

      return null;
   }
}
