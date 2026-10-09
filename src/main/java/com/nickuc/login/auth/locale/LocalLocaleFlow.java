package com.nickuc.login.auth.locale;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalLocaleFlow {
   public static final String name = "0123456789AaBbCcDdEeFf";
   private static final Pattern pattern = Pattern.compile("([&§])([A-Fa-f0-9KkLlMmNnOoRr])");
   private static final Pattern activePattern = Pattern.compile("[&§]#([A-Fa-f0-9]{6})");
   public static final char marker = '#';
   private static final Pattern pendingPattern = Pattern.compile("[§]#([A-Fa-f0-9]{6})");
   public static final char activeMarker = 167;
   public static final String activeName = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
   private static final Pattern currentPattern = Pattern.compile("([§])([A-Fa-f0-9KkLlMmNnOoRr])");
   public static final char pendingMarker = '&';

   public static String buildMessage(String instance) {
      if (instance != null && !instance.isEmpty()) {
         char[] target = instance.toCharArray();

         for (int input = 0; input < target.length; input++) {
            char output = target[input];
            switch (output) {
               case ' ':
                  break;
               case '#':
                  int data = input + 7;
                  if (target.length >= data + 1) {
                     for (int value = input + 1; value < data; value++) {
                        char result = target[value];
                        if ("0123456789AaBbCcDdEeFf".indexOf(result) == -1) {
                           target[value] = Character.toUpperCase(target[value]);
                           return new String(target);
                        }
                     }

                     input += 6;
                  }
                  break;
               case '&':
               case '§':
                  if (target.length >= input + 2) {
                     char context = target[input + 1];
                     if (context != '#') {
                        if ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(context) <= -1) {
                           target[input + 1] = Character.toUpperCase(context);
                           return new String(target);
                        }

                        input++;
                     }
                  }
                  break;
               default:
                  target[input] = Character.toUpperCase(target[input]);
                  return new String(target);
            }
         }

         return new String(target);
      } else {
         return instance;
      }
   }

   public static String handleMessage(String instance) {
      return processMessage(resolveMessage(instance));
   }

   public static String loadMessage(String instance) {
      return processMessage(instance, '&', '§', true, true);
   }

   public static String resolveMessage(String instance, boolean target) {
      return instance != null && !instance.isEmpty() ? (target ? currentPattern : pattern).matcher(instance).replaceAll("") : instance;
   }

   private static String processMessage(String instance, char target, char input, boolean output, boolean context) {
      if (instance != null && !instance.isEmpty()) {
         char[] data = instance.toCharArray();

         label42:
         for (int value = 0; value < data.length - 1; value++) {
            if (data[value] == target) {
               char result = data[value + 1];
               if (context && result == '#' && data.length > value + 7) {
                  for (int request = value + 2; request <= value + 7; request++) {
                     char response = data[request];
                     if ("0123456789AaBbCcDdEeFf".indexOf(response) == -1) {
                        continue label42;
                     }
                  }

                  data[value] = input;
                  value += 7;
               } else if (output && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(result) > -1) {
                  data[value] = input;
                  data[++value] = Character.toLowerCase(data[value]);
               }
            }
         }

         return new String(data);
      } else {
         return instance;
      }
   }

   public static String resolveMessage(String instance) {
      return resolveMessage(instance, false);
   }

   public static String processMessage(String instance) {
      return processMessage(instance, false);
   }

   public static String handleMessageForMessage(String instance) {
      return processMessage(instance, '&', '§', false, true);
   }

   public static String loadMessage(String instance, boolean target) {
      if (instance != null && !instance.isEmpty()) {
         Matcher input = (target ? pendingPattern : activePattern).matcher(instance);
         StringBuffer output = new StringBuffer(instance.length() + 32);

         while (input.find()) {
            String context = input.group(1).toLowerCase(Locale.ROOT);
            input.appendReplacement(
               output, "§x§" + context.charAt(0) + '§' + context.charAt(1) + '§' + context.charAt(2) + '§' + context.charAt(3) + '§' + context.charAt(4) + '§' + context.charAt(5)
            );
         }

         return input.appendTail(output).toString();
      } else {
         return instance;
      }
   }

   public static String processMessage(String instance, boolean target) {
      return instance != null && !instance.isEmpty() ? (target ? pendingPattern : activePattern).matcher(instance).replaceAll("") : instance;
   }

   public static String createMessage(String instance, boolean target) {
      return processMessage(resolveMessage(instance, target), target);
   }

   public static String createMessage(String instance) {
      return processMessage(instance, '§', '&', true, true);
   }

   public static String buildMessageForMessage(String instance) {
      return processMessage(instance, '&', '§', true, false);
   }
}
