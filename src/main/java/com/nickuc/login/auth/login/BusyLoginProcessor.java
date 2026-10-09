package com.nickuc.login.auth.login;



public class BusyLoginProcessor {
   private final boolean enabled;
   private final String[] names;

   private BusyLoginProcessor(String[] target, boolean input) {
      this.names = target;
      this.enabled = input;
   }

   public static BusyLoginProcessor handleBusyLoginProcessor(String... instance) {
      return createBusyLoginProcessor(false, instance);
   }

   public static BusyLoginProcessor createBusyLoginProcessor(boolean instance, String... target) {
      return new BusyLoginProcessor(target, instance);
   }

   public String[] fetchNames() {
      return this.names;
   }

   public boolean fetchState() {
      return this.enabled;
   }
}
