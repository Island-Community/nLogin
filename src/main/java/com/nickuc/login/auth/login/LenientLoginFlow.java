package com.nickuc.login.auth.login;



public class LenientLoginFlow {
   public final String name;
   public final String activeName;

   public LenientLoginFlow(String target, String input) {
      this.activeName = target;
      this.name = input;
   }
}
