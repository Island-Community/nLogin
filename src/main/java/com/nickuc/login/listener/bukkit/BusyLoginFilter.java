package com.nickuc.login.listener.bukkit;

import com.nickuc.login.auth.login.LoginCheckpoint;
import org.bukkit.entity.Player;

public class BusyLoginFilter {
   public static final boolean enabled = (boolean)(LoginCheckpoint.handleMethod(Player.class, "isGliding") != null ? 1 : 0);

   public static boolean hasState(Player instance) {
      return enabled && instance.isGliding();
   }

   public static void handlePlayer(Player instance, boolean target) {
      if (enabled) {
         instance.setGliding(target);
      }
   }
}
