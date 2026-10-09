package com.nickuc.login.model;

import javax.annotation.Nullable;


public enum LenientPremiumOption {
   LENIENT_PREMIUM_OPTION("português brasileiro", "br", "pt", true),
   ACTIVE_LENIENTPREMIUMOPTION("english", "en", true),
   PENDING_LENIENTPREMIUMOPTION("español", "es", true),
   CURRENT_LENIENTPREMIUMOPTION("polski", "pl"),
   PRIMARY_LENIENTPREMIUMOPTION("pусский", "ru", true),
   MAIN_LENIENTPREMIUMOPTION("française", "fr"),
   LOCAL_LENIENTPREMIUMOPTION("lietuvių", "lt"),
   REMOTE_LENIENTPREMIUMOPTION("deutsch", "de"),
   CACHED_LENIENTPREMIUMOPTION("türkçe", "tr"),
   STORED_LENIENTPREMIUMOPTION("magyar", "hu"),
   VERIFIED_LENIENTPREMIUMOPTION("simplified chinese", "cn", true),
   AUTHENTICATED_LENIENTPREMIUMOPTION("čeština", "cz"),
   SHARED_LENIENTPREMIUMOPTION("românesc", "ro"),
   PRIVATE_LENIENTPREMIUMOPTION("українська", "ua", "uk", false),
   INTERNAL_LENIENTPREMIUMOPTION("indonesia", "id"),
   UPSTREAM_LENIENTPREMIUMOPTION("italiano", "it"),
   INCOMING_LENIENTPREMIUMOPTION("português", "pt"),
   OUTGOING_LENIENTPREMIUMOPTION("tiếng việt", "vn"),
   SECONDARY_LENIENTPREMIUMOPTION("اللغة العربية", "ar"),
   DIRECT_LENIENTPREMIUMOPTION("Български", "bg"),
   LINKED_LENIENTPREMIUMOPTION("日本語", "jp", "ja", false),
   ROOT_LENIENTPREMIUMOPTION(null, "other", null, false);

   public final String name;
   public final String activeName;
   public final String pendingName;
   public final String currentName;
   public final String primaryName;
   public final int count;

   public static LenientPremiumOption handleLenientPremiumOption(String instance) {
      if (instance != null) {
         for (LenientPremiumOption context : values()) {
            if (context != ROOT_LENIENTPREMIUMOPTION && instance.equalsIgnoreCase(context.activeName)) {
               return context;
            }
         }
      }

      return ROOT_LENIENTPREMIUMOPTION;
   }

   @Nullable
   public static LenientPremiumOption buildLenientPremiumOption(@Nullable String instance) {
      if (instance != null && instance.length() >= 2) {
         String target = instance.substring(0, 2);

         for (LenientPremiumOption data : values()) {
            if (data != ROOT_LENIENTPREMIUMOPTION && target.equals(data.primaryName)) {
               return data;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   LenientPremiumOption(String output, String context, boolean data) {
      this(output, context, context, data);
   }

   LenientPremiumOption(String output, String context, String data, boolean value) {
      this.activeName = context;
      this.pendingName = output == null ? "messages_en.yml" : "messages_" + context + ".yml";
      this.name = output;
      this.currentName = value ? context : "en";
      this.primaryName = data;
      this.count = value ? this.ordinal() + 1 : -1;
   }

   public static LenientPremiumOption resolveLenientPremiumOption(String instance) {
      if (instance != null) {
         for (LenientPremiumOption context : values()) {
            if (context != ROOT_LENIENTPREMIUMOPTION && instance.equalsIgnoreCase(context.pendingName)) {
               return context;
            }
         }
      }

      return ROOT_LENIENTPREMIUMOPTION;
   }

   public static LenientPremiumOption computeLenientPremiumOption(int instance) {
      for (LenientPremiumOption context : values()) {
         if (context != ROOT_LENIENTPREMIUMOPTION && context.count != -1 && instance == context.count) {
            return context;
         }
      }

      return ROOT_LENIENTPREMIUMOPTION;
   }

   LenientPremiumOption(String output, String context) {
      this(output, context, context, false);
   }

   public String findMessage() {
      return this.name;
   }
}
