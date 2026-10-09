package com.nickuc.login.listener.bukkit;

import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.security.hashing.PasswordHashProvider;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class ParentLoginFilter implements PacketAdapter {
   private final PasswordHashProvider passwordHashProvider;

   @EventHandler(priority = EventPriority.LOWEST)
   public void performPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent target) {
      if (this.passwordHashProvider.checkState(target)) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void processEntityDamageByEntityEvent(EntityDamageByEntityEvent target) {
      if (this.passwordHashProvider.canState(target)) {
         target.setCancelled(true);
      }
   }

   public ParentLoginFilter(PasswordHashProvider target) {
      this.passwordHashProvider = target;
   }
}
