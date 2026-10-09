package com.nickuc.login.listener.bukkit;

import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.BusyLimboStore;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class SpawnListener implements PacketAdapter {
   private final BukkitPlatform BukkitPlatform;
   private final BusyLimboStore busyLimboStore;

   @EventHandler(priority = EventPriority.HIGH)
   public void savePlayerSpawnLocationEvent(PlayerSpawnLocationEvent target) {
      VerifiedServerAdapter input = this.BukkitPlatform.b().processVerifiedServerAdapter(target.getPlayer());
      String output = input.getName();
      InetAddress context = Optional.ofNullable(input.fetchInetSocketAddress()).map(InetSocketAddress::getAddress).orElse(null);
      PacketLink data = PacketLink.processPacketLink(output, context, null);
      if (data == null) {
         String request = "Unable to find connection cache for user " + output + " in " + target.getClass().getSimpleName() + " event (behavior changed by a plugin?)";
         PasswordHashContainer.performMessage(request);
         input.buildCompletableFuture(OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + request, "", "§ePlease contact an administrator."));
      } else {
         Location value = target.getSpawnLocation();
         Location result = this.busyLimboStore.loadSpawnSession().buildLocation(value, false);
         if (result != null) {
            if (ReadyLoginFilter.enabled) {
               data.location = value;
            } else {
               this.BukkitPlatform.fetchPasswordStore().loadLimboRegistry().loadLimboCoordinator(input).resolveLimboStore().performLocation(value);
            }

            target.setSpawnLocation(result);
         }
      }
   }

   public SpawnListener(BukkitPlatform target, BusyLimboStore input) {
      this.BukkitPlatform = target;
      this.busyLimboStore = input;
   }
}
