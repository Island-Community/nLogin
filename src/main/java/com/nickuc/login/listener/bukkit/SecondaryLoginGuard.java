package com.nickuc.login.listener.bukkit;

import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.security.hashing.PasswordHashProvider;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityAirChangeEvent;

public class SecondaryLoginGuard implements PacketAdapter {
   private final PasswordHashProvider passwordHashProvider;

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void sendEntityAirChangeEvent(EntityAirChangeEvent target) {
      if (this.passwordHashProvider.canState(target)) {
         target.setCancelled(true);
      }
   }

   public SecondaryLoginGuard(PasswordHashProvider target) {
      this.passwordHashProvider = target;
   }
}
