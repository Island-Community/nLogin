package com.nickuc.login.auth.mojang;

import java.util.UUID;
import javax.annotation.Nullable;


public class MojangService extends BusyLoginHandler {
   private final String name;
   public static final int count = 0;
   @Nullable
   private final UUID uniqueId;
   @Nullable
   private final UUID activeUniqueId;

   @Nullable
   public UUID getBedrockId() {
      return this.uniqueId;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public String toString() {
      return "IdentityImpl.IdentityByData(name=" + this.getName() + ", mojangId=" + this.getMojangId() + ", bedrockId=" + this.getBedrockId() + ")";
   }

   @Nullable
   public UUID getMojangId() {
      return this.activeUniqueId;
   }

   public MojangService(String target, @Nullable UUID input, @Nullable UUID output) {
      this.name = target;
      this.activeUniqueId = input;
      this.uniqueId = output;
   }
}
