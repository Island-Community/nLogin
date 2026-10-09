package com.nickuc.login.auth.bedrock;

import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import java.lang.reflect.Method;
import java.util.UUID;

public class BedrockCheckpoint implements SecondaryConnectionContract {
   private static volatile boolean geyserChecked;
   private static volatile Method connectionLookup;

   @Override
   public boolean fetchState() {
      return false;
   }

   private static Method resolveConnectionLookup() {
      if (!geyserChecked) {
         synchronized (BedrockCheckpoint.class) {
            if (!geyserChecked) {
               try {
                  Class<?> apiType = Class.forName("org.geysermc.api.Geyser");
                  Object api = apiType.getMethod("api").invoke(null);
                  connectionLookup = api.getClass().getMethod("connectionByUuid", UUID.class);
               } catch (Throwable ignored) {
                  connectionLookup = null;
               } finally {
                  geyserChecked = true;
               }
            }
         }
      }
      return connectionLookup;
   }

   @Override
   public boolean isState(UUID target) {
      try {
         if (target == null) {
            return false;
         }
         Method lookup = resolveConnectionLookup();
         if (lookup == null) {
            return false;
         }
         Object api = Class.forName("org.geysermc.api.Geyser").getMethod("api").invoke(null);
         return lookup.invoke(api, target) != null;
      } catch (Throwable output) {
         if (output.getCause() instanceof ClassNotFoundException) {
            PasswordHashContainer.performMessage("Unable to verify that the player is a bedrock player. This is a Geyser error: %s", output.getMessage());
         } else {
            PasswordHashContainer.processMessage("Unable to verify that the player is a bedrock player. Most likely this is a Geyser error.", output);
         }

         return false;
      }
   }
}
