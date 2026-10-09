package com.nickuc.login.security.hashing;

import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginService;
import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.limbo.LimboStore;
import com.nickuc.login.listener.bukkit.ReadyLoginFilter;
import com.nickuc.login.listener.bukkit.SettingsGuard;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.connection.StrictConnectionContract;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.FloodgateResolver;
import com.nickuc.login.session.BusyLimboStore;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.session.MojangCoordinator;
import com.nickuc.login.spawn.PremiumOption;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PasswordHashProvider extends FloodgateResolver {
   private final boolean enabled;
   private final BukkitPlatform BukkitPlatform;

   public String handleMessage(VerifiedServerAdapter target, PacketLink input) {
      long output = System.nanoTime();
      String data = target.getName();

      try {
         if (this.enabled) {
            InetSocketAddress element = Optional.ofNullable(target.fetchInetSocketAddress()).orElse((InetSocketAddress)input.channel.remoteAddress());
            LimboCoordinator content = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(target, null, target.getName(), element, false, false, input);
            if (input.location != null) {
               content.resolveLimboStore().performLocation(input.location);
            }

            return null;
         } else {
            SecondaryConnectionContract value = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
            String result = super.createMessage(
               target,
               input.spawnLookup,
               input.name,
               Optional.ofNullable(target.fetchInetSocketAddress()).orElse((InetSocketAddress)input.channel.remoteAddress()),
               input.loadState(),
               input.enabled || value != null && value.isState(target.getUniqueId()),
               input
            );
            if (result == null && input.location != null) {
               this.passwordStore.loadLimboRegistry().loadLimboCoordinator(target).resolveLimboStore().performLocation(input.location);
            }

            return result;
         }
      } catch (Throwable record) {
         PasswordHashContainer.handleMessage("Severe error during login handler. (" + data + ")", record);
         return "§4[nLogin] Severe internal error detected. Please report to an admin.";
      } finally {
         PrimaryLoginService.savePremiumOption(PremiumOption.PENDING_PREMIUMOPTION, output);
      }
   }

   public boolean checkState(PlayerEvent target) {
      Player input = target.getPlayer();
      return this.canState(input);
   }

   public boolean isState(Player target) {
      if (MojangCoordinator.pendingEnabled) {
         PasswordHashContainer.processMessage(
            "Verifying if player named \""
               + target.getName()
               + "\" with ID \""
               + target.getUniqueId()
               + "\" ("
               + target
               + ", hash = "
               + target.hashCode()
               + ", class = "
               + target.getClass().getCanonicalName()
               + ") is an NPC"
         );
      }

      if (!target.hasMetadata("fake") && !target.hasMetadata("NPC") && !target.hasMetadata("fake-player")) {
         SettingsGuard input = ((StrictConnectionContract)this.passwordStore.loadInternalAccountHandler()).findSettingsGuard();
         return input != null && input.canState(target);
      } else {
         return true;
      }
   }

   public void executePlayerQuitEvent(PlayerQuitEvent target) {
      Player input = target.getPlayer();
      VerifiedServerAdapter output = this.passwordStore.b().processVerifiedServerAdapter(input);
      long context = System.nanoTime();
      LimboRegistry value = this.passwordStore.loadLimboRegistry();

      try {
         try {
            if (SpawnState.PENDING_AUTHENTICATED_SPAWNSTATE.ar()) {
               String result = target.getQuitMessage();
               if (result != null && result.toLowerCase(Locale.ENGLISH).contains("left the game")) {
                  target.setQuitMessage(null);
               }
            }

            if (this.isState(input)) {
               return;
            }

            Channel payload = this.passwordStore.resolveRootMessageHandler().handleChannel(target, input);
            if (payload != null) {
               if (this.passwordStore.resolveRootMessageHandler().verifyState(payload)) {
                  return;
               }
            } else {
               PasswordHashContainer.performMessage("Unable to get the channel from " + target.getClass().getSimpleName() + " event for " + output.getName());
            }

            LimboCoordinator request = value.buildLimboCoordinator(output);
            if (request != null) {
               LimboStore response = (LimboStore)request.d(LenientMessageKind.LOCAL_LENIENTMESSAGEKIND);
               if (response != null) {
                  BusyLimboStore source = (BusyLimboStore)this.passwordStore.findSettingsLinker();
                  source.loadSpawnSession().performLimboCoordinator(request, response, true);
               }
            }

            if (SpawnState.TOP_SPAWNSTATE.ar()) {
               this.passwordStore.resolveRootMessageHandler().executeVerifiedServerAdapter(output);
            }

            if (!this.enabled) {
               super.performVerifiedServerAdapter(output);
               return;
            }
         } catch (Throwable element) {
            PasswordHashContainer.handleMessage("Severe error during quit handler. (" + output.getName() + ")", element);
         }
      } finally {
         value.saveVerifiedServerAdapter(output);
         PrimaryLoginService.savePremiumOption(PremiumOption.PRIMARY_PREMIUMOPTION, context);
      }
   }

   public void sendPlayerJoinEvent(PlayerJoinEvent target) {
      Player input = target.getPlayer();
      if (!this.isState(input)) {
         VerifiedServerAdapter output = this.passwordStore.b().processVerifiedServerAdapter(input);
         Channel context = this.passwordStore.resolveRootMessageHandler().handleChannel(target, input);
         if (context == null) {
            output.buildCompletableFuture(
               OpenLocaleBarrier.loadMessage(
                  "§4[nLogin]", "", "§cUnable to get the channel from " + target.getClass().getSimpleName() + " event", "", "§ePlease contact an administrator."
               )
            );
         }

         if (!this.passwordStore.resolveRootMessageHandler().verifyState(context)) {
            if (ReadyLoginFilter.enabled) {
               String data = this.computeMessage(output, context, target);
               if (data != null) {
                  output.buildCompletableFuture(data);
                  return;
               }
            }

            long element = System.nanoTime();

            try {
               if (!this.passwordStore.loadLimboRegistry().canState(input.getName(), input.getUniqueId())) {
                  if (SpawnState.PENDING_AUTHENTICATED_SPAWNSTATE.ar()) {
                     String result = target.getJoinMessage();
                     if (result != null && result.toLowerCase(Locale.ENGLISH).contains("joined the game")) {
                        target.setJoinMessage(null);
                     }
                  }

                  LimboCoordinator content = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
                  Runnable request = () -> {
                     if (input.getHealth() <= 0.0 || input.isDead()) {
                        input.spigot().respawn();
                        content.resolveLimboStore().saveTask();
                     }

                     if (this.enabled) {
                        this.BukkitPlatform.resolveLimboSupervisor().saveVerifiedServerAdapter(output);
                     } else {
                        super.processVerifiedServerAdapter(output, content);
                     }
                  };
                  if (BungeeWriter.fetchState()) {
                     output.getLinkedSessionHandler().buildStrictCommandHandler(request);
                  } else {
                     request.run();
                  }

                  return;
               }
            } catch (Throwable record) {
               PasswordHashContainer.handleMessage("Severe error during login request when joining. (" + output.getName() + ")", record);
               output.buildCompletableFuture("§4[nLogin] Severe internal error detected. Please report to an admin.");
               return;
            } finally {
               PrimaryLoginService.savePremiumOption(PremiumOption.CURRENT_PREMIUMOPTION, element);
            }
         }
      }
   }

   private String computeMessage(VerifiedServerAdapter target, Channel input, Object output) {
      String context = target.getName();

      try {
         PacketLink data = (PacketLink)input.attr(PacketLink.attributeKey).get();
         if (data == null) {
            String value = "Unable to find connection cache for user "
               + target.getName()
               + " in "
               + output.getClass().getSimpleName()
               + " event (behavior changed by a plugin?)";
            PasswordHashContainer.performMessage(value);
            return OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + value, "", "§ePlease contact an administrator.");
         } else {
            return this.handleMessage(target, data);
         }
      } catch (Throwable result) {
         PasswordHashContainer.handleMessage("Severe error during login handler. (" + context + ")", result);
         return "§4[nLogin] Severe internal error detected. Please report to an admin.";
      }
   }

   public boolean canState(Player target) {
      if (target == null) {
         return true;
      } else {
         return this.isState(target) ? false : !this.passwordStore.loadLimboRegistry().canState(this.passwordStore.b().processVerifiedServerAdapter(target));
      }
   }

   public boolean canState(EntityEvent target) {
      Entity input = target.getEntity();
      return this.hasState(input);
   }

   public boolean hasState(Entity target) {
      if (target instanceof Player) {
         Player input = (Player)target;
         return this.canState(input);
      } else {
         return false;
      }
   }

   public PasswordHashProvider(PasswordStore target, BukkitPlatform input, boolean output) {
      super(target);
      this.BukkitPlatform = input;
      this.enabled = output;
   }
}
