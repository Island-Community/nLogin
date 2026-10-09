package com.nickuc.login.auth.message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import javax.annotation.CheckReturnValue;

public class MessageProcessor {
   public static boolean checkState(String instance, File target) {
      InputStream input = FastMessageHandler.createInputStream(instance);

      try {
         if (input == null) {
            throw new IOException("Unable to find stream in the requested path! " + instance);
         }

         if (!isState(target)) {
            return false;
         }

         updateInputStream(input, target);
         return true;
      } finally {
         if (Collections.singletonList(input).get(0) != null) {
            input.close();
         }
      }
   }

   @CheckReturnValue
   public static BufferedInputStream createBufferedInputStream(File instance, OpenOption... target) {
      return new BufferedInputStream(Files.newInputStream(instance.toPath(), target));
   }

   @CheckReturnValue
   public static BufferedOutputStream handleBufferedOutputStream(File instance, int target, OpenOption... input) {
      return new BufferedOutputStream(Files.newOutputStream(instance.toPath(), input), target);
   }

   public static boolean verifyState(File instance, File target) {
      if (isState(target)) {
         BufferedInputStream input = createBufferedInputStream(instance);

         try {
            BufferedOutputStream output = computeBufferedOutputStream(target);

            try {
               FastMessageHandler.processInputStream(input, output);
               return true;
            } finally {
               if (Collections.singletonList(output).get(0) != null) {
                  output.close();
               }
            }
         } finally {
            if (Collections.singletonList(input).get(0) != null) {
               input.close();
            }
         }
      } else {
         return false;
      }
   }

   public static String handleMessage(File instance) {
      BufferedInputStream target = createBufferedInputStream(instance);

      try {
         byte[] input = FastMessageHandler.handlePayload(target);
         return new String(Base64.getEncoder().encode(input));
      } finally {
         if (Collections.singletonList(target).get(0) != null) {
            target.close();
         }
      }
   }

   public static File buildFile(File instance, String target) {
      int input = 0;
      File output = instance.getParentFile();

      File context;
      do {
         context = new File(output, String.format(target, input++));
      } while (context.exists());

      return context;
   }

   public static File handleFile(Class<?> instance) {
      try {
         String target = instance.getProtectionDomain().getCodeSource().getLocation().getPath();
         String input = URLDecoder.decode(target, "UTF-8");
         return new File(input);
      } catch (UnsupportedEncodingException output) {
         throw new RuntimeException(output);
      }
   }

   public static void updateInputStream(InputStream instance, File target) {
      BufferedOutputStream input = computeBufferedOutputStream(target);

      try {
         FastMessageHandler.processInputStream(instance, input);
      } finally {
         if (Collections.singletonList(input).get(0) != null) {
            input.close();
         }
      }
   }

   @CheckReturnValue
   public static BufferedInputStream handleBufferedInputStream(File instance, int target, OpenOption... input) {
      return new BufferedInputStream(Files.newInputStream(instance.toPath(), input), target);
   }

   public static boolean validateState(File instance) {
      if (instance.isDirectory()) {
         File[] target = instance.listFiles();
         if (target != null) {
            for (File data : target) {
               validateState(data);
            }
         }
      }

      return instance.delete();
   }

   @CheckReturnValue
   public static BufferedOutputStream computeBufferedOutputStream(File instance, OpenOption... target) {
      return new BufferedOutputStream(Files.newOutputStream(instance.toPath(), target));
   }

   public static String createMessage(File instance, MessageDigest target) {
      BufferedInputStream input = createBufferedInputStream(instance);

      try {
         return FastMessageHandler.createMessage(input, target);
      } finally {
         if (Collections.singletonList(input).get(0) != null) {
            input.close();
         }
      }
   }

   public static boolean validateState(File instance, long target) {
      BasicFileAttributes output = Files.readAttributes(instance.toPath(), BasicFileAttributes.class);
      long context = output.creationTime().toMillis();
      return System.currentTimeMillis() - context >= target;
   }

   public static boolean canState(File instance, boolean target) {
      if (!target && instance.exists()) {
         return true;
      }

      File input = instance.getParentFile();
      return input != null && !input.exists() && !input.mkdirs() ? false : instance.createNewFile();
   }

   public static String resolveMessage(File instance) {
      String target = instance.getName();
      String[] input = target.split("\\.");
      return input.length == 1 ? target : String.join(".", Arrays.copyOfRange(input, 0, input.length - 1));
   }

   public static boolean isState(File instance) {
      return canState(instance, false);
   }
}
