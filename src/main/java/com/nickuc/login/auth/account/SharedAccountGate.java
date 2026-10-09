package com.nickuc.login.auth.account;

import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SpawnOption;
import com.nickuc.login.premium.Pbkdf2Linker;


public class SharedAccountGate {
   private final SpawnOption spawnOption;
   private final long timestamp;
   private final String name;

   public String loadMessage() {
      return this.name;
   }

   public SharedAccountGate(long target, String output, SpawnOption context) {
      this.timestamp = target;
      this.name = output;
      this.spawnOption = context;
   }

   public SpawnOption loadSpawnOption() {
      return this.spawnOption;
   }

   public String getName() {
      return this.spawnOption != SpawnOption.PENDING_SPAWNOPTION && QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()
         ? Pbkdf2Linker.handleMessage(this.name, this.spawnOption == SpawnOption.SPAWN_OPTION)
         : this.name;
   }

   public long resolveTime() {
      return this.timestamp;
   }

   @Override
   public String toString() {
      return "PlayerIP.Account(ai=" + this.resolveTime() + ", realName=" + this.loadMessage() + ", type=" + this.loadSpawnOption() + ")";
   }
}
