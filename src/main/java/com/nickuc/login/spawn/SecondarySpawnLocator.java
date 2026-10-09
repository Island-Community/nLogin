package com.nickuc.login.spawn;

import com.nickuc.login.api.enums.SpawnType;

public class SecondarySpawnLocator {
   static {
      try {
         values[SpawnType.JOIN.ordinal()] = 1;
      } catch (NoSuchFieldError data) {
      }

      try {
         values[SpawnType.FIRST_JOIN.ordinal()] = 2;
      } catch (NoSuchFieldError context) {
      }

      try {
         values[SpawnType.LOGIN.ordinal()] = 3;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[SpawnType.REGISTER.ordinal()] = 4;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[SpawnType.RESPAWN.ordinal()] = 5;
      } catch (NoSuchFieldError target) {
      }
   }
}
