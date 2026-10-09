package com.nickuc.login.platform.premium;


import org.bukkit.Bukkit;
import org.bukkit.Server;

public enum SilentPremiumOption {
   SILENT_PREMIUM_OPTION("git-Spigot", false),
   ACTIVE_SILENTPREMIUMOPTION("git-Paper", false),
   PENDING_SILENTPREMIUMOPTION("git-Folia", false),
   CURRENT_SILENTPREMIUMOPTION("git-Purpur", false),
   PRIMARY_SILENTPREMIUMOPTION("git-Bukkit", false),
   MAIN_SILENTPREMIUMOPTION("git-Taco", false),
   LOCAL_SILENTPREMIUMOPTION("git-Torch", false),
   REMOTE_SILENTPREMIUMOPTION("Mohist", true),
   CACHED_SILENTPREMIUMOPTION("Magma", true),
   STORED_SILENTPREMIUMOPTION("Arclight", true),
   VERIFIED_SILENTPREMIUMOPTION("Thermos", true),
   AUTHENTICATED_SILENTPREMIUMOPTION("Cauldron", true),
   SHARED_SILENTPREMIUMOPTION("Crucible", true);

   private final String name;
   private final boolean enabled;
   private static final SilentPremiumOption silentPremiumOption;

   public String getName() {
      return this.name;
   }

   public boolean retrieveState() {
      return this.enabled;
   }

   SilentPremiumOption(String output, boolean context) {
      this.name = output;
      this.enabled = context;
   }

   public static SilentPremiumOption fetchSilentPremiumOption() {
      return silentPremiumOption;
   }

   static {
      SilentPremiumOption instance = null;
      Server target = Bukkit.getServer();
      String input = target.getVersion();
      String output = target.getName();
      SilentPremiumOption[] context = values();

      for (SilentPremiumOption request : context) {
         if (input.contains(request.getName()) || output != null && output.contains(request.getName())) {
            instance = request;
            break;
         }
      }

      if (instance == null) {
         instance = context[0];
      }

      silentPremiumOption = instance;
   }
}
