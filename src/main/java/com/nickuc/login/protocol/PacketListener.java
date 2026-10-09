package com.nickuc.login.protocol;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.listener.bukkit.LoginGuard;
import com.nickuc.login.listener.bukkit.StoredLoginListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PacketListener {
   @Nullable
   public static final Field field = PacketListener.value != null && PacketListener.primaryValue != null
      ? LoginCheckpoint.handleField(PacketListener.value, PacketListener.primaryValue, 0)
      : null;
   @Nullable
   public static final Method method;
   public static final Method activeMethod;
   @Nullable
   public static final Class<?> value = loadClass(
      () -> PacketListener.pendingMethod != null ? PacketListener.pendingMethod.getReturnType() : null,
      "net.minecraft.server.level.EntityPlayer",
      "net.minecraft.server.level.ServerPlayer",
      "{nms}.EntityPlayer"
   );
   @Nullable
   public static final Method pendingMethod = PacketListener.pendingValue != null
      ? LoginCheckpoint.handleMethod(PacketListener.pendingValue, "getHandle")
      : null;
   @Nullable
   public static final Field activeField;
   @Nullable
   public static final Class<?> activeValue = handleClass("{obc}.CraftServer");
   @Nullable
   public static final Class<?> pendingValue = handleClass("{obc}.entity.CraftPlayer");
   @Nullable
   public static final Class<?> currentValue = handleClass("net.minecraft.server.MinecraftServer", "{nms}.MinecraftServer");
   @Nullable
   public static final Class<?> primaryValue = loadClass(
      () -> value != null
         ? Arrays.stream(value.getDeclaredFields()).map(Field::getType).filter(instanceValue -> instanceValue.getSimpleName().contains("NetHandler")).findFirst().orElse(null)
         : null,
      "net.minecraft.server.network.PlayerConnection",
      "net.minecraft.server.network.ServerGamePacketListenerImpl",
      "{nms}.PlayerConnection"
   );

   public static void processTask() {
   }

   public static void handlePlayer(Player instance, Object target) {
      if (target == null) {
         throw new IllegalArgumentException("Packet cannot be null!");
      }

      if (field == null) {
         throw new IllegalArgumentException("Cannot find playerConnectionField to invoke sendPacket method!");
      }

      if (activeField == null) {
         throw new IllegalArgumentException("Cannot find playerNetworkManager to invoke sendPacket method!");
      }

      Object input = handleObject(instance);
      if (input != null) {
         Object output = field.get(input);
         if (LoginGuard.resolveLoginGuard().canState(LoginGuard.storedLoginGuard)) {
            Object context = activeField.get(output);
            LoginCheckpoint.buildObject(method, context, target);
         } else {
            LoginCheckpoint.buildObject(method, output, target);
         }
      }
   }

   static {
      Class instance;
      if (LoginGuard.resolveLoginGuard().canState(LoginGuard.storedLoginGuard) && primaryValue != null) {
         Class target = handleClass("net.minecraft.network.NetworkManager", "net.minecraft.network.Connection");
         activeField = LoginCheckpoint.handleField(primaryValue, target, 0);
         instance = target;
      } else {
         activeField = null;
         instance = primaryValue;
      }

      Class input = handleClass("net.minecraft.network.protocol.Packet", "{nms}.Packet");
      method = input != null && instance != null ? LoginCheckpoint.loadMethod(instance, null, void.class, input) : null;
      activeMethod = LoginCheckpoint.handleMethod(Sound.class, "valueOf", String.class);
   }

   @Nullable
   public static Class<?> handleClass(String... instance) {
      for (int target = 0; target < instance.length; target++) {
         instance[target] = StoredLoginListener.processMessage(instance[target]);
      }

      for (String data : instance) {
         try {
            Class request;
            if ((request = ChildLoginCheckpoint.loadClass(StoredLoginListener.processMessage(data))) != null) {
               return request;
            }
         } catch (Throwable result) {
            PasswordHashContainer.sendThrowable(result);
         }
      }

      return null;
   }

   @Nullable
   public static Object handleObject(Player instance) {
      return pendingMethod != null ? LoginCheckpoint.buildObject(pendingMethod, instance) : null;
   }

   public static Class<?> loadClass(Callable<Class<?>> instance, String... target) {
      Class input = handleClass(target);
      if (input != null) {
         return input;
      }

      try {
         return (Class<?>)instance.call();
      } catch (Exception context) {
         throw new RuntimeException("Cannot call fallback class" + (target.length > 0 ? " for " + target[0] : 0) + "!", context);
      }
   }
}
