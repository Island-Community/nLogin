package com.nickuc.login.listener.bukkit;

import com.nickuc.login.auth.login.StoredLoginHandler;
import com.nickuc.login.platform.command.StrictCommandHandler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class IncomingLoginGuard implements StrictCommandHandler {
   @Nullable
   private final Player player;
   private ScheduledTask scheduledTask;
   private final boolean enabled;
   private final String name = new Exception().getStackTrace()[2].toString();
   private boolean activeEnabled;
   private final Consumer<ScheduledTask> consumer;

   public IncomingLoginGuard buildIncomingLoginGuard(JavaPlugin target, long input, TimeUnit context) {
      if (target.isEnabled()) {
         if (this.scheduledTask != null) {
            throw new IllegalStateException("Task already started!");
         }

         this.scheduledTask = this.player != null
            ? this.player.getScheduler().runDelayed(target, this.consumer, () -> this.consumer.accept(null), context.toMillis(input) / 50L)
            : (
               this.enabled
                  ? Bukkit.getAsyncScheduler().runDelayed(target, this.consumer, input, context)
                  : Bukkit.getGlobalRegionScheduler().runDelayed(target, this.consumer, context.toMillis(input) / 50L)
            );
      }

      return this;
   }

   @Override
   public <T> T findObject() {
      return (T)this.scheduledTask;
   }

   public IncomingLoginGuard(@Nullable Player target, boolean input, StoredLoginHandler output, Consumer<StrictCommandHandler> context) {
      this.enabled = input;
      this.player = target;
      this.consumer = outputValue -> {
         try {
            output.dispatchStrictCommandHandler(this);
            context.accept(this);
         } finally {
            output.performStrictCommandHandler(this);
         }
      };
   }

   public IncomingLoginGuard(@Nullable Player target, boolean input, StoredLoginHandler output, Runnable context) {
      this.enabled = input;
      this.player = target;
      this.consumer = outputValue -> {
         try {
            output.dispatchStrictCommandHandler(this);
            context.run();
         } finally {
            output.performStrictCommandHandler(this);
         }
      };
   }

   public IncomingLoginGuard resolveIncomingLoginGuard(JavaPlugin target) {
      if (target.isEnabled()) {
         if (this.scheduledTask != null) {
            throw new IllegalStateException("Task already started!");
         }

         this.scheduledTask = this.player != null
            ? this.player.getScheduler().run(target, this.consumer, () -> this.consumer.accept(null))
            : (this.enabled ? Bukkit.getAsyncScheduler().runNow(target, this.consumer) : Bukkit.getGlobalRegionScheduler().run(target, this.consumer));
      }

      return this;
   }

   @Override
   public String loadMessage() {
      return this.name;
   }

   public static void sendJavaPlugin(JavaPlugin instance) {
      Bukkit.getAsyncScheduler().cancelTasks(instance);
      Bukkit.getGlobalRegionScheduler().cancelTasks(instance);
   }

   public IncomingLoginGuard computeIncomingLoginGuard(JavaPlugin target, long input, long context, TimeUnit value) {
      if (target.isEnabled()) {
         if (this.scheduledTask != null) {
            throw new IllegalStateException("Task already started!");
         }

         this.scheduledTask = this.player != null
            ? this.player
               .getScheduler()
               .runAtFixedRate(target, this.consumer, () -> this.consumer.accept(null), value.toMillis(input) / 50L, value.toMillis(context) / 50L)
            : (
               this.enabled
                  ? Bukkit.getAsyncScheduler().runAtFixedRate(target, this.consumer, input, context, value)
                  : Bukkit.getGlobalRegionScheduler().runAtFixedRate(target, this.consumer, value.toMillis(input) / 50L, value.toMillis(context) / 50L)
            );
      }

      return this;
   }

   @Override
   public boolean fetchState() {
      return this.activeEnabled;
   }

   @Override
   public void performTask() {
      if (this.scheduledTask != null) {
         this.scheduledTask.cancel();
      }

      this.activeEnabled = true;
   }
}
