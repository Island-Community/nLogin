package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.StoredLoginHandler;
import com.nickuc.login.platform.command.StrictCommandHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class LocalBungeeBridge implements StrictCommandHandler {
   private final String name = new Exception().getStackTrace()[2].toString();
   private ScheduledTask scheduledTask;
   private final Runnable runnable;
   private boolean enabled;

   public LocalBungeeBridge(StoredLoginHandler target, Runnable input) {
      this.runnable = () -> {
         try {
            target.dispatchStrictCommandHandler(this);
            input.run();
         } finally {
            target.performStrictCommandHandler(this);
         }
      };
   }

   public LocalBungeeBridge computeLocalBungeeBridge(Plugin target, long input, TimeUnit context) {
      if (this.scheduledTask != null) {
         throw new IllegalStateException("Task already started!");
      }

      this.scheduledTask = target.getProxy().getScheduler().schedule(target, this.runnable, input, context);
      return this;
   }

   @Override
   public void performTask() {
      if (this.scheduledTask != null) {
         this.scheduledTask.cancel();
      }

      this.enabled = true;
   }

   @Override
   public boolean fetchState() {
      return this.enabled;
   }

   @Override
   public <T> T findObject() {
      return (T)this.scheduledTask;
   }

   @Override
   public String loadMessage() {
      return this.name;
   }

   public LocalBungeeBridge handleLocalBungeeBridge(Plugin target, long input, long context, TimeUnit value) {
      if (this.scheduledTask != null) {
         throw new IllegalStateException("Task already started!");
      }

      this.scheduledTask = target.getProxy().getScheduler().schedule(target, this.runnable, input, context, value);
      return this;
   }

   public LocalBungeeBridge(StoredLoginHandler target, Consumer<StrictCommandHandler> input) {
      this.runnable = () -> {
         try {
            target.dispatchStrictCommandHandler(this);
            input.accept(this);
         } finally {
            target.performStrictCommandHandler(this);
         }
      };
   }

   public LocalBungeeBridge computeLocalBungeeBridge(Plugin target) {
      if (this.scheduledTask != null) {
         throw new IllegalStateException("Task already started!");
      }

      this.scheduledTask = target.getProxy().getScheduler().runAsync(target, this.runnable);
      return this;
   }
}
