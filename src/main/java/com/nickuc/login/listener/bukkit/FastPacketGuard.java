package com.nickuc.login.listener.bukkit;

import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.protocol.PasswordHashListener;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FastPacketGuard implements PacketAdapter {
   private final Server server;
   private final DiscordGuard discordGuard;

   @EventHandler(priority = EventPriority.LOWEST)
   public void dispatchPlayerLoginEvent(PlayerLoginEvent target) {
      Player input = target.getPlayer();
      PasswordHashListener.sessions.put(input, PasswordHashListener.loadPasswordHashListener(this.discordGuard, this.server, input));
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void handlePlayerQuitEvent(PlayerQuitEvent target) {
      PasswordHashListener.sessions.remove(target.getPlayer());
   }

   public FastPacketGuard(DiscordGuard target, Server input) {
      this.discordGuard = target;
      this.server = input;
   }
}
