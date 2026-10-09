package com.nickuc.login.listener.bukkit;

import com.nickuc.login.platform.connection.QuickConnectionContract;
import java.util.Objects;
import org.bukkit.entity.Player;

public class PrimaryTitleFilter implements QuickConnectionContract {
   public PrimaryTitleFilter() {
      Class[] switchSelector = new Class[]{String.class, String.class, int.class, int.class, int.class};
      Objects.requireNonNull(Player.class.getMethod("resetTitle"));
   }

   @Override
   public void dispatchPlayer(Player target) {
      target.resetTitle();
   }

   @Override
   public void performPlayer(Player target, String input, String output, int context, int data, int value) {
      if (input.isEmpty() && output.isEmpty()) {
         this.dispatchPlayer(target);
      } else {
         if (input.isEmpty()) {
            input = "§r";
         }

         if (output.isEmpty()) {
            output = "§r";
         }

         target.sendTitle(input, output, context, data, value);
      }
   }
}
