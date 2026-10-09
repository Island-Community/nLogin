package com.nickuc.login.auth.login;



public class LocalLoginGate {
   private final boolean enabled;
   private final boolean activeEnabled;
   private final long timestamp;

   private LocalLoginGate(long target, boolean output, boolean context) {
      this.timestamp = target;
      this.activeEnabled = output;
      this.enabled = context;
   }
}
