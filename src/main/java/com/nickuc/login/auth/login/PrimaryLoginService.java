package com.nickuc.login.auth.login;

import com.nickuc.login.spawn.PremiumOption;

public class PrimaryLoginService {
   private static final long[][] values = new long[PrimaryLoginService.count][500];
   private static final int count = PremiumOption.values().length;
   private static final Object[] activeValues = handleValues(count);
   private static final long[] pendingValues = new long[count];
   private static final int[] currentValues = new int[count];
   private static final boolean[] primaryValues = new boolean[count];

   private static Object[] handleValues(int instance) {
      Object[] target = new Object[instance];

      for (int input = 0; input < target.length; input++) {
         target[input] = new Object();
      }

      return target;
   }

   public static void savePremiumOption(PremiumOption instance, long target) {
      long output = System.nanoTime() - target;
      int data = instance.ordinal();
      synchronized (activeValues[data]) {
         int result = currentValues[data]++;
         values[data][result] = output;
         pendingValues[data] = output;
         if (result == 499) {
            primaryValues[data] = true;
            currentValues[data] = 0;
         }
      }
   }
}
