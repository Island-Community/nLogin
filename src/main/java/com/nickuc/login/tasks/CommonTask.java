package com.nickuc.login.tasks;



public abstract class CommonTask implements Runnable {
   private final Runnable runnable;

   public void updateTask() {
      this.runnable.run();
   }

   public CommonTask(Runnable target) {
      this.runnable = target;
   }
}
