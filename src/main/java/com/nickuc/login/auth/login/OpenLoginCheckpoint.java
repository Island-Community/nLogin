package com.nickuc.login.auth.login;

import com.nickuc.login.premium.SpawnLookup;
import javax.annotation.Nullable;


public class OpenLoginCheckpoint {
   @Nullable
   public final SpawnLookup spawnLookup;

   public OpenLoginCheckpoint(@Nullable SpawnLookup target) {
      this.spawnLookup = target;
   }

   public SpawnLookup resolveSpawnLookup(String target) {
      return this.spawnLookup != null ? this.spawnLookup : SpawnLookup.handleSpawnLookup(target);
   }
}
