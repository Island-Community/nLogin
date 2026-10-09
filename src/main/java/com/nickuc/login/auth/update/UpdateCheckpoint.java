package com.nickuc.login.auth.update;

import javax.annotation.CheckReturnValue;

public class UpdateCheckpoint {
   @CheckReturnValue
   public static SecondaryUpdateCheckpoint handleSecondaryUpdateCheckpoint(String instance, int target, String input, Object... output) {
      if (output.length % 2 != 0) {
         throw new IllegalArgumentException("Not in key and value format!");
      }

      int context = output.length - target * 2;
      StringBuilder data = new StringBuilder();
      data.append("UPDATE `").append(instance).append("` SET ");

      for (byte value = 0; value < context; value += 2) {
         if (value != 0) {
            data.append(", ");
         }

         data.append("`").append(output[value]).append("`").append(" = ?");
      }

      data.append(" WHERE ");

      for (int response = context; response < output.length; response += 2) {
         if (response > context) {
            data.append(" AND ");
         }

         data.append("`").append(output[response]).append("`").append(" = ?");
      }

      data.append(" ").append(input);
      Object[] source = new Object[output.length / 2];
      int result = 0;

      for (byte request = 1; request < output.length; request += 2) {
         source[result++] = output[request];
      }

      return new SecondaryUpdateCheckpoint(data.toString(), source);
   }

   @CheckReturnValue
   public static SecondaryUpdateCheckpoint processSecondaryUpdateCheckpoint(String instance, Object... target) {
      if (target.length % 2 != 0) {
         throw new IllegalArgumentException("Not in key and value format!");
      }

      StringBuilder input = new StringBuilder();
      input.append("INSERT INTO `").append(instance).append("` (");

      for (byte output = 0; output < target.length; output += 2) {
         if (output != 0) {
            input.append(", ");
         }

         input.append("`").append(target[output]).append("`");
      }

      input.append(") VALUES (");

      for (int value = 0; value < target.length / 2; value++) {
         if (value != 0) {
            input.append(", ");
         }

         input.append("?");
      }

      input.append(")");
      Object[] result = new Object[target.length / 2];
      int context = 0;

      for (byte data = 1; data < target.length; data += 2) {
         result[context++] = target[data];
      }

      return new SecondaryUpdateCheckpoint(input.toString(), result);
   }
}
