package com.nickuc.login.auth.account;

import java.util.Collection;
import javax.annotation.Nullable;


public class LocalAccountGate {
   private final Collection<SharedAccountGate> collection;
   private final String name;

   public boolean isState(@Nullable Long target, int input) {
      return this.collection.size() >= input && !this.validateState(target);
   }

   public Collection<SharedAccountGate> loadCollection() {
      return this.collection;
   }

   public boolean canState(int target) {
      return this.collection.size() >= target;
   }

   public boolean validateState(@Nullable Long target) {
      return target != null && !this.loadState() && this.collection.stream().anyMatch(targetValue -> SharedAccountGate.loadTime(targetValue) == target);
   }

   @Override
   public String toString() {
      return "PlayerIP(ip=" + this.retrieveMessage() + ", accounts=" + this.loadCollection() + ")";
   }

   public boolean loadState() {
      return this.collection.isEmpty();
   }

   public LocalAccountGate(String target, Collection<SharedAccountGate> input) {
      this.name = target;
      this.collection = input;
   }

   public String retrieveMessage() {
      return this.name;
   }
}
