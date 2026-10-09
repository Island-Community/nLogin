package com.nickuc.login.session;

import com.nickuc.login.auth.login.FastLoginBarrier;
import com.nickuc.login.auth.message.FastMessageHandler;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Encoder;
import javax.crypto.Cipher;

public class Sha256Tracker {
   public static final GeneralSecurityException generalSecurityException = new GeneralSecurityException("Incorrectly signed chat message");
   private static final PublicKey publicKey;
   public static final FastLoginBarrier<String, String> fastLoginBarrier = FastLoginBarrier.createFastLoginBarrier(
      "-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----"
   );
   private static final Encoder base = Base64.getMimeEncoder(76, "\n".getBytes(StandardCharsets.UTF_8));
   private static final KeyFactory keyFactory;
   public static final byte[] values = new byte[0];
   public static final String name = "SHA256withRSA";
   public static final GeneralSecurityException activeGeneralSecurityException = new GeneralSecurityException("Unsigned chat message requested signed preview");
   public static final String activeName = "SHA1withRSA";
   public static final FastLoginBarrier<String, String> activeFastLoginBarrier = FastLoginBarrier.createFastLoginBarrier(
      "-----BEGIN RSA PUBLIC KEY-----", "-----END RSA PUBLIC KEY-----"
   );

   public static PublicKey loadPublicKey(byte[] instance) {
      try {
         return keyFactory.generatePublic(new X509EncodedKeySpec(instance));
      } catch (InvalidKeySpecException input) {
         throw new IllegalArgumentException("Invalid key bytes");
      }
   }

   static {
      try {
         keyFactory = KeyFactory.getInstance("RSA");
      } catch (NoSuchAlgorithmException input) {
         throw new RuntimeException(input);
      }

      try {
         byte[] instance = FastMessageHandler.handlePayload(FastMessageHandler.createInputStream("yggdrasil_session_pubkey.der"));
         publicKey = loadPublicKey(instance);
      } catch (IOException | NullPointerException target) {
         throw new RuntimeException(target);
      }
   }

   public static KeyPair handleKeyPair(int instance) {
      try {
         KeyPairGenerator target = KeyPairGenerator.getInstance("RSA");
         target.initialize(instance);
         return target.generateKeyPair();
      } catch (NoSuchAlgorithmException input) {
         throw new RuntimeException("Unable to generate RSA keypair", input);
      }
   }

   public static byte[] buildPayload(KeyPair instance, byte[] target) {
      Cipher input = Cipher.getInstance("RSA");
      input.init(2, instance.getPrivate());
      return input.doFinal(target);
   }

   public static byte[] resolvePayload(String instance, FastLoginBarrier<String, String> target) {
      int input = instance.indexOf((String)target.findObject());
      if (input < 0) {
         throw new IllegalArgumentException("Start idx cannot be negative!");
      } else {
         int output = ((String)target.findObject()).length();
         int context = instance.indexOf((String)target.loadObject(), output + input) + 1;
         if (context <= 0) {
            throw new IllegalArgumentException("End idx must be positive!");
         } else {
            return handlePayload(instance.substring(input + output, context));
         }
      }
   }

   public static PublicKey resolvePublicKey() {
      return publicKey;
   }

   public static String createMessage(byte[] instance) {
      return new BigInteger(instance).toString(16);
   }

   public static byte[] resolvePayload(long... instance) {
      ByteBuffer target = ByteBuffer.allocate(8 * instance.length).order(ByteOrder.BIG_ENDIAN);

      for (long data : instance) {
         target.putLong(data);
      }

      return target.array();
   }

   public static boolean isState(String instance, PublicKey target, byte[] input, byte[]... output) {
      if (output.length == 0) {
         throw new IllegalArgumentException("toVerify cannot be empty!");
      }

      try {
         Signature context = Signature.getInstance(instance);
         context.initVerify(target);

         for (byte[] request : output) {
            context.update(request);
         }

         return context.verify(input);
      } catch (GeneralSecurityException response) {
         throw new IllegalArgumentException("Invalid signature parameters");
      }
   }

   public static byte[] handlePayload(String instance) {
      return Base64.getMimeDecoder().decode(instance);
   }

   public static byte[] buildPayload(String instance, PrivateKey target, byte[]... input) {
      if (input.length == 0) {
         throw new IllegalArgumentException("toSign cannot be empty!");
      }

      try {
         Signature output = Signature.getInstance(instance);
         output.initSign(target);

         for (byte[] result : input) {
            output.update(result);
         }

         return output.sign();
      } catch (GeneralSecurityException request) {
         throw new IllegalArgumentException("Invalid signature parameters");
      }
   }

   public static String buildMessage(String instance, byte[] target, PublicKey input) {
      try {
         MessageDigest output = MessageDigest.getInstance("SHA-1");
         output.update(instance.getBytes(StandardCharsets.UTF_8));
         output.update(target);
         output.update(input.getEncoded());
         return createMessage(output.digest());
      } catch (NoSuchAlgorithmException context) {
         throw new AssertionError(context);
      }
   }

   public static String computeMessage(Key instance) {
      if (instance == null) {
         throw new IllegalArgumentException("Key cannot be null!");
      }

      FastLoginBarrier target;
      if (instance instanceof PublicKey) {
         target = activeFastLoginBarrier;
      } else {
         if (!(instance instanceof PrivateKey)) {
            throw new IllegalArgumentException("Invalid key type");
         }

         target = fastLoginBarrier;
      }

      return (String)target.findObject() + "\n" + handleMessage(instance.getEncoded()) + "\n" + (String)target.loadObject() + "\n";
   }

   public static String handleMessage(byte[] instance) {
      return base.encodeToString(instance);
   }
}
