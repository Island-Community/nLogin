package com.nickuc.login.listener.bukkit;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.security.hashing.PasswordHashProvider;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import java.net.InetAddress;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class ReadyLoginFilter implements PacketAdapter {
   public static final boolean enabled = (boolean)(LoginGuard.resolveLoginGuard().canState(LoginGuard.outgoingLoginGuard)
         && ChildLoginCheckpoint.validateState("io.papermc.paper.event.player.PlayerServerFullCheckEvent")
      ? 1
      : 0);
   private final BukkitPlatform BukkitPlatform;
   private final PasswordHashProvider passwordHashProvider;
   private final boolean activeEnabled;

   @EventHandler(priority = EventPriority.LOW)
   public void executePlayerLoginEvent(PlayerLoginEvent target) {
      if (!this.activeEnabled) {
         if (target.getResult() == Result.ALLOWED) {
            Player input = target.getPlayer();
            if (!this.passwordHashProvider.isState(input)) {
               Player output = Bukkit.getServer().getPlayerExact(input.getName());
               if (output == null) {
                  output = Bukkit.getServer().getPlayer(input.getUniqueId());
               }

               if (output != null && output.isOnline()) {
                  String context = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_LINKED_LOUDPROXYSTATE);
                  target.disallow(Result.KICK_OTHER, context);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void savePlayerLoginEvent(PlayerLoginEvent target) {
      if (target.getResult() == Result.ALLOWED) {
         Player input = target.getPlayer();
         if (!this.passwordHashProvider.isState(input)) {
            InetAddress output;
            try {
               output = target.getRealAddress();
            } catch (NoSuchMethodError value) {
               output = null;
            }

            PacketLink context = PacketLink.processPacketLink(input.getName(), target.getAddress(), output);
            if (context == null) {
               String result = "Unable to find connection cache for user "
                  + input.getName()
                  + " in "
                  + target.getClass().getSimpleName()
                  + " event (behavior changed by a plugin?)";
               PasswordHashContainer.performMessage(result);
               target.disallow(Result.KICK_OTHER, OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + result, "", "§ePlease contact an administrator."));
            } else {
               String data = this.passwordHashProvider.handleMessage(this.BukkitPlatform.fetchPasswordStore().b().processVerifiedServerAdapter(input), context);
               if (data != null) {
                  target.disallow(Result.KICK_OTHER, data);
               }
            }
         }
      }
   }

   public ReadyLoginFilter(BukkitPlatform target, PasswordHashProvider input, boolean output) {
      this.BukkitPlatform = target;
      this.passwordHashProvider = input;
      this.activeEnabled = output;
   }
}
