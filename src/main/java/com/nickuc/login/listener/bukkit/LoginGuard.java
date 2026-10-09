package com.nickuc.login.listener.bukkit;


import org.bukkit.Bukkit;

public class LoginGuard {
   public static final LoginGuard loginGuard = createLoginGuard(1, 20, 6);
   public static final LoginGuard activeLoginGuard = createLoginGuard(1, 19, 3);
   public static final LoginGuard pendingLoginGuard = createLoginGuard(1, 11, 0);
   private final int count;
   public static final LoginGuard currentLoginGuard = createLoginGuard(1, 20, 0);
   public static final LoginGuard primaryLoginGuard = createLoginGuard(26, 0, 0);
   public static final LoginGuard mainLoginGuard = createLoginGuard(1, 16, 0);
   public static final LoginGuard localLoginGuard = createLoginGuard(1, 8, 0);
   public static final LoginGuard remoteLoginGuard = createLoginGuard(1, 13, 0);
   public static final LoginGuard cachedLoginGuard = createLoginGuard(1, 19, 0);
   public static final LoginGuard storedLoginGuard = createLoginGuard(1, 18, 0);
   public static final LoginGuard verifiedLoginGuard = createLoginGuard(1, 10, 0);
   private final int activeCount;
   public static final LoginGuard authenticatedLoginGuard = createLoginGuard(1, 9, 0);
   public static final LoginGuard sharedLoginGuard = createLoginGuard(1, 17, 0);
   public static final LoginGuard privateLoginGuard = createLoginGuard(1, 20, 2);
   public static final LoginGuard internalLoginGuard = createLoginGuard(1, 12, 0);
   private final int pendingCount;
   public static final LoginGuard upstreamLoginGuard = createLoginGuard(1, 19, 4);
   public static final LoginGuard incomingLoginGuard = createLoginGuard(1, 15, 0);
   public static final LoginGuard outgoingLoginGuard = createLoginGuard(1, 21, 7);
   private static final LoginGuard secondaryLoginGuard;
   public static final LoginGuard directLoginGuard = createLoginGuard(1, 14, 0);

   public boolean verifyState(LoginGuard target) {
      if (this.pendingCount > target.pendingCount) {
         return true;
      } else if (this.pendingCount < target.pendingCount) {
         return false;
      } else if (this.count > target.count) {
         return true;
      } else {
         return this.count < target.count ? false : this.activeCount > target.activeCount;
      }
   }

   private LoginGuard(int target, int input, int output) {
      this.pendingCount = target;
      this.count = input;
      this.activeCount = output;
   }

   public static LoginGuard resolveLoginGuard() {
      return secondaryLoginGuard;
   }

   public int fetchCount() {
      return this.activeCount;
   }

   static {
      String instance = Bukkit.getVersion();
      String target = "MC: ";
      int input = instance.indexOf(target);
      if (input == -1) {
         throw new IllegalArgumentException("Unsupported Minecraft version! " + instance);
      }

      String output = instance.substring(input);
      int context = output.indexOf(")");
      if (context != -1) {
         output = output.substring(target.length(), context);
         String[] data = output.split(" ");
         if (data.length > 1) {
            output = data[0];
         }
      }

      String[] entry = output.split("\\.");
      if (entry.length < 2) {
         throw new IllegalArgumentException("Unsupported Minecraft version! " + instance + " " + entry.length);
      }

      int value;
      int result;
      int request;
      try {
         value = Integer.parseInt(entry[0]);
         result = Integer.parseInt(entry[1]);
         request = entry.length >= 3 ? Integer.parseInt(entry[2]) : 0;
      } catch (NumberFormatException source) {
         throw new RuntimeException("Unable to determine Minecraft major and minor versions! " + instance);
      }

      secondaryLoginGuard = new LoginGuard(value, result, request);
   }

   @Override
   public int hashCode() {
      byte target = 59;
      int input = 1;
      input = input * 59 + this.resolveCount();
      input = input * 59 + this.retrieveCount();
      return input * 59 + this.fetchCount();
   }

   public boolean canState(LoginGuard target) {
      return !this.isState(target);
   }

   @Override
   public String toString() {
      return this.pendingCount + "." + this.count + "." + this.activeCount;
   }

   public boolean checkState(LoginGuard target) {
      return !this.verifyState(target);
   }

   public int resolveCount() {
      return this.pendingCount;
   }

   @Override
   public boolean equals(Object target) {
      if (target == this) {
         return true;
      } else if (!(target instanceof LoginGuard)) {
         return false;
      } else {
         LoginGuard input = (LoginGuard)target;
         if (!input.validateState(this)) {
            return false;
         } else if (this.resolveCount() != input.resolveCount()) {
            return false;
         } else {
            return this.retrieveCount() != input.retrieveCount() ? false : this.fetchCount() == input.fetchCount();
         }
      }
   }

   private static LoginGuard createLoginGuard(int instance, int target, int input) {
      return new LoginGuard(instance, target, input);
   }

   public boolean isState(LoginGuard target) {
      if (this.pendingCount < target.pendingCount) {
         return true;
      } else if (this.pendingCount > target.pendingCount) {
         return false;
      } else if (this.count < target.count) {
         return true;
      } else {
         return this.count > target.count ? false : this.activeCount < target.activeCount;
      }
   }

   public int retrieveCount() {
      return this.count;
   }

   public boolean validateState(Object target) {
      return target instanceof LoginGuard;
   }
}
