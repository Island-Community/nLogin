package com.nickuc.login.listener.bukkit;

import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.listener.PacketAdapter;
import java.net.InetAddress;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class SharedLoginListener implements PacketAdapter {
   private final BukkitPlatform BukkitPlatform;

   public SharedLoginListener(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void handleAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent target) {
      if (target.getLoginResult() == Result.ALLOWED) {
         InetAddress input = target.getAddress();

         InetAddress output;
         try {
            output = target.getRawAddress();
         } catch (NoSuchMethodError data) {
            output = null;
         }

         PacketLink context = PacketLink.processPacketLink(target.getName(), input, output);
         if (context != null && context.runnable != null) {
            context.runnable.run();
         }
      }
   }
}
