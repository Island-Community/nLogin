package com.nickuc.login.auth.login;

import net.lingala.zip4j.ZipFile;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class ChainedLoginFlow {
   public static void executeFile(File instance, File target) {
      ZipFile input = new ZipFile(instance);

      try {
         input.extractAll(target.toString());
      } catch (Throwable value) {
         try {
            input.close();
         } catch (Throwable data) {
            value.addSuppressed(data);
         }

         throw value;
      }

      input.close();
   }

   public static byte[] loadPayload(byte[] instance) {
      try {
         ByteArrayOutputStream target = new ByteArrayOutputStream();
         DeflaterOutputStream input = new DeflaterOutputStream(target);

         try {
            input.write(instance);
         } catch (Throwable value) {
            try {
               input.close();
            } catch (Throwable data) {
               value.addSuppressed(data);
            }

            throw value;
         }

         input.close();
         return target.toByteArray();
      } catch (IOException result) {
         throw new RuntimeException("Unable to compress with Gzip!", result);
      }
   }

   public static void sendFile(File instance, File target) {
      ZipFile input = new ZipFile(target);

      try {
         if (instance.isDirectory()) {
            input.addFolder(instance);
         } else {
            input.addFile(instance);
         }
      } catch (Throwable value) {
         try {
            input.close();
         } catch (Throwable data) {
            value.addSuppressed(data);
         }

         throw value;
      }

      input.close();
   }

   public static void dispatchCollection(List<File> instance, File target) {
      if (instance.isEmpty()) {
         throw new IllegalArgumentException("Input collection cannot be empty!");
      }

      ZipFile input = new ZipFile(target);

      try {
         for (File context : instance) {
            if (context.isDirectory()) {
               input.addFolder(context);
            } else {
               input.addFile(context);
            }
         }
      } catch (Throwable value) {
         try {
            input.close();
         } catch (Throwable data) {
            value.addSuppressed(data);
         }

         throw value;
      }

      input.close();
   }

   public static byte[] resolvePayload(byte[] instance) {
      try {
         ByteArrayOutputStream target = new ByteArrayOutputStream();
         InflaterOutputStream input = new InflaterOutputStream(target);

         try {
            input.write(instance);
            input.flush();
         } catch (Throwable value) {
            try {
               input.close();
            } catch (Throwable data) {
               value.addSuppressed(data);
            }

            throw value;
         }

         input.close();
         return target.toByteArray();
      } catch (IOException result) {
         throw new RuntimeException("Unable to decompress with Gzip!", result);
      }
   }
}
