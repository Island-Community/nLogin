package com.nickuc.login.platform.proxy;



public enum SilentProxyState {
   SILENT_PROXY_STATE("Bukkit", "Bukkit", false),
   ACTIVE_SILENTPROXYSTATE("BungeeCord", "Bungee", true),
   PENDING_SILENTPROXYSTATE("Velocity", "Velocity", true);

   private final String name;
   private final String activeName;
   private final boolean enabled;

   public boolean loadState() {
      return this.enabled;
   }

   @Override
   public String toString() {
      return this.name;
   }

   SilentProxyState(String output, String context, boolean data) {
      this.name = output;
      this.activeName = context;
      this.enabled = data;
   }

   public String getName() {
      return this.name;
   }

   public String resolveMessage() {
      return this.activeName;
   }
}
