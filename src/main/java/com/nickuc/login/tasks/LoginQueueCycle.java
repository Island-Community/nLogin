package com.nickuc.login.tasks;

public class LoginQueueCycle {
   private int count = 3;

   public boolean findState() {
      this.count++;
      if (this.count == 4) {
         this.count = 0;
         return true;
      } else {
         return false;
      }
   }
}
