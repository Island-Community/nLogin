package com.nickuc.login.security.hashing;

import de.mkammerer.argon2.Argon2;
import com.nickuc.login.platform.player.IndirectPlayerContract;
import com.nickuc.login.storage.spawn.SpawnState;


public abstract class PasswordHashVerifier implements IndirectPlayerContract {
   private final Argon2 argon2;

   @Override
   public boolean verifyState(String target, String input) {
      return this.argon2.verify(input, target.toCharArray());
   }

   @Override
   public boolean canState(String target) {
      return this.argon2
         .needsRehash(
            target, SpawnState.PENDING_ACTIVE_SPAWNSTATE.r(), SpawnState.PENDING_CURRENT_SPAWNSTATE.r() * 1024, SpawnState.PENDING_PRIMARY_SPAWNSTATE.r()
         );
   }

   @Override
   public String computeMessage(String target) {
      return this.argon2
         .hash(
            SpawnState.PENDING_ACTIVE_SPAWNSTATE.r(),
            SpawnState.PENDING_CURRENT_SPAWNSTATE.r() * 1024,
            SpawnState.PENDING_PRIMARY_SPAWNSTATE.r(),
            target.toCharArray()
         );
   }

   public PasswordHashVerifier(Argon2 target) {
      this.argon2 = target;
   }
}
