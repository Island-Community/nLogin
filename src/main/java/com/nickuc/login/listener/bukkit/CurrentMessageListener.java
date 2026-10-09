package com.nickuc.login.listener.bukkit;

import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import io.papermc.paper.event.player.AsyncChatEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class CurrentMessageListener implements PacketAdapter {
   private final BukkitPlatform BukkitPlatform;

   @EventHandler(priority = EventPriority.HIGH)
   public void saveAsyncChatEvent(AsyncChatEvent target) {
      VerifiedServerAdapter input = this.BukkitPlatform.b().processVerifiedServerAdapter(target.getPlayer());
      Component output = target.message();
      String context = output instanceof TextComponent ? ((TextComponent)output).content() : null;
      if (this.BukkitPlatform.fetchPasswordStore().getFloodgateResolver().verifyState(input, context)) {
         target.setCancelled(true);
      }
   }

   public CurrentMessageListener(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }
}
