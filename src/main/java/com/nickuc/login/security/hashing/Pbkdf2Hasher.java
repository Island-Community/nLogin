package com.nickuc.login.security.hashing;

import com.nickuc.login.platform.player.IndirectPlayerContract;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.Base64;

public class Pbkdf2Hasher extends Pbkdf2Digest implements IndirectPlayerContract {
   @Override
   public boolean canState(String target) {
      return false;
   }

   @Override
   public boolean verifyState(String target, String input) {
      String[] output = input.split("\\$");
      if (output.length != 6) {
         return false;
      }

      String context = output[2];
      int data = Integer.parseInt(output[3]);
      int value = resolveCount(context);
      byte[] result = Base64.getUrlDecoder().decode(output[4]);
      byte[] request = Base64.getUrlDecoder().decode(output[5]);
      byte[] response = computePayload(context, target.toCharArray(), result, data, value);
      return isState(request, response);
   }

   @Override
   public String computeMessage(String target) {
      String input = SpawnState.ACTIVE_LIVE_SPAWNSTATE.a(new Object[0]);
      int output = SpawnState.ACTIVE_READY_SPAWNSTATE.r();
      return "$PBKDF2$" + input + "$" + loadMessage(input, output, target);
   }
}
