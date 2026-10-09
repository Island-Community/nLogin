package com.nickuc.login.listener;

import com.nickuc.login.listener.bukkit.DiscordGuard;
import com.nickuc.login.loader.platform.BukkitLoader;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import org.bukkit.event.Listener;

public interface PacketAdapter extends OutgoingAccountHandler, Listener {
   @Override
   default void handleObject(Object target) {
      DiscordGuard input = (DiscordGuard)target;
      BukkitLoader output = input.retrieveBukkitLoader();
      output.getServer().getPluginManager().registerEvents(this, output);
   }
}
