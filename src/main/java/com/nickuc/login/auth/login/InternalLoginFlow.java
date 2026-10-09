package com.nickuc.login.auth.login;

import com.nickuc.login.platform.server.AuthenticatedServerAdapter;


public class InternalLoginFlow implements AuthenticatedServerAdapter {
   public static final InternalLoginFlow internalLoginFlow = new InternalLoginFlow();

   private InternalLoginFlow() {
   }

   @Override
   public boolean hasState(String target) {
      return true;
   }

   @Override
   public <T> T findObject() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void performMessage(String target) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String getName() {
      return "CONSOLE";
   }

   @Override
   public void dispatchMessage(String target) {
      System.out.println(LocalLocaleFlow.resolveMessage(target, true));
   }
}
