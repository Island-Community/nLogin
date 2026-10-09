package com.nickuc.login.account;

import java.util.function.BiFunction;

public enum SpawnOption {
   SPAWN_OPTION,
   ACTIVE_SPAWNOPTION,
   PENDING_SPAWNOPTION,
   CURRENT_SPAWNOPTION;

   public String buildMessage(boolean target) {
      return this.buildMessage(target, (instance, targetValue) -> instance + targetValue);
   }

   public String buildMessage(boolean target, BiFunction<String, String, String> input) {
      switch (this) {
         case SPAWN_OPTION:
            return (String)input.apply("§6", "premium");
         case ACTIVE_SPAWNOPTION:
            return (String)input.apply("§c", "offline");
         case PENDING_SPAWNOPTION:
            return (String)input.apply("§6", "bedrock");
         case CURRENT_SPAWNOPTION:
            return (String)input.apply("§c", target ? "sem registro" : "unregistered");
         default:
            throw new IllegalArgumentException("Invalid account type " + this + "!");
      }
   }
}
