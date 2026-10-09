package com.nickuc.login.listener.bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

public final class StoredLoginListener {
   private static final String name = Bukkit.getServer().getClass().getPackage().getName();
   private static final String activeName = name.replace("org.bukkit.craftbukkit", "net.minecraft.server");
   private static final Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
   private static final String pendingName = name.replace("org.bukkit.craftbukkit", "").replace(".", "");
   private static final String currentName = name.replace("org.bukkit.craftbukkit", "net.minecraft");

   private StoredLoginListener() {
   }

   public static String processMessage(String instance) {
      StringBuffer target = new StringBuffer();
      Matcher input = pattern.matcher(instance);

      while (input.find()) {
         String output = input.group(1);
         String context = "";
         switch (output) {
            case "nms":
               context = activeName;
               break;
            case "nm":
               context = currentName;
               break;
            case "obc":
               context = name;
               break;
            case "version":
               context = pendingName;
               break;
            default:
               throw new IllegalArgumentException("Unknown variable: " + output);
         }

         if (!context.isEmpty() && input.end() < instance.length() && instance.charAt(input.end()) != '.') {
            context = context + ".";
         }

         input.appendReplacement(target, Matcher.quoteReplacement(context));
      }

      input.appendTail(target);
      return target.toString();
   }
}
