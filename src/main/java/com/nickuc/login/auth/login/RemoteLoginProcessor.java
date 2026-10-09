package com.nickuc.login.auth.login;



public class RemoteLoginProcessor extends BusyLoginHandler {
   public static final int count = 1;
   private final String name;

   public String getKnownName() {
      return this.name;
   }

   @Override
   public String toString() {
      return "IdentityImpl.IdentityByKnownName(knownName=" + this.getKnownName() + ")";
   }

   public RemoteLoginProcessor(String target) {
      this.name = target;
   }
}
