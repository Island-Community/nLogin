package com.nickuc.login.security.hashing;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public abstract class Pbkdf2Digest {
   private static final SecureRandom secureRandom = new SecureRandom();

   public static int resolveCount(String instance) {
      try {
         return Mac.getInstance(instance).getMacLength() * 16;
      } catch (NoSuchAlgorithmException input) {
         throw new RuntimeException("[PBKDF2] No such algorithm: " + instance + "!", input);
      }
   }

   public static boolean isState(byte[] instance, byte[] target) {
      int input = instance.length ^ target.length;

      for (int output = 0; output < instance.length && output < target.length; output++) {
         input |= instance[output] ^ target[output];
      }

      return input == 0;
   }

   public static byte[] computePayload(String instance, char[] target, byte[] input, int output, int context) {
      PBEKeySpec data = new PBEKeySpec(target, input, output, context);

      try {
         SecretKeyFactory value = SecretKeyFactory.getInstance("PBKDF2With" + instance);
         return value.generateSecret(data).getEncoded();
      } catch (InvalidKeySpecException result) {
         throw new RuntimeException("[PBKDF2] Invalid SecretKeyFactory!", result);
      } catch (NoSuchAlgorithmException request) {
         throw new RuntimeException("[PBKDF2] No such algorithm: " + instance + "!", request);
      }
   }

   public static String loadMessage(String instance, int target, String input) {
      int output = resolveCount(instance);
      byte[] context = new byte[output / 4];
      secureRandom.nextBytes(context);
      byte[] data = computePayload(instance, input.toCharArray(), context, target, output);
      return String.format(
         "%s$%s$%s", target, Base64.getUrlEncoder().withoutPadding().encodeToString(context), Base64.getUrlEncoder().withoutPadding().encodeToString(data)
      );
   }
}
