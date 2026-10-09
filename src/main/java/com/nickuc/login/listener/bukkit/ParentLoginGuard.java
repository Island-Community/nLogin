package com.nickuc.login.listener.bukkit;

import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.security.hashing.PasswordHashProvider;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ParentLoginGuard implements PacketAdapter {
   private final PasswordHashProvider passwordHashProvider;

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void executeFoodLevelChangeEvent(FoodLevelChangeEvent target) {
      if (this.passwordHashProvider.canState(target)) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void handleEntityTargetEvent(EntityTargetEvent target) {
      if (this.passwordHashProvider.hasState(target.getTarget())) {
         target.setTarget(null);
      }
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
   public void updateEntityShootBowEvent(EntityShootBowEvent target) {
      if (this.passwordHashProvider.canState(target)) {
         target.setCancelled(true);
      }
   }

   public ParentLoginGuard(PasswordHashProvider target) {
      this.passwordHashProvider = target;
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void saveEntityInteractEvent(EntityInteractEvent target) {
      if (this.passwordHashProvider.canState(target)) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void performEntityRegainHealthEvent(EntityRegainHealthEvent target) {
      if (this.passwordHashProvider.canState(target)) {
         target.setAmount(0.0);
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void handleEntityDamageByEntityEvent(EntityDamageByEntityEvent target) {
      if (this.passwordHashProvider.hasState(target.getDamager())) {
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void updateEntityDamageEvent(EntityDamageEvent target) {
      if (this.passwordHashProvider.canState(target)) {
         target.getEntity().setFireTicks(0);
         target.setDamage(0.0);
         target.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void handleProjectileLaunchEvent(ProjectileLaunchEvent target) {
      Projectile input = target.getEntity();
      if (input.getType() != EntityType.ENDER_PEARL) {
         ProjectileSource output = input.getShooter();
         if (output instanceof Player && this.passwordHashProvider.canState((Player)output)) {
            target.setCancelled(true);
         }
      }
   }
}
