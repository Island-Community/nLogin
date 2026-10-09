package com.nickuc.login.auth.login;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;

public class RootLoginHandler {
   private static final SecureRandom secureRandom = new SecureRandom();

   public static byte[] handlePayload(byte[] instance, Key target) {
      Cipher input = Cipher.getInstance("RSA");
      input.init(1, target);
      return input.doFinal(instance);
   }

   public static byte[] handlePayloadForPayload(byte[] instance, Key target) {
      Cipher input = Cipher.getInstance("RSA");
      input.init(2, target);
      return input.doFinal(instance);
   }

   public static byte[] handlePayloadAndPayload(byte[] instance, Key target) {
      byte[] input = new byte[instance.length + 4];
      byte[] output = new byte[4];
      secureRandom.nextBytes(output);
      System.arraycopy(output, 0, input, 0, output.length);
      System.arraycopy(instance, 0, input, 4, instance.length);
      return handlePayload(input, target);
   }

   public static byte[] loadPayload(String instance, Key target) {
      return pendingHandlePayload(Base64.getDecoder().decode(instance), target);
   }

   public static String createMessage(byte[] instance, Key target) {
      return Base64.getEncoder().encodeToString(activeHandlePayload(instance, target));
   }

   public static PrivateKey processPrivateKey(byte[] instance) {
      KeyFactory target = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec input = new PKCS8EncodedKeySpec(instance);
      return target.generatePrivate(input);
   }

   public static byte[] getPayload(byte[] instance, Key target) {
      byte[] input = handlePayloadForPayload(instance, target);
      return Arrays.copyOfRange(input, 4, input.length);
   }

   public static PublicKey processPublicKey(byte[] instance) {
      KeyFactory target = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec input = new X509EncodedKeySpec(instance);
      return target.generatePublic(input);
   }

   public static byte[] activeHandlePayload(byte[] instance, Key target) {
      try {
         return handlePayloadAndPayload(instance, target);
      } catch (GeneralSecurityException output) {
         throw new RuntimeException("Unable to encrypt using RSA", output);
      }
   }

   public static byte[] pendingHandlePayload(byte[] instance, Key target) {
      try {
         return getPayload(instance, target);
      } catch (GeneralSecurityException output) {
         throw new RuntimeException("Unable to decrypt using RSA", output);
      }
   }
}
