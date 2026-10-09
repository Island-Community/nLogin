package com.nickuc.login.listener.bukkit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import org.bukkit.Location;

public class VerifiedLoginGuard {
   private static final SharedLoginGuard sharedLoginGuard = new SharedLoginGuard();

   public static Location resolveLocation(String instance) {
      try {
         if (instance != null) {
            byte[] target = Base64.getDecoder().decode(instance.getBytes(StandardCharsets.UTF_8));
            ByteArrayInputStream input = new ByteArrayInputStream(target);

            try {
               DataInputStream output = new DataInputStream(input);

               try {
                  return sharedLoginGuard.handleLocation(output);
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
            return null;
         }
      } catch (IOException content) {
         throw new RuntimeException("Unable to decode location!", content);
      }
   }

   public static SharedLoginGuard fetchSharedLoginGuard() {
      return sharedLoginGuard;
   }

   public static String createMessage(Location instance) {
      try {
         ByteArrayOutputStream target = new ByteArrayOutputStream();

         try {
            DataOutputStream input = new DataOutputStream(target);

            try {
               sharedLoginGuard.executeLocation(instance, input);
               return new String(Base64.getEncoder().encode(target.toByteArray()), StandardCharsets.UTF_8);
            } finally {
               if (Collections.singletonList(input).get(0) != null) {
                  input.close();
               }
            }
         } finally {
            if (Collections.singletonList(target).get(0) != null) {
               target.close();
            }
         }
      } catch (IOException element) {
         throw new RuntimeException("Unable to encode location!", element);
      }
   }
}
