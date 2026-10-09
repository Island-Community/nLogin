package com.nickuc.login.listener.bukkit;

import com.nickuc.login.listener.PacketAdapter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.TabCompleteEvent;

public class CachedLocaleFilter implements PacketAdapter {
   private final Set<String> players;
   private final String name;

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void updateTabCompleteEvent(TabCompleteEvent target) {
      CommandSender input = target.getSender();
      if (input instanceof Player) {
         if (!target.getCompletions().isEmpty()) {
            if (target.getBuffer().startsWith("/")) {
               ArrayList output = new ArrayList(target.getCompletions());
               output.removeIf(targetValue -> {
                  String[] inputValue = targetValue.split(" ");
                  if (inputValue.length == 0) {
                     return false;
                  }

                  String outputValue = inputValue[0].toLowerCase(Locale.ENGLISH);
                  return outputValue.startsWith('/' + this.name + ':') || this.players.contains(outputValue);
               });
               target.setCompletions(output);
            }
         }
      }
   }

   public CachedLocaleFilter(String target, Set<String> input) {
      this.name = target;
      this.players = input;
   }
}
