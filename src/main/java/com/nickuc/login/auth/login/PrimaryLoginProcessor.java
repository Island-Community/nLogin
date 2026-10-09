package com.nickuc.login.auth.login;

import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.premium.LocaleVerifier;
import com.nickuc.login.premium.LoginVerifier;
import java.util.UUID;
import javax.annotation.Nullable;


public class PrimaryLoginProcessor {
   @Nullable
   private final UUID uniqueId;

   public PrimaryLoginProcessor(@Nullable UUID target) {
      this.uniqueId = target;
   }

   public boolean hasState(IndirectSessionHandler<?> target) {
      return this.uniqueId != null && LocaleVerifier.verifyState(target, this.uniqueId);
   }

   @Override
   public String toString() {
      return "UUIDUtil.WrappedUUID(id=" + this.fetchUniqueId() + ")";
   }

   public boolean validateState(@Nullable LoginVerifier target) {
      return this.uniqueId != null && LocaleVerifier.canState(target, this.uniqueId);
   }

   @Nullable
   public UUID fetchUniqueId() {
      return this.uniqueId;
   }
}
