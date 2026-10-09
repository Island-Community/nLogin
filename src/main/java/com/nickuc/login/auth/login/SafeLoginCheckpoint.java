package com.nickuc.login.auth.login;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import javax.annotation.CheckReturnValue;

public class SafeLoginCheckpoint {
   @CheckReturnValue
   public static PrintWriter processPrintWriter(File instance, Charset target, boolean input) {
      return new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(instance, true), target)), input);
   }

   public static boolean isState(File instance, Charset target, String... input) {
      if (input.length == 0) {
         return false;
      }

      if (!MessageProcessor.isState(instance)) {
         return false;
      }

      PrintWriter output = processPrintWriter(instance, target, true);

      try {
         for (String result : input) {
            if (result.isEmpty()) {
               output.println();
            } else {
               output.println(result);
            }
         }

         output.flush();
         return true;
      } finally {
         if (Collections.singletonList(output).get(0) != null) {
            output.close();
         }
      }
   }

   public static boolean hasState(File instance, byte[] target) {
      if (MessageProcessor.isState(instance)) {
         ByteArrayInputStream input = new ByteArrayInputStream(target);

         try {
            MessageProcessor.updateInputStream(input, instance);
            return true;
         } finally {
            if (Collections.singletonList(input).get(0) != null) {
               input.close();
            }
         }
      } else {
         return false;
      }
   }

   public static boolean isState(File instance, String... target) {
      return isState(instance, StandardCharsets.UTF_8, target);
   }
}
