package com.nickuc.login.auth.login;



public class LiveLoginBarrier {
   private final String name;
   private final long timestamp;
   private final long activeTimestamp;

   private LiveLoginBarrier(String target, long input, long context) {
      this.name = target;
      this.timestamp = input;
      this.activeTimestamp = context;
   }
}
