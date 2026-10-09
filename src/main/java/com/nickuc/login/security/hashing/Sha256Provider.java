package com.nickuc.login.security.hashing;

import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.server.LinkedServerAdapter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Sha256Provider implements LinkedServerAdapter {
   private static final char[] values = "abcdefghijklopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789".toCharArray();
   public static final String name = "$LOCKLOGIN";

   @Override
   public boolean verifyState(String target, String input) {
      input = input.substring(0, input.length() - "$LOCKLOGIN".length());
      String[] output = input.split("\\$");
      String context = output[1];
      Pattern data = Pattern.compile("\\$" + context + "\\$(\\d\\d\\d?)\\$(.{512})");
      if (context.length() <= 1) {
         data = Pattern.compile("\\$" + context + "\\$(\\d\\d\\d?)\\$(.{512})");
      }

      Matcher value = data.matcher(input);
      if (!value.matches()) {
         return false;
      }

      int result = processCount(Integer.parseInt(value.group(1)));
      byte[] request = Base64.getUrlDecoder().decode(value.group(2));
      byte[] response = Arrays.copyOfRange(request, 0, 256);

      for (char item : values) {
         String element = target + item;
         byte[] content = resolvePayload(element.toCharArray(), response, result);
         int payload = 0;

         for (int holder = 0; holder < content.length; holder++) {
            payload |= request[response.length + holder] ^ content[holder];
         }

         if (payload == 0) {
            return true;
         }
      }

      return false;
   }

   private static int processCount(int instance) {
      if ((instance & -513) != 0) {
         throw new IllegalArgumentException("cost: " + instance);
      } else {
         return 1 << instance;
      }
   }

   private static byte[] resolvePayload(char[] instance, byte[] target, int input) {
      PBEKeySpec output = new PBEKeySpec(instance, target, input, 1024);

      try {
         SecretKeyFactory context = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
         return context.generateSecret(output).getEncoded();
      } catch (InvalidKeySpecException data) {
         PasswordHashContainer.handleMessage("[LockLogin Algorithm] Invalid SecretKeyFactory!", data);
      } catch (NoSuchAlgorithmException value) {
         PasswordHashContainer.handleMessage("[LockLogin Algorithm] No such algorithm: PBKDF2WithHmacSHA512", value);
      }

      return new byte[1];
   }
}
