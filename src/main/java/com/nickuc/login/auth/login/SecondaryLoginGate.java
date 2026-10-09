package com.nickuc.login.auth.login;

import com.nickuc.login.platform.account.ParentAccountHandler;


public class SecondaryLoginGate {
   private static ParentAccountHandler parentAccountHandler;

   public static void performParentAccountHandler(ParentAccountHandler instance) {
      parentAccountHandler = instance;
   }

   public static ParentAccountHandler fetchParentAccountHandler() {
      return parentAccountHandler;
   }
}
