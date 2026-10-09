package com.nickuc.login.auth.login;

import com.nickuc.login.platform.listener.ChildListenerContract;


public class QuickLoginBarrier implements ChildListenerContract {
   private final String name;
   private final Object object;
   private final String activeName;

   public String retrieveMessage() {
      return this.name;
   }

   public String getMessage() {
      return this.activeName;
   }

   @Override
   public String toString() {
      return "ServerPlugin(name=" + this.retrieveMessage() + ", version=" + this.getMessage() + ", instance=" + this.retrieveObject() + ")";
   }

   public QuickLoginBarrier(String target, String input, Object output) {
      this.name = target;
      this.activeName = input;
      this.object = output;
   }

   public Object retrieveObject() {
      return this.object;
   }

   @Override
   public <T> T findObject() {
      return (T)this.object;
   }
}
