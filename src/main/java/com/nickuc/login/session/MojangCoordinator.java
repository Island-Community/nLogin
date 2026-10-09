package com.nickuc.login.session;

public class MojangCoordinator {
   public static final String name = createMessage(null, "nlogin.unique-id", "NLoginUniqueID");
   public static final boolean enabled = canState("nlogin.force-geyser-forms", "ForceGeyserFormsForNLogin");
   public static final boolean activeEnabled = canState("nlogin.disable-proxy-mode", "ForceDisableProxyMode");
   public static boolean pendingEnabled = canState("nlogin.print-player-listeners", "PrintPlayerListenersForNLogin");
   public static final String activeName = createMessage("https://sessionserver.mojang.com", "minecraft.api.session.host");

   private static boolean canState(String... instance) {
      String target = createMessage(null, instance);
      return "1".equals(target) || "true".equalsIgnoreCase(target);
   }

   private static String createMessage(String instance, String... target) {
      for (String value : target) {
         String input;
         if ((input = System.getProperty(value)) != null) {
            return input;
         }
      }

      return instance;
   }
}
