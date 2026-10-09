package com.nickuc.login.security.hashing;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Hasher;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import com.nickuc.login.platform.player.IndirectPlayerContract;
import com.nickuc.login.storage.spawn.SpawnState;
import java.nio.charset.StandardCharsets;


public class CurrentPasswordHashDigest implements IndirectPlayerContract {
   private final Hasher bCryptHasher;

   @Override
   public boolean canState(String target) {
      return !hasState(target);
   }

   @Override
   public String computeMessage(String target) {
      return this.bCryptHasher.hashToString(SpawnState.ACTIVE_OPEN_SPAWNSTATE.r(), target.toCharArray());
   }

   private static boolean hasState(String instance) {
      return instance.length() == 60 && instance.startsWith("$2");
   }

   public CurrentPasswordHashDigest(Hasher target) {
      this.bCryptHasher = target;
   }

   @Override
   public boolean verifyState(String target, String input) {
      if (!hasState(input)) {
         return false;
      }

      String output = input.contains("@") ? input.split("@")[0] : input;
      Result context = BCrypt.verifyer().verify(target.getBytes(StandardCharsets.UTF_8), output.getBytes(StandardCharsets.UTF_8));
      return context.verified;
   }
}
