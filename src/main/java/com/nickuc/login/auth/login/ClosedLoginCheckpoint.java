package com.nickuc.login.auth.login;

import com.nickuc.login.account.SpawnOption;
import com.nickuc.login.model.PremiumState;
import com.nickuc.login.model.ProxyState;

public class ClosedLoginCheckpoint {
   static {
      try {
         values[PremiumState.MAIN_PREMIUMSTATE.ordinal()] = 1;
      } catch (NoSuchFieldError record) {
      }

      try {
         values[PremiumState.PREMIUM_STATE.ordinal()] = 2;
      } catch (NoSuchFieldError entry) {
      }

      try {
         values[PremiumState.ACTIVE_PREMIUMSTATE.ordinal()] = 3;
      } catch (NoSuchFieldError source) {
      }

      try {
         values[PremiumState.PENDING_PREMIUMSTATE.ordinal()] = 4;
      } catch (NoSuchFieldError response) {
      }

      try {
         values[PremiumState.CURRENT_PREMIUMSTATE.ordinal()] = 5;
      } catch (NoSuchFieldError request) {
      }

      try {
         values[PremiumState.PRIMARY_PREMIUMSTATE.ordinal()] = 6;
      } catch (NoSuchFieldError result) {
      }

      activeValues = new int[ProxyState.values().length];

      try {
         activeValues[ProxyState.ACTIVE_PROXYSTATE.ordinal()] = 1;
      } catch (NoSuchFieldError value) {
      }

      try {
         activeValues[ProxyState.PROXY_STATE.ordinal()] = 2;
      } catch (NoSuchFieldError data) {
      }

      pendingValues = new int[SpawnOption.values().length];

      try {
         pendingValues[SpawnOption.PENDING_SPAWNOPTION.ordinal()] = 1;
      } catch (NoSuchFieldError context) {
      }

      try {
         pendingValues[SpawnOption.SPAWN_OPTION.ordinal()] = 2;
      } catch (NoSuchFieldError output) {
      }

      try {
         pendingValues[SpawnOption.ACTIVE_SPAWNOPTION.ordinal()] = 3;
      } catch (NoSuchFieldError input) {
      }

      try {
         pendingValues[SpawnOption.CURRENT_SPAWNOPTION.ordinal()] = 4;
      } catch (NoSuchFieldError target) {
      }
   }
}
