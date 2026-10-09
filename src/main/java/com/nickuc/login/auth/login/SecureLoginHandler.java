package com.nickuc.login.auth.login;

import com.nickuc.login.model.SecondaryMessageKind;
import java.security.SecureRandom;
import java.util.Random;

public class SecureLoginHandler {
   private static final Random random = new Random();
   private static final SecureRandom secureRandom = new SecureRandom();

   public static int resolveCount(int instance) {
      return instance == 0 ? 0 : random.nextInt(instance);
   }

   public static int handleCount(int instance, int target) {
      if (instance == target) {
         throw new IllegalArgumentException("Min and max cannot be equals!");
      }

      if (instance < 0) {
         throw new IllegalArgumentException("Min must be higher or equals 0!");
      }

      if (target <= 0) {
         throw new IllegalArgumentException("Max must be higher than 0!");
      }

      if (instance > target) {
         int input = target;
         target = instance;
         instance = input;
      }

      int context = target - instance;
      int output = resolveCount(context);
      return instance + output;
   }

   public static Random retrieveRandom() {
      return random;
   }

   public static String loadMessage(SecondaryMessageKind instance, int target) {
      return handleMessage(SecondaryMessageKind.resolveValues(instance), target);
   }

   public static String handleMessage(char[] instance, int target) {
      StringBuilder input = new StringBuilder();

      for (int output = 0; output < target; output++) {
         input.append(instance[secureRandom.nextInt(instance.length)]);
      }

      return input.toString();
   }

   public static String processMessage(String instance, int target) {
      return handleMessage(instance.toCharArray(), target);
   }
}
