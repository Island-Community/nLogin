package com.nickuc.login.auth.login;

import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class AuthenticatedLoginCheckpoint {
   public static byte[] createPayload(PendingPasswordHashHasher instance, byte[] target) {
      instance.updateMessage("Content-Encoding", "gzip");

      try {
         ByteArrayOutputStream input = new ByteArrayOutputStream();
         GZIPOutputStream output = new GZIPOutputStream(input);

         try {
            output.write(target);
         } catch (Throwable result) {
            try {
               output.close();
            } catch (Throwable value) {
               result.addSuppressed(value);
            }

            throw result;
         }

         output.close();
         return input.toByteArray();
      } catch (IOException request) {
         throw new RuntimeException("Unable to compress with Gzip!", request);
      }
   }

   public static byte[] createPayload(Object... instance) {
      if (instance.length % 2 != 0) {
         throw new IllegalArgumentException("Need both key and value for parameters!");
      }

      StringBuilder target = new StringBuilder();

      for (int input = 0; input < instance.length; input++) {
         if (target.length() > 0) {
            target.append("&");
         }

         Object output = instance[input++];
         if (output == null) {
            throw new IllegalArgumentException("Key cannot be null!");
         }

         Object context = instance[input];

         try {
            target.append(URLEncoder.encode(output.toString(), "UTF-8")).append("=").append(URLEncoder.encode(context == null ? "null" : context.toString(), "UTF-8"));
         } catch (UnsupportedEncodingException value) {
            throw new RuntimeException(value);
         }
      }

      return target.toString().getBytes(StandardCharsets.UTF_8);
   }
}
