package com.nickuc.login.spawn;



public class LoginLocator {
   private final String name;
   private final String activeName;

   public String findMessage() {
      return this.name;
   }

   public LoginLocator(String target, String input) {
      this.name = target;
      this.activeName = input;
   }

   public String loadMessage() {
      return this.activeName;
   }

   public static LoginLocator resolveLoginLocator(String instance, String target) {
      return new LoginLocator(target.replace("{}", "."), instance);
   }

   @Override
   public String toString() {
      return "Relocation(pattern=" + this.findMessage() + ", relocatedPattern=" + this.loadMessage() + ")";
   }
}
