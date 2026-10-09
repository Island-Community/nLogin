package com.nickuc.login.model;

import com.nickuc.login.auth.login.DeadLoginFlow;
import java.util.UUID;

public enum UpstreamLoginOption {
   UPSTREAM_LOGIN_OPTION,
   ACTIVE_UPSTREAMLOGINOPTION,
   PENDING_UPSTREAMLOGINOPTION;

   public UUID createUniqueId(String target, UUID input) {
      switch (this) {
         case UPSTREAM_LOGIN_OPTION:
            return UUID.randomUUID();
         case ACTIVE_UPSTREAMLOGINOPTION:
            return input != null ? input : DeadLoginFlow.computeUniqueId(target);
         case PENDING_UPSTREAMLOGINOPTION:
            return DeadLoginFlow.computeUniqueId(target);
         default:
            throw new IllegalArgumentException("Unknown unique id type: " + this);
      }
   }
}
