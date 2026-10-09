package com.nickuc.login.command.completion;

import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.listener.PacketAdapter;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class PrimaryCompletionAdminCommand implements PacketAdapter {
   private final BukkitPlatform BukkitPlatform;

   public PrimaryCompletionAdminCommand(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void handlePlayerCommandSendEvent(PlayerCommandSendEvent target) {
      if (!this.BukkitPlatform.getState()) {
         Player input = target.getPlayer();
         if (!input.hasPermission("nlogin.command.nlogin") && !input.hasPermission("nlogin.admin")) {
            Collection output = target.getCommands();
            if (output.stream().noneMatch("nlogin"::equalsIgnoreCase)) {
               output.add("nlogin");
            }
         }
      }
   }
}
