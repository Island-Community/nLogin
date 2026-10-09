package com.nickuc.login.auth.sha;

import com.nickuc.login.platform.server.LinkedServerAdapter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Gate implements LinkedServerAdapter {
   private static final char[] values = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   public static final String name = "CRAZYLOGIN";

   @Override
   public boolean verifyState(String target, String input) {
      String[] output = input.split("\\$");
      if (output.length != 4) {
         return false;
      }

      String context = output[2];
      String data = output[3];
      String value = "ÜÄaeut//&/=I " + target + "7421€547" + context + "__+IÄIH§%NK " + target;
      return data.equals(this.createMessage(value));
   }

   private String createMessage(String target) {
      MessageDigest input;
      try {
         input = MessageDigest.getInstance("SHA-512");
      } catch (NoSuchAlgorithmException context) {
         throw new RuntimeException(context);
      }

      input.update(target.getBytes(StandardCharsets.UTF_8), 0, target.length());
      return handleMessage(input.digest());
   }

   private static String handleMessage(byte... instance) {
      char[] target = new char[instance.length * 2];

      for (int input = 0; input < instance.length; input++) {
         target[input * 2] = values[instance[input] >> 4 & 15];
         target[input * 2 + 1] = values[instance[input] & 15];
      }

      return new String(target);
   }
}
