package com.nickuc.login.command;

import com.nickuc.login.auth.login.InternalLoginFlow;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.platform.server.AuthenticatedServerAdapter;

import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;

public class SettingsAction implements AuthenticatedServerAdapter {
   private final Server server;
   private final ConsoleCommandSender consoleCommandSender;

   @Override
   public <T> T findObject() {
      return (T)this.consoleCommandSender;
   }

   public static AuthenticatedServerAdapter processAuthenticatedServerAdapter(Server instance, ConsoleCommandSender target) {
      return target != null ? new SettingsAction(instance, target) : InternalLoginFlow.internalLoginFlow;
   }

   @Override
   public void performMessage(String target) {
      if (target.length() >= 2 && target.charAt(0) == '/') {
         target = target.substring(1);
      }

      if (this.server.isPrimaryThread()) {
         this.server.dispatchCommand(this.consoleCommandSender, target);
      } else {
         String input = target;
         BungeeWriter.executeRunnable(() -> this.server.dispatchCommand(this.consoleCommandSender, input));
      }
   }

   @Override
   public boolean hasState(String target) {
      return this.consoleCommandSender.hasPermission(target);
   }

   @Override
   public void dispatchMessage(String target) {
      this.consoleCommandSender.sendMessage(target);
   }

   @Override
   public String getName() {
      return this.consoleCommandSender.getName();
   }

   private SettingsAction(Server target, ConsoleCommandSender input) {
      this.server = target;
      this.consoleCommandSender = input;
   }
}
