package com.nickuc.login.auth.message;

import com.nickuc.login.platform.player.IndirectPlayerContract;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public abstract class MessageService implements IndirectPlayerContract {
   public final String name;

   @Override
   public boolean canState(String target) {
      return target.contains("@");
   }

   public MessageService(String target) {
      this.name = target;
   }

   @Override
   public boolean verifyState(String target, String input) {
      return false;
   }

   @Override
   public String computeMessage(String target) {
      try {
         MessageDigest input = MessageDigest.getInstance(this.name);
         input.reset();
         input.update(target.getBytes());
         byte[] output = input.digest();
         return String.format("%0" + (output.length << 1) + "x", new BigInteger(1, output));
      } catch (NoSuchAlgorithmException context) {
         throw new RuntimeException(context);
      }
   }
}
