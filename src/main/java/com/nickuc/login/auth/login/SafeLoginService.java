package com.nickuc.login.auth.login;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class SafeLoginService {
   private static final LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.builder().hexColors().build();
   private static final LegacyComponentSerializer activeLegacyComponentSerializer = LegacyComponentSerializer.builder().hexColors().extractUrls().build();

   public static TextComponent computeTextComponent(String instance, boolean target) {
      if (!instance.isEmpty()) {
         LegacyComponentSerializer input = target ? activeLegacyComponentSerializer : legacyComponentSerializer;
         return input.deserialize(instance);
      } else {
         return Component.empty();
      }
   }

   public static String buildMessage(Component instance) {
      return legacyComponentSerializer.serialize(instance);
   }

   public static TextComponent resolveTextComponent(String instance) {
      return computeTextComponent(instance, false);
   }
}
