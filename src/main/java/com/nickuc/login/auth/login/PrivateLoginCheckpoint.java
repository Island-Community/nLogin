package com.nickuc.login.auth.login;

import java.text.DecimalFormat;
import javax.annotation.Nullable;

public class PrivateLoginCheckpoint {
   private static double ratio = Double.longBitsToDouble(4652218415073722368L);
   private static final String[] names = new String[]{"B", "KiB", "MiB", "GiB", "TiB"};
   private static double activeRatio = Double.longBitsToDouble(4636737291354636288L);
   private static final double pendingRatio = Math.log10(ratio);
   private static double currentRatio = Double.longBitsToDouble(4652218415073722368L);
   private static final DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
   private static final double[] values = new double[names.length];

   @Nullable
   public static Short processShort(String instance) {
      return createShort(instance, null);
   }

   static {
      for (int instance = 0; instance < values.length; instance++) {
         values[instance] = Math.pow(currentRatio, instance);
      }
   }

   public static Integer handleInteger(String instance, Integer target) {
      if (instance == null) {
         return target;
      }

      try {
         return Integer.valueOf(instance);
      } catch (NumberFormatException output) {
         return target;
      }
   }

   public static double resolveRatio(long instance, long input) {
      return instance * activeRatio / input;
   }

   public static Double buildDouble(String instance, Double target) {
      if (instance == null) {
         return target;
      }

      try {
         return Double.valueOf(instance);
      } catch (NumberFormatException output) {
         return target;
      }
   }

   @Nullable
   public static Integer createInteger(String instance) {
      return handleInteger(instance, null);
   }

   @Nullable
   public static Long createLong(String instance) {
      return resolveLong(instance, null);
   }

   @Nullable
   public static Double createDouble(String instance) {
      return buildDouble(instance, null);
   }

   public static Long resolveLong(String instance, Long target) {
      if (instance == null) {
         return target;
      }

      try {
         return Long.valueOf(instance);
      } catch (NumberFormatException output) {
         return target;
      }
   }

   public static boolean canState(char instance) {
      return "0123456789".indexOf(instance) > -1;
   }

   public static String computeMessage(long instance) {
      if (instance <= 0L) {
         return "0 B";
      }

      int input = (int)(Math.log10(instance) / pendingRatio);
      double output = instance / values[input];
      return decimalFormat.format(output) + " " + names[input];
   }

   public static Short createShort(String instance, Short target) {
      if (instance == null) {
         return target;
      }

      try {
         return Short.valueOf(instance);
      } catch (NumberFormatException output) {
         return target;
      }
   }
}
