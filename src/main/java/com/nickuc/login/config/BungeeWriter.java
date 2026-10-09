package com.nickuc.login.config;

import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.listener.bukkit.DiscordGuard;
import com.nickuc.login.platform.command.StrictCommandHandler;
import com.nickuc.login.security.hashing.MainPasswordHashVerifier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class BungeeWriter {
   private static final boolean enabled = (boolean)(LoginCheckpoint.handleMethod(Bukkit.class, "getGlobalRegionScheduler") != null ? 1 : 0);
   private static final Method method;
   private static final Field field;
   private static DiscordGuard discordGuard;
   private static final Method activeMethod;
   private static final Method pendingMethod;

   public static void processRunnable(Runnable instance) {
      discordGuard.processLinkedSessionHandler(true).buildStrictCommandHandler(instance);
   }

   public static void sendConsumer(Consumer<StrictCommandHandler> instance) {
      discordGuard.processLinkedSessionHandler(false).loadStrictCommandHandler(instance);
   }

   public static void performDiscordGuard(DiscordGuard instance) {
      discordGuard = instance;
   }

   public static boolean retrieveState() {
      YamlConfiguration instance = findYamlConfiguration();
      return instance == null ? false : instance.getBoolean("settings.velocity-support.enabled") || instance.getBoolean("proxies.velocity.enabled");
   }

   public static CompletableFuture<Void> resolveCompletableFuture(Player instance, String target) {
      CompletableFuture input = new CompletableFuture();
      if (!instance.isOnline()) {
         input.complete(null);
      } else if (Bukkit.getServer().isPrimaryThread()) {
         instance.kickPlayer(target);
         input.complete(null);
      } else {
         executeRunnable(() -> {
            instance.kickPlayer(target);
            input.complete(null);
         });
      }

      return input;
   }

   @Nullable
   public static YamlConfiguration findYamlConfiguration() {
      if (pendingMethod != null && method != null) {
         try {
            Object instance = pendingMethod.invoke(Bukkit.getServer());
            return (YamlConfiguration)method.invoke(instance);
         } catch (ReflectiveOperationException target) {
            if (target.getCause() instanceof UnsupportedOperationException) {
               return null;
            } else {
               throw new RuntimeException("Cannot invoke getPaperConfig method!", target);
            }
         }
      } else {
         return null;
      }
   }

   public static boolean resolveState() {
      try {
         return field != null && field.getBoolean(null);
      } catch (ReflectiveOperationException target) {
         throw new RuntimeException("Cannot invoke isBungee method!", target);
      }
   }

   public static void executeRunnable(Runnable instance) {
      discordGuard.processLinkedSessionHandler(false).buildStrictCommandHandler(instance);
   }

   public static boolean fetchState() {
      return enabled;
   }

   static {
      Method instance = null;

      try {
         Bukkit.getServer().getOnlinePlayers();
      } catch (Throwable request) {
         try {
            instance = Server.class.getMethod("getOnlinePlayers");
         } catch (Throwable result) {
            throw new UnsupportedOperationException("getOnlinePlayers not supported!", result);
         }
      }

      activeMethod = instance;
      Method target = null;
      Method input = null;

      try {
         Class output = Class.forName("org.bukkit.Server$Spigot");
         input = Server.class.getMethod("spigot");
         target = output.getMethod("getPaperConfig");
      } catch (ReflectiveOperationException value) {
      }

      pendingMethod = input;
      method = target;

      Field response;
      try {
         Class context = Class.forName("org.spigotmc.SpigotConfig");
         response = context.getField("bungee");
      } catch (ReflectiveOperationException data) {
         response = null;
      }

      field = response;
   }

   public static Collection<? extends Player> retrieveCollection() {
      if (activeMethod == null) {
         return Bukkit.getServer().getOnlinePlayers();
      }

      try {
         return MainPasswordHashVerifier.resolveMainPasswordHashVerifier((Player[])activeMethod.invoke(Bukkit.getServer()));
      } catch (ReflectiveOperationException target) {
         throw new RuntimeException("Cannot list online players! (legacy)", target);
      }
   }

   public static void executeConsumer(Consumer<StrictCommandHandler> instance) {
      discordGuard.processLinkedSessionHandler(true).loadStrictCommandHandler(instance);
   }

   public static UUID handleUniqueId(String instance) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + instance).getBytes(StandardCharsets.UTF_8));
   }
}
