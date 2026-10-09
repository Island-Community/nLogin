package com.nickuc.login.config;

import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.crypto.SignatureData;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import com.nickuc.login.premium.FloodgateLinker;
import com.nickuc.login.protocol.MojangBridge;
import com.nickuc.login.protocol.PacketListener;
import com.nickuc.login.session.Sha256Tracker;
import com.nickuc.login.storage.password.PasswordStore;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Key;
import java.security.KeyPair;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.bukkit.Server;
import org.slf4j.LoggerFactory;

public class SettingsRegistry {
   private final KeyPair keyPair;
   private final BukkitPlatform BukkitPlatform;
   public final MojangBridge mojangBridge;
   private static final Method method;
   public final FloodgateLinker floodgateLinker = new FloodgateLinker(this, null);
   private static final Method activeMethod;
   private final PasswordStore passwordStore;

   static {
      LoggerFactory.getLogger(SettingsRegistry.class);
      Method target = null;
      Method instance;
      if ((instance = LoginCheckpoint.loadMethod(SpigotReflectionUtil.NETWORK_MANAGER_CLASS, null, null, SecretKey.class)) == null) {
         instance = LoginCheckpoint.loadMethod(SpigotReflectionUtil.NETWORK_MANAGER_CLASS, null, null, Cipher.class, Cipher.class);
         if (instance == null) {
            throw new IllegalArgumentException("Cannot find encrypt method in NetworkManager!");
         }

         Class input = PacketListener.handleClass(
            "net.minecraft.util.MinecraftEncryption", "net.minecraft.util.Crypt", "net.minecraft.util.CryptManager", "{nms}.MinecraftEncryption"
         );
         if (input == null) {
            throw new IllegalArgumentException("Cannot find MinecraftEncryption class!");
         }

         target = LoginCheckpoint.loadMethod(input, null, null, int.class, Key.class);
         if (target == null) {
            throw new IllegalArgumentException("Cannot find configure cipher method in MinecraftEncryption!");
         }
      }

      method = instance;
      activeMethod = target;
   }

   private SettingsRegistry(BukkitPlatform target, PasswordStore input, KeyPair output) {
      this.mojangBridge = new MojangBridge(this, null);
      this.BukkitPlatform = target;
      this.passwordStore = input;
      this.keyPair = output;
   }

   private static KeyPair buildKeyPair(Server instance) {
      KeyPair target = null;
      Object input = SpigotReflectionUtil.getMinecraftServerInstance(instance);
      if (input != null) {
         Field output = LoginCheckpoint.handleField(SpigotReflectionUtil.MINECRAFT_SERVER_CLASS, KeyPair.class, 0);
         if (output != null) {
            try {
               target = (KeyPair)output.get(input);
            } catch (ReflectiveOperationException data) {
               throw new RuntimeException("Unable to find server key pair", data);
            }
         }
      }

      if (target == null) {
         target = Sha256Tracker.handleKeyPair(1024);
      }

      return target;
   }

   private static void dispatchUser(User instance, WrapperLoginClientLoginStart target, String input) {
      if (input.length() > 16) {
         throw new IllegalArgumentException("Username longer than 16 characters! " + input);
      }

      instance.receivePacketSilently(
         new WrapperLoginClientLoginStart(
            target.getClientVersion(), target.getUsername(), (SignatureData)target.getSignatureData().orElse(null), (UUID)target.getPlayerUUID().orElse(null)
         )
      );
   }

   public SettingsRegistry(BukkitPlatform target) {
      this(target, target.fetchPasswordStore(), buildKeyPair(target.getServer()));
   }
}
