package com.nickuc.login.spawn;

import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginService;
import java.util.concurrent.TimeUnit;

public enum PremiumOption {
   PREMIUM_OPTION("Login queue", 3, 5, 8, 15, 20),
   ACTIVE_PREMIUMOPTION("Pre-login", 10, 20, 30, 50, 80),
   PENDING_PREMIUMOPTION("Login", 10, 20, 30, 50, 80),
   CURRENT_PREMIUMOPTION("Join", 10, 30, 50, 60, 80),
   PRIMARY_PREMIUMOPTION("Quit", 3, 5, 10, 15, 20),
   MAIN_PREMIUMOPTION("Chat", 3, 5, 10, 15, 20),
   LOCAL_PREMIUMOPTION("Auth", 25, 50, 100, 200, 500),
   REMOTE_PREMIUMOPTION("Register", 250, 500, 750, 1500, 2500),
   CACHED_PREMIUMOPTION("Build", 5, 10, 20, 50, 100),
   STORED_PREMIUMOPTION("Update", 10, 20, 50, 80, 120),
   VERIFIED_PREMIUMOPTION("RSA", true, 200, 400, 600, 800, 1000),
   AUTHENTICATED_PREMIUMOPTION("Hashing", 100, 250, 500, 750, 1500);

   private static final String[] names = new String[]{"§a", "§e", "§6", "§c", "§4"};
   public final String name;
   private final boolean enabled;
   private final int[] values;
   private static double ratio = Double.longBitsToDouble(4652007308841189376L);
   private static double activeRatio = Double.longBitsToDouble(4696837146684686336L);
   private static double pendingRatio = Double.longBitsToDouble(4741671816366391296L);
   private static double currentRatio = Double.longBitsToDouble(4768169126130614272L);
   private static double primaryRatio = Double.longBitsToDouble(4794699203894837248L);
   private static double mainRatio = Double.longBitsToDouble(4815374002031689728L);

   private String handleMessage(double target) {
      if (target <= 0.0) {
         return "§f";
      }

      for (int output = 0; output < this.values.length; output++) {
         if (target <= this.values[output]) {
            return names[output];
         }
      }

      return names[names.length - 1];
   }

   public String loadMessage(TimeUnit target, int input) {
      double output = this.buildRatio(target, this.retrieveTime());
      return this.handleMessage(output) + OpenLocaleBarrier.resolveMessage(output, input);
   }

   public long resolveTime() {
      if (this.loadState()) {
         return 0L;
      }

      long target = 0L;
      long[] output = this.findValuesForValues();

      for (long result : output) {
         target += result;
      }

      return target / (this.resolveState() ? output.length : this.fetchCount());
   }

   public long retrieveTime() {
      return PrimaryLoginService.loadValues()[this.ordinal()];
   }

   PremiumOption(String output, int... context) {
      this(output, false, context);
   }

   private double buildRatio(TimeUnit target, long input) {
      switch (target) {
         case MICROSECONDS:
            return input / ratio;
         case MILLISECONDS:
            return input / activeRatio;
         case SECONDS:
            return input / pendingRatio;
         case MINUTES:
            return input / currentRatio;
         case HOURS:
            return input / primaryRatio;
         case DAYS:
            return input / mainRatio;
         default:
            return input;
      }
   }

   public long[] findValuesForValues() {
      return PrimaryLoginService.findValues()[this.ordinal()];
   }

   PremiumOption(String output, boolean context, int... data) {
      byte value = 5;
      if (data.length != 5) {
         throw new IllegalArgumentException("Times must have length 5!");
      }

      this.name = output;
      this.enabled = context;
      this.values = data;
   }

   public int fetchCount() {
      synchronized (PrimaryLoginService.resolveValues()[this.ordinal()]) {
         return PrimaryLoginService.getValues()[this.ordinal()];
      }
   }

   public boolean loadState() {
      return !this.resolveState() && this.fetchCount() == 0;
   }

   public String resolveMessage(TimeUnit target, int input) {
      double output = this.buildRatio(target, this.resolveTime());
      return this.handleMessage(output) + OpenLocaleBarrier.resolveMessage(output, input);
   }

   public boolean resolveState() {
      return PrimaryLoginService.fetchValues()[this.ordinal()];
   }

   public boolean retrieveState() {
      return this.enabled && this.loadState();
   }
}
