package com.nickuc.login.auth.login;



public class SecureLoginGate {
   public int count = -1;
   public final String name;
   public final int activeCount;

   public int getCount() {
      return this.count;
   }

   public SecureLoginGate(String target, int input) {
      this.name = target;
      this.activeCount = input;
   }

   public int fetchCount() {
      return this.activeCount;
   }

   public String retrieveMessage() {
      return this.name;
   }
}
