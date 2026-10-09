package com.nickuc.login.protocol;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.RemotePremiumState;
import com.nickuc.login.listener.bukkit.DiscordGuard;
import com.nickuc.login.listener.bukkit.PendingLoginFilter;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.connection.QuickConnectionContract;
import com.nickuc.login.platform.command.SilentCommandHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PasswordHashListener implements VerifiedServerAdapter {
   private static Method method;
   private final Server server;
   private ChainedSessionHandler chainedSessionHandler;
   private final Player player;
   private static Method activeMethod;
   private static final Field field = PacketListener.value != null ? LoginCheckpoint.loadField(PacketListener.value, "locale") : null;
   private static final Class<?> value;
   private final UUID uniqueId = UUID.randomUUID();
   private final LinkedSessionHandler linkedSessionHandler;
   private static final Class<?> activeValue = ChildLoginCheckpoint.loadClass("org.bukkit.entity.Player$Spigot");
   public static final Map<Player, PasswordHashListener> sessions;
   private static final Field activeField = PacketListener.value != null ? LoginCheckpoint.loadField(PacketListener.value, "ping") : null;
   private static Method pendingMethod;

   @Override
   public <T> T findObject() {
      return (T)this.player;
   }

   private static PasswordHashListener resolvePasswordHashListener(DiscordGuard instance, Server target, Player input) {
      PasswordHashListener output = sessions.get(input);
      if (output != null) {
         return output;
      }

      if (PasswordHashContainer.findState()) {
         StackTraceElement[] context = new Exception().getStackTrace();
         String data = context.length > 0 ? context[Math.min(3, context.length - 1)].toString() : "unknown";
         PasswordHashContainer.performMessage("[nCore] Creating player instance for %s with no cache in \"%s\" context", input.getName(), data);
      }

      return loadPasswordHashListener(instance, target, input);
   }

   @Override
   public ChainedSessionHandler getChainedSessionHandler() {
      if (this.chainedSessionHandler == null) {
         this.chainedSessionHandler = (target, input) -> {
            if (this.player.isOnline()) {
               try {
                  PacketListener.handlePlayer(this.player, target);

                  for (Object value : input) {
                     PacketListener.handlePlayer(this.player, value);
                  }
               } catch (ReflectiveOperationException result) {
                  throw new RuntimeException("Cannot send packet for " + this.getName(), result);
               }
            }
         };
      }

      return this.chainedSessionHandler;
   }

   @Override
   public boolean hasState(String target) {
      return this.player.hasPermission(target);
   }

   @Override
   public void processMessage(String target) {
      this.player.chat(target);
   }

   @Override
   public void executeTask() {
      QuickConnectionContract.retrieveQuickConnectionContract().dispatchPlayer(this.player);
   }

   @Nullable
   @Override
   public InetSocketAddress fetchInetSocketAddress() {
      try {
         return this.player.getAddress();
      } catch (NullPointerException input) {
         return null;
      }
   }

   @Override
   public String findMessage() {
      return this.player.getDisplayName();
   }

   @Override
   public void handleMessage(String target) {
      SilentCommandHandler.resolveSilentCommandHandler().send(this.player, LocalLocaleFlow.loadMessage(target, true));
   }

   private PasswordHashListener(Player target, Server input, LinkedSessionHandler output) {
      this.player = target;
      this.server = input;
      this.linkedSessionHandler = output;
   }

   @Override
   public Optional<String> loadOptional() {
      try {
         return Optional.of(this.player.getLocale());
      } catch (NoSuchMethodError data) {
         if (field == null) {
            return Optional.empty();
         }

         Object input = PacketListener.handleObject(this.player);
         if (input == null) {
            return Optional.empty();
         }

         try {
            return Optional.ofNullable((String)field.get(input));
         } catch (Exception context) {
            return Optional.empty();
         }
      }
   }

   @Override
   public void performIndirectSessionHandler(IndirectSessionHandler<?> target, RemotePremiumState input, Object output, byte[] context) {
      if (!(output instanceof String)) {
         throw new IllegalArgumentException("Channel must be a string!");
      }

      this.player.sendPluginMessage((Plugin)target.loadObject(), (String)output, context);
   }

   @Override
   public CompletableFuture<Void> buildCompletableFuture(String target) {
      return BungeeWriter.resolveCompletableFuture(this.player, LocalLocaleFlow.loadMessage(target, true));
   }

   @Override
   public int getCount() {
      if (activeField == null) {
         return 0;
      }

      Object target = PacketListener.handleObject(this.player);
      if (target == null) {
         return 0;
      }

      try {
         return (Integer)activeField.get(target);
      } catch (Exception output) {
         PasswordHashContainer.handleMessage("Cannot get ping for " + this + ".", output);
         return 0;
      }
   }

   @Override
   public boolean findState() {
      return this.player.hasMetadata("fake") || this.player.hasMetadata("NPC") || this.player.hasMetadata("fake-player");
   }

   @Override
   public void saveMessage(String target, String input, int output, int context, int data) {
      QuickConnectionContract.retrieveQuickConnectionContract()
         .performPlayer(this.player, LocalLocaleFlow.loadMessage(target, true), LocalLocaleFlow.loadMessage(input, true), output, context, data);
   }

   public static PasswordHashListener processPasswordHashListener(DiscordGuard instance, Server target, Object input) {
      if (input instanceof String) {
         String output = ((String)input).toLowerCase(Locale.ENGLISH);
         Player context = target.getPlayerExact(output);
         return context == null ? null : resolvePasswordHashListener(instance, target, context);
      } else if (input instanceof Player) {
         return resolvePasswordHashListener(instance, target, (Player)input);
      } else {
         throw new IllegalArgumentException("Unsupported player type! " + input + " " + (input != null ? input.getClass().getCanonicalName() : ""));
      }
   }

   @Override
   public UUID getUniqueId() {
      return this.player.getUniqueId();
   }

   @Override
   public void processMessageForValue(String target) {
      this.player.setDisplayName(target);
   }

   @Override
   public boolean equals(Object target) {
      if (target != null && this.getClass() == target.getClass()) {
         PasswordHashListener input = (PasswordHashListener)target;
         return Objects.equals(this.uniqueId, input.uniqueId) && Objects.equals(this.player, input.player);
      } else {
         return false;
      }
   }

   @Override
   public boolean loadState() {
      Player target = this.server.getPlayerExact(this.player.getName());
      return target != null && target.equals(this.player);
   }

   public static PasswordHashListener loadPasswordHashListener(DiscordGuard instance, Server target, Player input) {
      LinkedSessionHandler output = BungeeWriter.fetchState()
         ? new PendingLoginFilter(instance.retrieveBukkitLoader(), input)
         : instance.processLinkedSessionHandler(false);
      return new PasswordHashListener(input, target, output);
   }

   @Override
   public LinkedSessionHandler getLinkedSessionHandler() {
      return this.linkedSessionHandler;
   }

   static {
      if (activeValue != null) {
         activeMethod = LoginCheckpoint.handleMethod(Player.class, "spigot");
         Class instance = ChildLoginCheckpoint.loadClass("net.md_5.bungee.api.chat.BaseComponent");
         if (instance != null) {
            pendingMethod = LoginCheckpoint.handleMethod(activeValue, "sendMessage", instance);
         }
      }

      value = ChildLoginCheckpoint.loadClass("net.kyori.adventure.text.Component");
      Class target = ChildLoginCheckpoint.loadClass("net.kyori.adventure.audience.Audience");
      if (value != null && target != null && target.isAssignableFrom(Player.class)) {
         method = LoginCheckpoint.handleMethod(target, "sendMessage", value);
      }

      sessions = new ConcurrentHashMap<>();
   }

   @Override
   public String getName() {
      return this.player.getName();
   }

   @Override
   public void performObject(Object target) {
      if (target instanceof String) {
         if (method != null) {
            LoginCheckpoint.buildObject(method, this.player, SafeLoginService.computeTextComponent((String)target, true));
         } else {
            this.player.sendMessage(LocalLocaleFlow.loadMessage((String)target, true));
         }
      } else if (method != null && target instanceof Component) {
         LoginCheckpoint.buildObject(method, this.player, target);
      } else {
         if (activeValue == null || activeMethod == null || pendingMethod == null || !(target instanceof BaseComponent)) {
            throw new IllegalArgumentException("Unsupported message value! " + target + " " + target.getClass().getCanonicalName());
         }

         try {
            Object input = activeMethod.invoke(this.player);
            pendingMethod.invoke(input, target);
         } catch (ReflectiveOperationException output) {
            throw new RuntimeException("Unable to send message to " + this.getName(), output);
         }
      }
   }

   public static boolean getState() {
      return method != null;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.uniqueId, this.player);
   }

   @Override
   public String toString() {
      return "BukkitPlayerFactory{objectId=" + this.uniqueId + ", player=" + this.player + '}';
   }

   @Override
   public void performMessage(String target) {
      if (target.length() >= 2 && target.charAt(0) == '/') {
         target = target.substring(1);
      }

      this.server.dispatchCommand(this.player, target);
   }
}
