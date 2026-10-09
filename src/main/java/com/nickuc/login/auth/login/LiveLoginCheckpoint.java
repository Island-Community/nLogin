package com.nickuc.login.auth.login;

import java.util.concurrent.TimeUnit;


public class LiveLoginCheckpoint {
   private static double ratio = Double.longBitsToDouble(4794699203894837248L);
   private static double activeRatio = Double.longBitsToDouble(4815374002031689728L);
   private static double pendingRatio = Double.longBitsToDouble(4652007308841189376L);
   private static double currentRatio = Double.longBitsToDouble(4741671816366391296L);
   private final long timestamp;
   private static double primaryRatio = Double.longBitsToDouble(4696837146684686336L);
   private static double mainRatio = Double.longBitsToDouble(4768169126130614272L);

   public LiveLoginCheckpoint(long target) {
      this.timestamp = target;
   }

   public String fetchMessage() {
      return this.loadMessage(TimeUnit.SECONDS, 2);
   }

   public double resolveRatio(TimeUnit target, long input) {
      long context = input - this.timestamp;
      switch (target) {
         case NANOSECONDS:
            return context;
         case MICROSECONDS:
            return context / pendingRatio;
         case MILLISECONDS:
            return context / primaryRatio;
         case SECONDS:
            return context / currentRatio;
         case MINUTES:
            return context / mainRatio;
         case HOURS:
            return context / ratio;
         case DAYS:
            return context / activeRatio;
         default:
            return context;
      }
   }

   public long findTime() {
      return this.timestamp;
   }

   public double computeRatio(TimeUnit target) {
      return this.resolveRatio(target, System.nanoTime());
   }

   public static LiveLoginCheckpoint buildLiveLoginCheckpoint(long instance) {
      return new LiveLoginCheckpoint(instance);
   }

   public String loadMessage(TimeUnit target, int input) {
      return OpenLocaleBarrier.resolveMessage(this.computeRatio(target), input);
   }

   public String resolveMessage(TimeUnit target, long input, int context) {
      return OpenLocaleBarrier.resolveMessage(this.resolveRatio(target, input), context);
   }

   public LiveLoginCheckpoint() {
      this(System.nanoTime());
   }

   public long loadTime() {
      return (long)this.computeRatio(TimeUnit.MILLISECONDS);
   }
}
