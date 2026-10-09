package com.nickuc.login.model;

import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.auth.message.MessageProcessor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public enum PlatformCatalog {
   PLATFORM_CATALOG("MD5", "md5"),
   ACTIVE_PLATFORMCATALOG("SHA-1", "sha1"),
   PENDING_PLATFORMCATALOG("SHA-256", "sha256");

   private final String name;
   private final String activeName;

   public MessageDigest findMessageDigest() {
      try {
         return MessageDigest.getInstance(this.name);
      } catch (NoSuchAlgorithmException input) {
         throw new RuntimeException(input);
      }
   }

   public String buildMessage(File target) {
      try {
         return MessageProcessor.createMessage(target, this.findMessageDigest());
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public boolean hasState(String target, String input) {
      String output = this.computeMessage(target);
      return output.equals(input);
   }

   public String createMessage(byte[] target) {
      MessageDigest input = this.findMessageDigest();
      input.reset();
      input.update(target);
      byte[] output = input.digest();
      return String.format("%0" + (output.length << 1) + "x", new BigInteger(1, output));
   }

   public String loadMessage() {
      return this.activeName;
   }

   public boolean canState(InputStream target, String input) {
      String output = this.loadMessage(target);
      return output.equals(input);
   }

   PlatformCatalog(String output, String context) {
      this.name = output;
      this.activeName = context;
   }

   public String loadMessage(InputStream target) {
      try {
         return FastMessageHandler.createMessage(target, this.findMessageDigest());
      } catch (IOException output) {
         throw new RuntimeException(output);
      }
   }

   public boolean validateState(File target, String input) {
      String output = this.buildMessage(target);
      return output.equals(input);
   }

   public String computeMessage(String target) {
      return this.createMessage(target.getBytes());
   }
}
