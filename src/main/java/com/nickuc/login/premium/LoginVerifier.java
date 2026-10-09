package com.nickuc.login.premium;

import com.nickuc.login.auth.login.LenientLoginFlow;
import java.util.Arrays;
import java.util.UUID;


public class LoginVerifier {
   private final UUID uniqueId;
   private final String name;
   private static final LenientLoginFlow[] values = new LenientLoginFlow[0];
   private final LenientLoginFlow[] activeValues;

   public UUID fetchUniqueId() {
      return this.uniqueId;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public String toString() {
      return "PremiumResponse(id=" + this.fetchUniqueId() + ", name=" + this.getName() + ", properties=" + Arrays.deepToString(this.findValues()) + ")";
   }

   public LoginVerifier(UUID target, String input, LenientLoginFlow[] output) {
      this.uniqueId = target;
      this.name = input;
      this.activeValues = output;
   }

   public LenientLoginFlow[] findValues() {
      return this.activeValues;
   }

   public LoginVerifier(UUID target, String input) {
      this(target, input, values);
   }
}
