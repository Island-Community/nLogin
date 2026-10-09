package com.nickuc.login.command;

import com.nickuc.login.platform.server.AuthenticatedServerAdapter;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeProcessor implements AuthenticatedServerAdapter {
   private final CommandSender commandSender;
   private final ProxyServer proxyServer;

   private BungeeProcessor(ProxyServer target, CommandSender input) {
      this.proxyServer = target;
      this.commandSender = input;
   }

   @Override
   public void dispatchMessage(String target) {
      this.commandSender.sendMessage(TextComponent.fromLegacyText(target));
   }

   @Override
   public String getName() {
      return this.commandSender.getName();
   }

   public static BungeeProcessor loadBungeeProcessor(ProxyServer instance, CommandSender target) {
      return new BungeeProcessor(instance, target);
   }

   @Override
   public boolean hasState(String target) {
      return this.commandSender.hasPermission(target);
   }

   @Override
   public <T> T findObject() {
      return (T)this.commandSender;
   }

   @Override
   public void performMessage(String target) {
      if (target.length() >= 2 && target.charAt(0) == '/') {
         target = target.substring(1);
      }

      this.proxyServer.getPluginManager().dispatchCommand(this.commandSender, target);
   }
}
