package com.nickuc.login.security.hashing;

import com.nickuc.login.auth.message.MessageService;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.model.SecondaryMessageKind;

public abstract class DeadPasswordHashHasher extends MessageService {
   private final String name;

   public String buildMessage(String target) {
      String input = SecureLoginHandler.loadMessage(SecondaryMessageKind.PENDING_SECONDARYMESSAGEKIND, 24);
      return this.computeMessage(target, input);
   }

   public String computeMessage(String target, String input) {
      return "$" + this.name + "$" + this.handleMessage(this.handleMessage(target) + input) + "$" + input;
   }

   @Override
   public String computeMessage(String target) {
      return this.buildMessage(target);
   }

   public DeadPasswordHashHasher(String target, String input) {
      super(target);
      this.name = input;
   }

   public String handleMessage(String target) {
      return super.computeMessage(target);
   }

   @Override
   public boolean verifyState(String target, String input) {
      String[] output = input.split("\\$");
      if (output.length != 3 && output.length != 4) {
         return false;
      }

      String context = output[1];
      if (!context.equalsIgnoreCase(this.name)) {
         return false;
      }

      String data = this.handleMessage(target);
      String value = output[2];
      switch (output.length) {
         case 3:
            String[] result = input.split("@");
            if (result.length == 2) {
               String response = result[1];
               return value.equals(this.handleMessage(data + response) + "@" + response);
            } else {
               if (result.length > 0) {
                  return result[0].equals("$" + context + "$" + data);
               }

               return value.equals(data);
            }
         case 4:
            String request = output[3];
            return value.equals(this.handleMessage(data + request));
         default:
            throw new IllegalArgumentException("Unsupported hash parts length for " + super.name + "! " + output.length);
      }
   }
}
