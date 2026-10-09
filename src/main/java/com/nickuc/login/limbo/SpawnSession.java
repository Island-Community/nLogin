package com.nickuc.login.limbo;

import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.PrivateNoticeKind;
import com.nickuc.login.listener.bukkit.BusyLoginFilter;
import com.nickuc.login.listener.bukkit.VerifiedLoginGuard;
import com.nickuc.login.platform.account.IncomingAccountHandler;
import com.nickuc.login.security.hashing.PrimaryPasswordHashVerifier;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.spawn.IndirectLoginKind;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import com.nickuc.login.tasks.SynchronizeWithServerThreadTask;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpawnSession implements IncomingAccountHandler {
   private final LimboTracker limboTracker;
   private static float factor = Float.intBitsToFloat(1036831949);
   private Location location;
   private static float activeFactor = Float.intBitsToFloat(1036831949);
   private Location activeLocation;
   private static double ratio = Double.longBitsToDouble(4602678819172646912L);
   private final File dataFile;
   private final BukkitPlatform BukkitPlatform;
   private static float pendingFactor = Float.intBitsToFloat(1045220557);
   private Location pendingLocation;
   private static float currentFactor = Float.intBitsToFloat(1036831949);
   private static float primaryFactor = Float.intBitsToFloat(1036831949);
   private Location currentLocation;
   private static float mainFactor = Float.intBitsToFloat(1036831949);
   private Location primaryLocation;
   private boolean enabled;
   private static double activeRatio = Double.longBitsToDouble(4602678819172646912L);
   private static float localFactor = Float.intBitsToFloat(1045220557);
   private static float remoteFactor = Float.intBitsToFloat(1036831949);

   public Location getLocation() {
      return this.primaryLocation;
   }

   public Location resolveLocation() {
      return this.pendingLocation;
   }

   private void executePlayer(Player target, Location input) {
      if (BungeeWriter.fetchState()) {
         target.teleportAsync(input, TeleportCause.PLUGIN);
      } else {
         target.teleport(input, TeleportCause.PLUGIN);
      }
   }

   @Nullable
   private Location createLocation(BukkitPlatform target, IndirectLoginKind input) {
      if (input == IndirectLoginKind.MAIN_INDIRECTLOGINKIND) {
         throw new UnsupportedOperationException("Unsupported spawn type: " + input);
      }

      PrimaryPasswordHashVerifier output = this.BukkitPlatform.a().loadPrimaryPasswordHashVerifier();
      String context = input.fetchMessage();

      try {
         Location data = VerifiedLoginGuard.resolveLocation(output.loadMessage(context));
         if (data != null) {
            World value = data.getWorld();
            if (value == null || target.getServer().getWorld(value.getName()) == null) {
               PasswordHashContainer.updateMessage("Corrupted " + input + " spawn location: world not found!");
               output.createPrimaryPasswordHashVerifier(context).sendTask();
               data = null;
            }
         }

         return data;
      } catch (Exception result) {
         PasswordHashContainer.handleMessage("Corrupted " + input + " spawn location!", result);
         output.createPrimaryPasswordHashVerifier(context).sendTask();
         return null;
      }
   }

   @Nullable
   public Location buildLocation(Location target, boolean input) {
      if (input && this.currentLocation != null) {
         return this.currentLocation;
      }

      if (this.activeLocation != null) {
         return this.activeLocation;
      }

      if (SpawnState.OPEN_SPAWNSTATE.ar()) {
         World output = target.getWorld();
         if (output != null) {
            return output.getHighestBlockAt(target).getLocation().add(ratio, 0.0, activeRatio);
         }
      }

      return null;
   }

   private void dispatchLimboStore(LimboStore target) {
      Player input = target.player;
      if (target.enabled && this.pendingLocation != null) {
         try {
            this.executePlayer(input, this.pendingLocation);
         } catch (Exception data) {
            PasswordHashContainer.handleMessage("[Limbo] Failed to teleport " + input.getName() + " to register spawn.", data);
         }
      } else if (!this.enabled && this.location != null) {
         try {
            this.executePlayer(input, this.location);
         } catch (Exception value) {
            PasswordHashContainer.handleMessage("[Limbo] Failed to teleport " + input.getName() + " to auth spawn.", value);
         }
      } else {
         if (this.activeLocation != null) {
            Location output = target.location != null ? target.location : (this.enabled ? target.activeLocation : null);
            if (output != null) {
               try {
                  this.executePlayer(input, output);
               } catch (Exception result) {
                  PasswordHashContainer.handleMessage("[Limbo] Failed to teleport " + input.getName() + " to last location.", result);
               }
            }
         }
      }
   }

   public File fetchFile() {
      return this.dataFile;
   }

   public void performLimboCoordinator(LimboCoordinator target, LimboStore input, boolean output) {
      synchronized (target.object) {
         try {
            Player data = input.player;
            if (!data.isOnline()) {
               return;
            }

            if (!this.BukkitPlatform.getServer().isPrimaryThread() && !BungeeWriter.fetchState()) {
               throw new IllegalStateException("Limbo restore need to run on the server thread!");
            }

            data.updateInventory();
            if (input.privateNoticeKind == PrivateNoticeKind.PENDING_PRIVATENOTICEKIND) {
               this.saveLimboStore(input);
               this.dispatchLimboStore(input);
               if (SpawnState.FAST_SPAWNSTATE.ar() || output) {
                  LimboKeeper.updateNLoginBukkit(this.BukkitPlatform, data);
               }

               if (SpawnState.SECURE_SPAWNSTATE.ar()) {
                  data.removePotionEffect(PotionEffectType.BLINDNESS);
               }

               this.BukkitPlatform.processLinkedSessionHandler(true).buildStrictCommandHandler(input.dataFile::delete);
               return;
            }

            if (input.privateNoticeKind != PrivateNoticeKind.CURRENT_PRIVATENOTICEKIND) {
               this.dispatchLimboStore(input);
            }
         } finally {
            input.privateNoticeKind = PrivateNoticeKind.CURRENT_PRIVATENOTICEKIND;
         }
      }
   }

   public boolean validateState(LimboCoordinator target, LimboStore input, boolean output) {
      synchronized (target.object) {
         Player data = input.player;
         if (input.privateNoticeKind != PrivateNoticeKind.PRIVATE_NOTICE_KIND) {
            return true;
         }

         try {
            if (input.dataFile.exists()) {
               try {
                  this.limboTracker.processLimboStore(input, output);
               } catch (IOException option) {
                  PasswordHashContainer.handleMessage("Unable to load player limbo file for " + data.getName() + " [corrupted data?]", option);
                  return false;
               } catch (Exception setting) {
                  PasswordHashContainer.handleMessage("Unable to load player limbo file for " + data.getName() + " " + input.dataFile, setting);
                  return false;
               }
            } else {
               input.primaryEnabled = data.getAllowFlight();
               input.activeEnabled = data.isFlying();
               input.mainEnabled = BusyLoginFilter.hasState(data);
               input.gameMode = data.getGameMode();
               input.activeCount = data.getFoodLevel();
               input.count = data.getTotalExperience();
               input.ratio = data.getHealth();
               input.activeFactor = data.getWalkSpeed();
               input.factor = data.getFlySpeed();
               input.enabled = output;
               if (!output) {
                  input.location = !input.pendingEnabled && input.activeLocation != null ? (this.enabled ? input.activeLocation : null) : data.getLocation();
               }
            }

            try {
               this.limboTracker.saveLimboStore(input);
            } catch (IOException reference) {
               PasswordHashContainer.handleMessage("Unable to save player limbo file for " + input.player.getName() + " [insufficient permissions?]", reference);
               return false;
            } catch (Exception subject) {
               PasswordHashContainer.handleMessage("Unable to save player limbo file for " + input.player.getName() + " " + input.dataFile, subject);
               return false;
            }

            if (output || input.activeLocation != null) {
               try {
                  Location value = this.buildLocation(data.getLocation(), output);
                  if (value != null) {
                     if (BungeeWriter.fetchState()) {
                        data.teleportAsync(value, TeleportCause.PLUGIN);
                     } else {
                        data.teleport(value, TeleportCause.PLUGIN);
                     }
                  }
               } catch (Exception holder) {
                  PasswordHashContainer.handleMessage("[Spawn] Failed to teleport " + data.getName() + " to join location.", holder);
               }
            }

            return true;
         } finally {
            input.privateNoticeKind = PrivateNoticeKind.ACTIVE_PRIVATENOTICEKIND;
         }
      }
   }

   public Location loadLocation() {
      return this.currentLocation;
   }

   public Location retrieveLocation() {
      return this.location;
   }

   public Location findLocation() {
      return this.activeLocation;
   }

   public SpawnSession(BukkitPlatform target) {
      this.BukkitPlatform = target;
      this.limboTracker = new LimboTracker(this);
      this.dataFile = new File(target.resolveFile(), "limbo");
      target.processLinkedSessionHandler(false)
         .buildStrictCommandHandler(new SynchronizeWithServerThreadTask(() -> this.processPasswordStore(target.fetchPasswordStore(), false)));
      File input = new File(target.resolveFile(), "tmp");
      if (!this.dataFile.exists() && input.exists() && input.isDirectory()) {
         input.renameTo(this.dataFile);
      }
   }

   public boolean retrieveState() {
      return this.enabled;
   }

   public boolean validateState(LimboCoordinator target, LimboStore input) {
      synchronized (target.object) {
         Player context = input.player;
         if (input.privateNoticeKind != PrivateNoticeKind.ACTIVE_PRIVATENOTICEKIND) {
            return true;
         }

         byte value;
         try {
            if (!context.isOnline()) {
               return true;
            }

            LimboRegistry data = this.BukkitPlatform.fetchPasswordStore().loadLimboRegistry();
            if (!data.canState(this.BukkitPlatform.b().processVerifiedServerAdapter(context))) {
               File payload = input.dataFile;
               if (!payload.exists()) {
                  throw new IllegalStateException("Limbo file not created yet!");
               }

               if (!this.BukkitPlatform.getServer().isPrimaryThread() && !BungeeWriter.fetchState()) {
                  throw new IllegalStateException("Limbo clear need to run on the server thread!");
               }

               if (SpawnState.ROOT_SPAWNSTATE.ar()) {
                  input.currentEnabled = true;
                  context.removePotionEffect(PotionEffectType.BLINDNESS);
                  context.setGameMode(GameMode.ADVENTURE);
                  context.setAllowFlight(SpawnState.SAFE_SPAWNSTATE.ar());
                  context.setFlying(false);
                  BusyLoginFilter.handlePlayer(context, false);
                  context.setFoodLevel(20);
                  context.setTotalExperience(0);
                  if (context.getHealth() > 0.0) {
                     double result = context.getMaxHealth();
                     context.setHealth(result);
                  }
               }

               if (SpawnState.SAFE_SPAWNSTATE.ar()) {
                  context.setFlySpeed(0.0F);
                  context.setWalkSpeed(0.0F);
               }

               if (SpawnState.FAST_SPAWNSTATE.ar()) {
                  LimboKeeper.processNLoginBukkit(this.BukkitPlatform, context);
               }

               if (SpawnState.SECURE_SPAWNSTATE.ar()) {
                  context.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999, 127));
               }

               return true;
            }

            value = 1;
         } finally {
            input.privateNoticeKind = PrivateNoticeKind.PENDING_PRIVATENOTICEKIND;
         }

         return (boolean)value;
      }
   }

   @Override
   public void processPasswordStore(PasswordStore target, boolean input) {
      BukkitPlatform output = this.BukkitPlatform;
      this.location = this.createLocation(output, IndirectLoginKind.PENDING_INDIRECTLOGINKIND);
      this.pendingLocation = this.createLocation(output, IndirectLoginKind.CURRENT_INDIRECTLOGINKIND);
      this.activeLocation = this.createLocation(output, IndirectLoginKind.INDIRECT_LOGIN_KIND);
      this.currentLocation = this.createLocation(output, IndirectLoginKind.ACTIVE_INDIRECTLOGINKIND);
      this.primaryLocation = this.createLocation(output, IndirectLoginKind.PRIMARY_INDIRECTLOGINKIND);
      PrimaryPasswordHashVerifier context = target.a().loadPrimaryPasswordHashVerifier();
      String data = IndirectLoginKind.MAIN_INDIRECTLOGINKIND.fetchMessage();
      if (!context.hasState(data)) {
         context.createPrimaryPasswordHashVerifier(data, this.location == null);
      }

      this.enabled = context.hasState(data, true);
   }

   private void saveLimboStore(LimboStore target) {
      Player input = target.player;
      if (target.currentEnabled) {
         GameMode output = target.gameMode;
         input.setGameMode(output != null ? output : GameMode.SURVIVAL);
         input.setAllowFlight(target.primaryEnabled);
         input.setFlying(target.activeEnabled && input.getAllowFlight());
         BusyLoginFilter.handlePlayer(input, target.mainEnabled);
         input.setFoodLevel(target.activeCount);
         int context = target.count;
         if (context > 0) {
            input.setTotalExperience(context);
         }

         double data = target.ratio;
         if (data > 0.0) {
            double result = Math.max(input.getMaxHealth(), 0.0);
            input.setHealth(Math.min(data, result));
         }

         float entry = target.activeFactor;
         input.setWalkSpeed(!(Math.abs(entry) > 1.0F) && !(entry <= factor) ? entry : localFactor);
         float request = target.factor;
         input.setFlySpeed(!(Math.abs(request) > 1.0F) && !(request <= activeFactor) ? request : currentFactor);
      } else {
         float response = input.getWalkSpeed();
         input.setWalkSpeed(!(Math.abs(response) > 1.0F) && !(response <= mainFactor) ? response : pendingFactor);
         float source = input.getFlySpeed();
         input.setFlySpeed(!(Math.abs(source) > 1.0F) && !(source <= primaryFactor) ? source : remoteFactor);
      }
   }
}
