package com.nickuc.login.auth.sha;

import com.nickuc.login.platform.server.LinkedServerAdapter;
import com.nickuc.login.security.hashing.Pbkdf2Digest;
import java.util.Arrays;
import java.util.Base64;

public class IncomingSha256Barrier extends Pbkdf2Digest implements LinkedServerAdapter {
   private static final char[] values = "abcdefghijklopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789".toCharArray();

   @Override
   public boolean verifyState(String target, String input) {
      String[] output = input.split("\\$");
      if (output.length != 4) {
         return false;
      }

      int context = 1 << Integer.parseInt(output[2]);
      byte[] data = Base64.getUrlDecoder().decode(output[3]);
      byte[] value = Arrays.copyOfRange(data, 0, 256);

      for (char source : values) {
         byte[] entry = computePayload("HmacSha512", (target + source).toCharArray(), value, context, 1024);
         int record = 0;

         for (int item = 0; item < entry.length; item++) {
            record |= data[value.length + item] ^ entry[item];
         }

         if (record == 0) {
            return true;
         }
      }

      return false;
   }
}
