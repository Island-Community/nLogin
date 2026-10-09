package com.nickuc.login.auth.login;



public class SecondaryLoginCheckpoint extends VerifiedLoginGate {
   private static final SecondaryLoginCheckpoint secondaryLoginCheckpoint = new SecondaryLoginCheckpoint(null, 0, null);
   private final String name;

   public String retrieveMessage() {
      return this.name;
   }

   public SecondaryLoginCheckpoint(byte[] target, int input, String output) {
      super(target, input);
      this.name = output;
   }
}
