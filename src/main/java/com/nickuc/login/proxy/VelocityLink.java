package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.StoredLoginHandler;
import com.nickuc.login.loader.platform.VelocityLoader;
import com.nickuc.login.platform.command.StrictCommandHandler;
import com.velocitypowered.api.scheduler.ScheduledTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class VelocityLink implements StrictCommandHandler {
   private final Runnable runnable;
   private final String name;
   private ScheduledTask scheduledTask;
   private boolean enabled;

   public VelocityLink processVelocityLink(VelocityLoader target, long input, long context, TimeUnit value) {
      if (this.scheduledTask != null) {
         throw new IllegalStateException("Task already started!");
      }

      this.scheduledTask = target.getServer().getScheduler().buildTask(target, this.runnable).delay(input, value).repeat(context, value).schedule();
      return this;
   }

   public VelocityLink(StoredLoginHandler target, Consumer<StrictCommandHandler> input) {
      this.name = new Exception().getStackTrace()[2].toString();
      this.runnable = () -> {
         try {
            target.dispatchStrictCommandHandler(this);
            input.accept(this);
         } finally {
            target.performStrictCommandHandler(this);
         }
      };
   }

   @Override
   public String loadMessage() {
      return this.name;
   }

   @Override
   public boolean fetchState() {
      return this.enabled;
   }

   public VelocityLink(Runnable target, String input) {
      this.runnable = target;
      this.name = input;
   }

   public VelocityLink buildVelocityLink(VelocityLoader target) {
      if (this.scheduledTask != null) {
         throw new IllegalStateException("Task already started!");
      }

      this.scheduledTask = target.getServer().getScheduler().buildTask(target, this.runnable).schedule();
      return this;
   }

   @Override
   public <T> T findObject() {
      return (T)this.scheduledTask;
   }

   @Override
   public void performTask() {
      if (this.scheduledTask != null) {
         this.scheduledTask.cancel();
      }

      this.enabled = true;
   }

   public VelocityLink(StoredLoginHandler target, Runnable input) {
      this.name = new Exception().getStackTrace()[2].toString();
      this.runnable = () -> {
         try {
            target.dispatchStrictCommandHandler(this);
            input.run();
         } finally {
            target.performStrictCommandHandler(this);
         }
      };
   }

   public VelocityLink computeVelocityLink(VelocityLoader target, long input, TimeUnit context) {
      if (this.scheduledTask != null) {
         throw new IllegalStateException("Task already started!");
      }

      this.scheduledTask = target.getServer().getScheduler().buildTask(target, this.runnable).delay(input, context).schedule();
      return this;
   }
}
