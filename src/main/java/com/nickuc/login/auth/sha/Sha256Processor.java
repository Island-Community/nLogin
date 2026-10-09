package com.nickuc.login.auth.sha;

import com.nickuc.login.model.UpstreamSpawnState;
import com.nickuc.login.platform.server.LinkedServerAdapter;
import com.nickuc.login.security.hashing.Sha256Hasher;

public class Sha256Processor implements LinkedServerAdapter {
   private String computeMessage(String target) {
      return ((Sha256Hasher)UpstreamSpawnState.REMOTE_UPSTREAMSPAWNSTATE.loadIndirectPlayerContract()).handleMessage(target);
   }

   @Override
   public boolean verifyState(String target, String input) {
      if (input.contains("@")) {
         input = input.split("@")[0];
      }

      String[] output = input.split("\\$");
      if (output.length != 4) {
         return false;
      }

      if (!output[1].equalsIgnoreCase("SHA")) {
         return false;
      }

      String context = output[2];
      String data = output[3];
      return data.equals(this.computeMessage(this.computeMessage(target) + context));
   }
}
