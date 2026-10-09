package com.nickuc.login.storage.sha;

import com.nickuc.login.auth.login.PrivateLoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.server.LinkedServerAdapter;
import com.nickuc.login.security.hashing.Pbkdf2Digest;

public class Sha256Collection extends Pbkdf2Digest implements LinkedServerAdapter {
   @Override
   public boolean verifyState(String target, String input) {
      String[] output = input.split("\\$");
      if (output.length != 4) {
         return false;
      } else if (!output[0].equalsIgnoreCase("pbkdf2_sha256")) {
         return false;
      } else {
         Integer context = PrivateLoginCheckpoint.createInteger(output[1]);
         if (context == null) {
            PasswordHashContainer.performMessage("[AUTHME_PBKDF2_SHA256] Cannot read iteration count for PBKDF2: \"" + output[1] + "\"");
            return false;
         } else {
            String data = output[2];
            byte[] value = handlePayload(output[3]);
            byte[] result = computePayload("HmacSHA256", target.toCharArray(), data.getBytes(), context, resolveCount("HmacSHA256"));
            return Pbkdf2Digest.isState(value, result);
         }
      }
   }

   private static byte[] handlePayload(String instance) {
      int target = instance.length();
      byte[] input = new byte[target / 2];

      for (byte output = 0; output < target; output += 2) {
         input[output / 2] = (byte)((Character.digit(instance.charAt(output), 16) << 4) + Character.digit(instance.charAt(output + 1), 16));
      }

      return input;
   }
}
