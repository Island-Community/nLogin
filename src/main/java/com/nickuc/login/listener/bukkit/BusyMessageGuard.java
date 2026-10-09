package com.nickuc.login.listener.bukkit;

import com.nickuc.login.session.LimboSupervisor;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class BusyMessageGuard implements PluginMessageListener {
   public BusyMessageGuard(LimboSupervisor target) {
      this.limboSupervisor = target;
   }

   public void onPluginMessageReceived(@NotNull String target, Player input, byte[] output) {
      if (target.equals("nlogin:main")) {
         this.limboSupervisor.sendVerifiedServerAdapter(LimboSupervisor.loadNLoginBukkit(this.limboSupervisor).b().processVerifiedServerAdapter(input), output);
      }
   }
}
