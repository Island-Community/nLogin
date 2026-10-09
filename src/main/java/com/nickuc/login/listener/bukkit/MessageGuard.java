package com.nickuc.login.listener.bukkit;

import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageGuard implements PacketAdapter {
   private final BukkitPlatform BukkitPlatform;

   @EventHandler(priority = EventPriority.HIGH)
   public void saveAsyncPlayerChatEvent(AsyncPlayerChatEvent target) {
      VerifiedServerAdapter input = this.BukkitPlatform.b().processVerifiedServerAdapter(target.getPlayer());
      if (this.BukkitPlatform.fetchPasswordStore().getFloodgateResolver().verifyState(input, target.getMessage())) {
         target.setCancelled(true);
      }
   }

   public MessageGuard(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }
}
