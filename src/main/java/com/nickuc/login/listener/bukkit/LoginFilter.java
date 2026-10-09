package com.nickuc.login.listener.bukkit;

import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.security.hashing.PasswordHashProvider;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class LoginFilter implements PacketAdapter {
   private final PasswordHashProvider passwordHashProvider;

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void handleBlockBreakEvent(BlockBreakEvent target) {
      if (this.passwordHashProvider.canState(target.getPlayer())) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void handleBlockPlaceEvent(BlockPlaceEvent target) {
      if (this.passwordHashProvider.canState(target.getPlayer())) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void performSignChangeEvent(SignChangeEvent target) {
      if (this.passwordHashProvider.canState(target.getPlayer())) {
         target.setCancelled(true);
      }
   }

   public LoginFilter(PasswordHashProvider target) {
      this.passwordHashProvider = target;
   }
}
