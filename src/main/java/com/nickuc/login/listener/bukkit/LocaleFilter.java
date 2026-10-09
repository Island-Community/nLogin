package com.nickuc.login.listener.bukkit;

import com.nickuc.login.listener.PacketAdapter;
import java.util.Locale;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class LocaleFilter implements PacketAdapter {
   private final Set<String> players;
   private final String name;

   public LocaleFilter(String target, Set<String> input) {
      this.name = target;
      this.players = input;
   }

   @EventHandler(priority = EventPriority.LOW)
   public void handlePlayerCommandSendEvent(PlayerCommandSendEvent target) {
      target.getCommands().removeIf(targetValue -> {
         targetValue = targetValue.toLowerCase(Locale.ENGLISH);
         return targetValue.startsWith(this.name + ':') || this.players.contains(targetValue);
      });
   }
}
