package com.nickuc.login.limbo;

import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.loader.platform.BukkitLoader;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LimboKeeper {
   private static final boolean enabled = (boolean)(LoginCheckpoint.handleMethod(Player.class, "hidePlayer", Plugin.class, Player.class) != null ? 1 : 0);

   public static void processNLoginBukkit(BukkitPlatform instance, Player target) {
      try {
         BukkitLoader input = instance.retrieveBukkitLoader();

         for (VerifiedServerAdapter context : instance.b().fetchCollection()) {
            if (!context.findState()) {
               Player data = context.findObject();
               executePlugin(input, data, target);
               executePlugin(input, target, data);
            }
         }
      } catch (Exception value) {
         PasswordHashContainer.handleMessage("[Limbo] Unable to remove " + target.getName() + "'s visibility", value);
      }
   }

   private static void sendPlugin(Plugin instance, Player target, Player input) {
      if (enabled) {
         target.showPlayer(instance, input);
      } else {
         target.showPlayer(input);
      }
   }

   private static void executePlugin(Plugin instance, Player target, Player input) {
      if (enabled) {
         target.hidePlayer(instance, input);
      } else {
         target.hidePlayer(input);
      }
   }

   public static void updateNLoginBukkit(BukkitPlatform instance, Player target) {
      try {
         LimboRegistry input = instance.fetchPasswordStore().loadLimboRegistry();
         BukkitLoader output = instance.retrieveBukkitLoader();

         for (VerifiedServerAdapter data : instance.b().fetchCollection()) {
            if (!data.findState() && input.canState(data)) {
               Player value = data.findObject();
               sendPlugin(output, value, target);
               sendPlugin(output, target, value);
            }
         }
      } catch (Exception result) {
         PasswordHashContainer.handleMessage("[Limbo] Unable to restore " + target.getName() + "'s visibility", result);
      }
   }
}
