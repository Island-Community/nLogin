package com.nickuc.login.auth.bedrock;

import com.nickuc.login.api.enums.AccountType;

public class BedrockGate {
   static {
      try {
         values[AccountType.PREMIUM.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[AccountType.BEDROCK.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
