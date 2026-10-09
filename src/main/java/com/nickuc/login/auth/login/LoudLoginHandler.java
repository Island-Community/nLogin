package com.nickuc.login.auth.login;

import com.nickuc.login.listener.bukkit.LoginListener;
import com.nickuc.login.listener.bukkit.PrimaryTitleFilter;
import com.nickuc.login.listener.bukkit.TitleFilter;
import com.nickuc.login.platform.connection.QuickConnectionContract;

public class LoudLoginHandler {
   private static QuickConnectionContract quickConnectionContract;

   public static QuickConnectionContract retrieveQuickConnectionContract() {
      return quickConnectionContract;
   }

   static {
      try {
         quickConnectionContract = new PrimaryTitleFilter();
      } catch (ReflectiveOperationException input) {
      }

      try {
         if (quickConnectionContract == null) {
            quickConnectionContract = new TitleFilter();
         }
      } catch (ReflectiveOperationException target) {
      }

      if (quickConnectionContract == null) {
         quickConnectionContract = new LoginListener();
      }
   }
}
