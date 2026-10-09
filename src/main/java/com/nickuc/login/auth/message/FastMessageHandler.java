package com.nickuc.login.auth.message;

import com.nickuc.login.loader.MemClassLoader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;
import java.util.function.Consumer;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

public class FastMessageHandler {
   @CheckReturnValue
   public static ByteArrayInputStream processByteArrayInputStream(byte[] instance) {
      return new ByteArrayInputStream(Base64.getDecoder().decode(instance));
   }

   @CheckReturnValue
   @Nullable
   public static InputStream buildInputStream(String instance, boolean target) {
      ClassLoader input = FastMessageHandler.class.getClassLoader();
      if (!(input instanceof MemClassLoader)) {
         throw new IllegalArgumentException("Invalid class loader! " + input.getClass().getCanonicalName());
      }

      MemClassLoader output = (MemClassLoader)input;
      InputStream context = target ? output.getParentLoader().getResourceAsStream(instance) : output.getInJarResourceAsStream(instance);
      if (context == null) {
         context = target ? output.getParentLoader().getResourceAsStream('/' + instance) : output.getInJarResourceAsStream('/' + instance);
      }

      return context;
   }

   @CheckReturnValue
   public static byte[] processPayload(InputStream instance, int target) {
      ByteArrayOutputStream input = new ByteArrayOutputStream();

      try {
         executeInputStream(instance, input, target);
         return input.toByteArray();
      } finally {
         if (Collections.singletonList(input).get(0) != null) {
            input.close();
         }
      }
   }

   public static void executeInputStream(InputStream instance, OutputStream target, int input) {
      byte[] output = new byte[input];

      int context;
      while ((context = instance.read(output, 0, input)) != -1) {
         target.write(output, 0, context);
      }

      target.flush();
   }

   @CheckReturnValue
   @Nullable
   public static InputStream createInputStream(String instance) {
      return buildInputStream(instance, false);
   }

   public static String createMessage(InputStream instance, MessageDigest target) {
      byte[] input = new byte[8192];

      int output;
      while ((output = instance.read(input)) > 0) {
         target.update(input, 0, output);
      }

      byte[] context = target.digest();
      StringBuilder data = new StringBuilder();

      for (byte response : context) {
         data.append(Integer.toString((response & 255) + 256, 16).substring(1));
      }

      return data.toString();
   }

   public static byte[] buildPayload(Consumer<ParentLoginCheckpoint> instance) {
      ByteArrayOutputStream target = new ByteArrayOutputStream();
      ParentLoginCheckpoint input = new ParentLoginCheckpoint(new DataOutputStream(target));
      instance.accept(input);
      return target.toByteArray();
   }

   @CheckReturnValue
   public static byte[] handlePayload(InputStream instance) {
      return processPayload(instance, 4096);
   }

   @CheckReturnValue
   public static ByteArrayInputStream computeByteArrayInputStream(String instance, Charset target) {
      return processByteArrayInputStream(instance.getBytes(target));
   }

   @CheckReturnValue
   public static ByteArrayInputStream loadByteArrayInputStream(String instance) {
      return computeByteArrayInputStream(instance, StandardCharsets.UTF_8);
   }

   public static void processInputStream(InputStream instance, OutputStream target) {
      executeInputStream(instance, target, 4096);
   }
}
