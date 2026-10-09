package com.nickuc.login.security.hashing;

import com.nickuc.login.auth.message.MessageService;

public final class PendingPasswordHashDigest extends MessageService {
   @Override
   public String computeMessage(String target) {
      return "$MD5$" + super.computeMessage(target);
   }

   public String loadMessage(String target) {
      return super.computeMessage(target);
   }

   public PendingPasswordHashDigest() {
      super("MD5");
   }

   @Override
   public boolean verifyState(String target, String input) {
      if (input.contains("@")) {
         input = input.split("@")[0];
      }

      String[] output = input.split("\\$");
      if (output.length != 3 && output.length != 4) {
         return false;
      }

      String context = output[1];
      if (!context.equalsIgnoreCase("MD5")) {
         return false;
      }

      String data = output[2];
      String value = super.computeMessage(target);
      switch (output.length) {
         case 3:
            return data.equals(value);
         case 4:
            String result = output[3];
            return data.equals(super.computeMessage(value + result));
         default:
            throw new IllegalArgumentException("Unsupported hash parts length for MD5! " + output.length);
      }
   }
}
