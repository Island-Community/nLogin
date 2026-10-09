package com.nickuc.login.platform.connection;

import com.nickuc.login.auth.login.LoudLoginHandler;
import org.bukkit.entity.Player;

public interface QuickConnectionContract {
   static QuickConnectionContract retrieveQuickConnectionContract() {
      return LoudLoginHandler.retrieveQuickConnectionContract();
   }

   void dispatchPlayer(Player target);

   void performPlayer(Player target, String input, String output, int context, int data, int value);
}
