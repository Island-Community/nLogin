package com.nickuc.login.auth.locale;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class OpenLocaleBarrier {
   private static final DecimalFormat[] values = computeValues(5);
   private static final DecimalFormat decimalFormat = new DecimalFormat("#,###");
   private static double ratio = Double.longBitsToDouble(4636737291354636288L);

   private static DecimalFormat[] computeValues(int instance) {
      DecimalFormat[] target = new DecimalFormat[instance];

      for (int input = 0; input < instance; input++) {
         DecimalFormat output = buildDecimalFormat(input + 1);
         target[input] = output;
      }

      return target;
   }

   public static String handleMessage(double instance, double input, int context, String data) {
      int value = (int)(instance * ratio / input);
      char result;
      if (value <= 20) {
         result = 'a';
      } else if (value <= 40) {
         result = '2';
      } else if (value <= 60) {
         result = '6';
      } else if (value <= 80) {
         result = 'c';
      } else {
         result = '4';
      }

      int request = (int)(instance * context / input);
      return "§" + result + buildMessage(data, request) + "§7" + buildMessage(data, context - request);
   }

   public static String processMessage(double instance) {
      return decimalFormat.format(instance);
   }

   public static String buildMessage(String instance, int target) {
      if (target <= 0) {
         return "";
      }

      int input = instance.length();
      char[] output = new char[input * target];
      char[] context = instance.toCharArray();

      for (int data = 0; data < output.length; data++) {
         output[data] = context[data % input];
      }

      return new String(output);
   }

   public static String handleMessage(double instance, double input) {
      return resolveMessage(instance, input, 20, ":");
   }

   public static String handleMessage(String instance) {
      if (instance.isEmpty()) {
         return instance;
      }

      String[] target = instance.split(" ");
      if (target.length == 0) {
         return instance;
      }

      for (int input = 0; input < target.length; input++) {
         target[input] = buildMessage(target[input]);
      }

      return String.join(" ", target);
   }

   public static String handleMessage(String instance, Object... target) {
      if (target != null && target.length > 0) {
         int input = instance.length();
         StringBuilder output = new StringBuilder(input);

         label46:
         for (int context = 0; context < input; context++) {
            char data = instance.charAt(context);
            if (data == '{') {
               int value = context + 1;

               while (true) {
                  if (value >= input) {
                     output.append(instance.substring(context));
                     return output.toString();
                  }

                  char result = instance.charAt(value);
                  if (result == '}') {
                     if (value - context >= 2) {
                        String request = instance.substring(context + 1, value);
                        Integer response = PrivateLoginCheckpoint.createInteger(request);
                        if (response == null) {
                           throw new IllegalArgumentException(
                              "Error in format method (not a number), select=\"" + request + "\", raw=\"" + instance + "\", pos=" + context
                           );
                        }

                        if (response < target.length) {
                           output.append(target[response]);
                           context += value - context;
                           continue label46;
                        }
                     }
                     break;
                  }

                  if (!PrivateLoginCheckpoint.canState(result)) {
                     break;
                  }

                  value++;
               }
            }

            output.append(data);
         }

         return output.toString();
      } else {
         return instance;
      }
   }

   public static String resolveMessage(double instance, double input, int context, String data) {
      int value = (int)(instance * context / input);
      return "§a" + buildMessage(data, value) + "§7" + buildMessage(data, context - value);
   }

   public static String resolveMessage(double instance, int input) {
      DecimalFormat output = values.length <= input ? buildDecimalFormat(input) : values[input - 1];
      return output.format(instance);
   }

   public static String buildMessage(String instance) {
      if (instance.isEmpty()) {
         return instance;
      }

      char[] target = instance.toCharArray();
      target[0] = Character.toUpperCase(target[0]);
      return new String(target);
   }

   public static String createMessage(String instance, String target, String input) {
      String output = instance.toLowerCase();
      String context = target.toLowerCase();
      int data = 0;
      StringBuilder value = new StringBuilder(instance);

      while ((data = output.indexOf(context, data)) != -1) {
         value.replace(data, data + context.length(), input);
         data += input.length();
      }

      return value.toString();
   }

   public static String loadMessage(String... instance) {
      StringBuilder target = new StringBuilder();

      for (int input = 0; input < instance.length; input++) {
         target.append(instance[input]);
         if (input != instance.length - 1) {
            target.append("§r\n§r");
         }
      }

      return target.toString();
   }

   public static String resolveMessage(long instance) {
      return decimalFormat.format(instance);
   }

   private static DecimalFormat buildDecimalFormat(int instance) {
      return new DecimalFormat("0." + buildMessage("0", instance), DecimalFormatSymbols.getInstance(Locale.ENGLISH));
   }

   public static String computeMessage(int instance) {
      return "0x" + Integer.toHexString(instance).toUpperCase(Locale.ENGLISH);
   }
}
