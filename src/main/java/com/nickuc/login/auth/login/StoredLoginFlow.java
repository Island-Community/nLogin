package com.nickuc.login.auth.login;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.CheckReturnValue;

public class StoredLoginFlow {
   public static String processMessage(InputStream instance) {
      return handleMessage(instance, StandardCharsets.UTF_8);
   }

   @CheckReturnValue
   public static OpenLoginBarrier processOpenLoginBarrier(InputStream instance) {
      return createOpenLoginBarrier(instance, StandardCharsets.UTF_8);
   }

   public static List<String> processCollection(File instance, Charset target) {
      ArrayList input = new ArrayList();
      if (instance.exists()) {
         OpenLoginBarrier output = buildOpenLoginBarrier(instance, target);

         String context;
         try {
            while ((context = output.findMessage()) != null) {
               input.add(context);
            }
         } catch (Throwable result) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable value) {
                  result.addSuppressed(value);
               }
            }

            throw result;
         }

         if (output != null) {
            output.close();
         }
      }

      return input;
   }

   @CheckReturnValue
   public static OpenLoginBarrier createOpenLoginBarrier(InputStream instance, Charset target) {
      return new OpenLoginBarrier(instance, target);
   }

   public static String buildMessage(File instance, Charset target) {
      List input = processCollection(instance, target);
      return String.join("\n", input);
   }

   public static String handleMessage(InputStream instance, Charset target) {
      List input = loadCollection(instance, target);
      return String.join("\n", input);
   }

   @CheckReturnValue
   public static OpenLoginBarrier buildOpenLoginBarrier(File instance, Charset target) {
      try {
         FileInputStream input = new FileInputStream(instance);
         return createOpenLoginBarrier(input, target);
      } catch (FileNotFoundException output) {
         throw new RuntimeException(output);
      }
   }

   public static List<String> resolveCollection(InputStream instance) {
      return loadCollection(instance, StandardCharsets.UTF_8);
   }

   public static String resolveMessage(File instance) {
      return buildMessage(instance, StandardCharsets.UTF_8);
   }

   public static List<String> resolveCollection(File instance) {
      return processCollection(instance, StandardCharsets.UTF_8);
   }

   public static List<String> loadCollection(InputStream instance, Charset target) {
      ArrayList input = new ArrayList();
      OpenLoginBarrier output = createOpenLoginBarrier(instance, target);

      String context;
      try {
         while ((context = output.findMessage()) != null) {
            input.add(context);
         }
      } catch (Throwable result) {
         if (output != null) {
            try {
               output.close();
            } catch (Throwable value) {
               result.addSuppressed(value);
            }
         }

         throw result;
      }

      if (output != null) {
         output.close();
      }

      return input;
   }

   @CheckReturnValue
   public static OpenLoginBarrier loadOpenLoginBarrier(File instance) {
      return buildOpenLoginBarrier(instance, StandardCharsets.UTF_8);
   }
}
