package com.nickuc.login.listener.bukkit;

import com.nickuc.login.platform.connection.QuickConnectionContract;
import org.bukkit.entity.Player;

public class LoginListener implements QuickConnectionContract {
   @Override
   public void performPlayer(Player target, String input, String output, int context, int data, int value) {
   }

   @Override
   public void dispatchPlayer(Player target) {
   }
}
