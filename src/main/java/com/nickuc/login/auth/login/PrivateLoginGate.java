package com.nickuc.login.auth.login;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;

public class PrivateLoginGate {
   public static String fetchMessage() {
      return System.getProperty("java.version");
   }

   public static OperatingSystemMXBean getOperatingSystemMXBean() {
      return ManagementFactory.getOperatingSystemMXBean();
   }

   public static void processCount(int instance) {
      try {
         LoginCheckpoint.handleMethod(System.class, "e".concat("xi").concat("t"), int.class).invoke(null, instance);
      } catch (IllegalAccessException | InvocationTargetException input) {
         throw new RuntimeException(input);
      }
   }

   public static int loadCount() {
      String instance = fetchMessage();
      if (instance.startsWith("1.")) {
         instance = instance.substring(2, 3);
      } else {
         int target = instance.indexOf(".");
         if (target != -1) {
            instance = instance.substring(0, target);
         }
      }

      StringBuilder context = new StringBuilder();

      for (int input = 0; input < instance.length(); input++) {
         char output = instance.charAt(input);
         if (!PrivateLoginCheckpoint.canState(output)) {
            break;
         }

         context.append(output);
      }

      if (context.length() == 0) {
         throw new RuntimeException("Unable to determine this Java version! " + fetchMessage());
      } else {
         return Integer.parseInt(context.toString());
      }
   }
}
