package com.nickuc.login.listener.bukkit;

import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.security.hashing.PasswordHashProvider;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class RemoteLoginListener implements PacketAdapter {
   private final PasswordHashProvider passwordHashProvider;

   public RemoteLoginListener(PasswordHashProvider target) {
      this.passwordHashProvider = target;
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void performPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }
}
