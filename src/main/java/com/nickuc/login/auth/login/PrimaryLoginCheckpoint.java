package com.nickuc.login.auth.login;

import com.nickuc.login.model.LoudPremiumOption;

public class PrimaryLoginCheckpoint {
   static {
      try {
         values[LoudPremiumOption.CURRENT_LOUDPREMIUMOPTION.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[LoudPremiumOption.ACTIVE_LOUDPREMIUMOPTION.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
