package com.nickuc.login.listener.bukkit;

import com.nickuc.login.auth.login.StoredLoginHandler;
import com.nickuc.login.platform.command.StrictCommandHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class PrimaryLoginGuard implements StrictCommandHandler {
   private final Runnable runnable;
   private final boolean enabled;
   private final String name = new Exception().getStackTrace()[2].toString();
   private BukkitTask bukkitTask;
   private boolean activeEnabled;

   @Override
   public String loadMessage() {
      return this.name;
   }

   public PrimaryLoginGuard computePrimaryLoginGuard(JavaPlugin target, long input, long context, TimeUnit value) {
      if (target.isEnabled()) {
         if (this.bukkitTask != null) {
            throw new IllegalStateException("Task already started!");
         }

         this.bukkitTask = this.enabled
            ? target.getServer().getScheduler().runTaskTimerAsynchronously(target, this.runnable, value.toMillis(input) / 50L, value.toMillis(context) / 50L)
            : target.getServer().getScheduler().runTaskTimer(target, this.runnable, value.toMillis(input) / 50L, value.toMillis(context) / 50L);
      }

      return this;
   }

   @Override
   public <T> T findObject() {
      return (T)this.bukkitTask;
   }

   public PrimaryLoginGuard buildPrimaryLoginGuard(JavaPlugin target, long input, TimeUnit context) {
      if (target.isEnabled()) {
         if (this.bukkitTask != null) {
            throw new IllegalStateException("Task already started!");
         }

         this.bukkitTask = this.enabled
            ? target.getServer().getScheduler().runTaskLaterAsynchronously(target, this.runnable, context.toMillis(input) / 50L)
            : target.getServer().getScheduler().runTaskLater(target, this.runnable, context.toMillis(input) / 50L);
      }

      return this;
   }

   public PrimaryLoginGuard resolvePrimaryLoginGuard(JavaPlugin target) {
      if (target.isEnabled()) {
         if (this.bukkitTask != null) {
            throw new IllegalStateException("Task already started!");
         }

         this.bukkitTask = this.enabled
            ? target.getServer().getScheduler().runTaskAsynchronously(target, this.runnable)
            : target.getServer().getScheduler().runTask(target, this.runnable);
      }

      return this;
   }

   public PrimaryLoginGuard(boolean target, StoredLoginHandler input, Runnable output) {
      this.enabled = target;
      this.runnable = () -> {
         try {
            input.dispatchStrictCommandHandler(this);
            output.run();
         } finally {
            input.performStrictCommandHandler(this);
         }
      };
   }

   public static void sendJavaPlugin(JavaPlugin instance) {
      instance.getServer().getScheduler().cancelTasks(instance);
   }

   @Override
   public boolean fetchState() {
      return this.activeEnabled;
   }

   public PrimaryLoginGuard(boolean target, StoredLoginHandler input, Consumer<StrictCommandHandler> output) {
      this.enabled = target;
      this.runnable = () -> {
         try {
            input.dispatchStrictCommandHandler(this);
            output.accept(this);
         } finally {
            input.performStrictCommandHandler(this);
         }
      };
   }

   @Override
   public void performTask() {
      if (this.bukkitTask != null) {
         this.bukkitTask.cancel();
      }

      this.activeEnabled = true;
   }
}
