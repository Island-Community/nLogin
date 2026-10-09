package com.nickuc.login.command;

import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.security.hashing.PasswordHashProvider;
import com.nickuc.login.session.BusyLimboStore;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.storage.spawn.SpawnState;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.server.ServerCommandEvent;

public class VerifiedLocaleAction implements PacketAdapter {
   private final BukkitPlatform BukkitPlatform;
   private final LimboRegistry limboRegistry;
   private final PasswordHashProvider passwordHashProvider;
   private final boolean enabled;
   private static final GameMode gameMode;

   @EventHandler(priority = EventPriority.LOW)
   public void sendPlayerQuitEvent(PlayerQuitEvent target) {
      ((PasswordHashProvider)this.BukkitPlatform.fetchPasswordStore().getFloodgateResolver()).executePlayerQuitEvent(target);
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void updatePlayerItemConsumeEvent(PlayerItemConsumeEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void updatePlayerBedEnterEvent(PlayerBedEnterEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void handlePlayerInteractEntityEvent(PlayerInteractEntityEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void performPlayerInteractEvent(PlayerInteractEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   public VerifiedLocaleAction(BukkitPlatform target, LimboRegistry input, PasswordHashProvider output, boolean context) {
      this.BukkitPlatform = target;
      this.limboRegistry = input;
      this.passwordHashProvider = output;
      this.enabled = context;
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void processPlayerDropItemEvent(PlayerDropItemEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   static {
      GameMode instance;
      try {
         instance = GameMode.valueOf("SPECTATOR");
      } catch (IllegalArgumentException input) {
         instance = null;
      }

      gameMode = instance;
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void processPlayerShearEntityEvent(PlayerShearEntityEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   private boolean isState(String target) {
      if (!target.isEmpty()) {
         if (target.charAt(0) != '/') {
            target = '/' + target;
         }

         String[] input = target.split(" ");
         if (input.length > 1) {
            String output = input[0].toLowerCase(Locale.ENGLISH);
            return output.equals("/nlogin");
         }
      }

      return false;
   }

   @EventHandler
   public void savePlayerRespawnEvent(PlayerRespawnEvent target) {
      Player input = target.getPlayer();
      if (!this.BukkitPlatform.fetchPasswordStore().loadLimboRegistry().canState(input.getName(), input.getUniqueId())) {
         BusyLimboStore output = (BusyLimboStore)this.BukkitPlatform.fetchPasswordStore().findSettingsLinker();
         Location context = output.loadSpawnSession().getLocation();
         if (context != null) {
            target.setRespawnLocation(context);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void saveInventoryClickEvent(InventoryClickEvent target) {
      if (this.passwordHashProvider.hasState(target.getWhoClicked()) && this.hasState(target.getView())) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void sendPlayerItemHeldEvent(PlayerItemHeldEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void handleServerCommandEvent(ServerCommandEvent target) {
      if (!this.enabled && target instanceof Cancellable && target.isCancelled() && this.isState(target.getCommand())) {
         target.setCancelled(false);
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void performPlayerKickEvent(PlayerKickEvent target) {
      String input = target.getReason();
      int output = !input.equalsIgnoreCase("You logged in from another location")
            && (
               this.limboRegistry.canState(this.BukkitPlatform.b().processVerifiedServerAdapter(target.getPlayer()))
                  || !input.equalsIgnoreCase("Flying is not enabled on this server")
            )
         ? 0
         : 1;
      if (output != 0) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void updatePlayerPickupItemEvent(PlayerPickupItemEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   private boolean hasState(Object target) {
      if (target == null) {
         return true;
      }

      List input = SpawnState.LIVE_SPAWNSTATE.a(new Object[0]);
      if (input.isEmpty()) {
         return true;
      }

      String output;
      try {
         output = LoginCheckpoint.processMethod(target.getClass(), "getTitle").invoke(target).toString();
      } catch (ReflectiveOperationException data) {
         throw new RuntimeException("Cannot invoke " + target.getClass().getCanonicalName() + "#getTitle", data);
      }

      return input.stream().filter(Objects::nonNull).noneMatch(targetValue -> LocalLocaleFlow.handleMessage(targetValue).contains(output));
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void sendPlayerFishEvent(PlayerFishEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void handlePlayerMoveEvent(PlayerMoveEvent target) {
      if (SpawnState.SAFE_SPAWNSTATE.ar()) {
         Player input = target.getPlayer();
         Location output = target.getFrom();
         Location context = target.getTo();
         if (context == null || !(output.getY() > context.getY()) || gameMode != null && input.getGameMode() == gameMode) {
            LimboCoordinator data = this.limboRegistry.loadLimboCoordinator(this.BukkitPlatform.b().processVerifiedServerAdapter(input));
            if (!data.loadTightPlatformCatalog().isState(TightPlatformCatalog.LOCAL_TIGHTPLATFORMCATALOG) || data.d(LenientMessageKind.SAFE_LENIENTMESSAGEKIND)
               )
             {
               target.setTo(output);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void executePlayerJoinEvent(PlayerJoinEvent target) {
      ((PasswordHashProvider)this.BukkitPlatform.fetchPasswordStore().getFloodgateResolver()).sendPlayerJoinEvent(target);
   }

   @EventHandler(priority = EventPriority.LOW)
   public void sendAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent target) {
      if (target.getLoginResult() == Result.ALLOWED) {
         InetAddress input = target.getAddress();
         if (input == null || input.getHostAddress() == null) {
            target.disallow(
               Result.KICK_OTHER,
               OpenLocaleBarrier.loadMessage(
                  "§c[nLogin]",
                  "",
                  "§cImpossible to authenticate because no IP address was provided.",
                  "",
                  "§cIn case of problems, try restarting your client."
               )
            );
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void dispatchPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent target) {
      VerifiedServerAdapter input = this.BukkitPlatform.b().processVerifiedServerAdapter(target.getPlayer());
      String output = target.getMessage().trim();
      String context = this.BukkitPlatform.fetchPasswordStore().getFloodgateResolver().handleMessage(input, output);
      if (context == null) {
         target.setCancelled(true);
      } else {
         if (!output.equals(context)) {
            target.setMessage(context);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void saveInventoryOpenEvent(InventoryOpenEvent target) {
      HumanEntity input = target.getPlayer();
      if (this.passwordHashProvider.hasState(input) && this.hasState(target.getView())) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void savePlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent target) {
      if (!this.enabled && target.isCancelled() && this.isState(target.getMessage())) {
         target.setCancelled(false);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void updatePlayerEditBookEvent(PlayerEditBookEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }
}
