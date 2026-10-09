package com.nickuc.login.auth.login;



public class ReadyLoginGate {
   private final String name;
   private final long timestamp;

   public ReadyLoginGate(long target, String output) {
      this.timestamp = target;
      this.name = output;
   }

   public long retrieveTime() {
      return this.timestamp;
   }

   public String findMessage() {
      return this.name;
   }
}
