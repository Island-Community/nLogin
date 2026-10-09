package com.nickuc.login.listener.bukkit;

import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.storage.login.LoginCollection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class SettingsFilter implements PluginMessageListener {
   private final BukkitPlatform BukkitPlatform;
   private final LoginCollection loginCollection;

   public SettingsFilter(BukkitPlatform target, LoginCollection input) {
      this.BukkitPlatform = target;
      this.loginCollection = input;
   }

   public void onPluginMessageReceived(@NotNull String target, Player input, byte[] output) {
      if (target.equals("nlogin:addon")) {
         try {
            this.loginCollection.fetchSettingsProcessor().canState(this.BukkitPlatform.b().processVerifiedServerAdapter(input), output);
         } catch (Exception data) {
            PasswordHashContainer.updateMessage("Unable to read nLogin Addon plugin message for " + input.getName(), data);
         }
      }
   }
}
