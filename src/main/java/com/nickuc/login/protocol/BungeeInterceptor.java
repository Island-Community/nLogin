package com.nickuc.login.protocol;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import java.lang.reflect.Field;
import java.util.UUID;
import javax.annotation.Nullable;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.connection.InitialHandler;

public class BungeeInterceptor {
   private static final Field field = LoginCheckpoint.loadField(InitialHandler.class, "uniqueId");
   private static final Field activeField;
   private static final Field pendingField;
   private static final Field currentField = LoginCheckpoint.loadField(InitialHandler.class, "rewriteId");
   private static final Field primaryField = LoginCheckpoint.loadField(InitialHandler.class, "name");
   private static final Class<?> value = ChildLoginCheckpoint.loadClass("net.md_5.bungee.protocol.packet.LoginRequest");

   public static void sendPendingConnection(PendingConnection instance, String target) {
      if (target == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }

      if (target.length() > 16) {
         throw new IllegalArgumentException("Username longer than 16 characters! " + target);
      }

      if (!instance.getName().equals(target)) {
         primaryField.set(instance, target);
      }
   }

   public static boolean canState(LoginEvent instance, UUID target) {
      PendingConnection input = instance.getConnection();

      try {
         performPendingConnection(input, target);
         return true;
      } catch (IllegalAccessException context) {
         PasswordHashContainer.handleMessage("Unable to set the saved uuid for " + input.getName() + ".", context);
         instance.setCancelled(true);
         instance.setCancelReason(TextComponent.fromLegacyText("§cInternal Error: Unable to set the saved uuid."));
         return false;
      }
   }

   public static void performPendingConnection(PendingConnection instance, UUID target) {
      if (target == null) {
         throw new IllegalArgumentException("Unique ID cannot be null!");
      }

      field.set(instance, target);
      if (currentField != null) {
         currentField.set(instance, target);
      }
   }

   @Nullable
   public static UUID buildUniqueId(PendingConnection instance) {
      if (pendingField != null && activeField != null) {
         Object target = pendingField.get(instance);
         if (target == null) {
            throw new IllegalArgumentException("Login Request UUID not available!");
         } else {
            return (UUID)activeField.get(target);
         }
      } else {
         throw new UnsupportedOperationException("Login Request not supported for this BungeeCord version!");
      }
   }

   public static boolean fetchState() {
      return value != null;
   }

   static {
      String instance = "Unable to find the field ";
      if (primaryField == null) {
         throw new IllegalArgumentException("Unable to find the field name in InitialHandler");
      }

      if (field == null) {
         throw new IllegalArgumentException("Unable to find the field uniqueId in InitialHandler");
      }

      pendingField = value != null ? LoginCheckpoint.handleField(InitialHandler.class, value, 0) : null;
      activeField = pendingField != null ? LoginCheckpoint.handleField(value, UUID.class, 0) : null;
   }
}
