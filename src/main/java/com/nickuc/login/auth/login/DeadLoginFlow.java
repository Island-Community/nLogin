package com.nickuc.login.auth.login;

import com.nickuc.login.premium.Pbkdf2Linker;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class DeadLoginFlow {
   private static final Pattern pattern = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

   public static UUID createUniqueId(String instance, UUID target) {
      return Pbkdf2Linker.resolveUpstreamLoginOption().createUniqueId(instance, target);
   }

   public static String processMessage(@Nullable UUID instance) {
      return instance != null ? instance.toString().replace("-", "") : null;
   }

   public static UUID buildUniqueId(@Nullable String instance) {
      if (instance != null && !instance.isEmpty()) {
         String target;
         if (instance.contains("-")) {
            target = instance;
         } else {
            target = pattern.matcher(instance).replaceAll("$1-$2-$3-$4-$5");
         }

         return UUID.fromString(target);
      } else {
         return null;
      }
   }

   public static UUID computeUniqueId(String instance) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + instance).getBytes(StandardCharsets.UTF_8));
   }
}
