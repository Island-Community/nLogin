package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.platform.server.AuthenticatedServerAdapter;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;


public class LocalVelocityGateway implements AuthenticatedServerAdapter {
   private final ConsoleCommandSource consoleCommandSource;
   private final ProxyServer proxyServer;

   @Override
   public void performMessage(String target) {
      if (target.length() >= 2 && target.charAt(0) == '/') {
         target = target.substring(1);
      }

      this.proxyServer.getCommandManager().executeImmediatelyAsync(this.consoleCommandSource, target);
   }

   @Override
   public <T> T findObject() {
      return (T)this.consoleCommandSource;
   }

   @Override
   public void dispatchMessage(String target) {
      this.consoleCommandSource.sendMessage(SafeLoginService.resolveTextComponent(target));
   }

   public static LocalVelocityGateway createLocalVelocityGateway(ProxyServer instance, ConsoleCommandSource target) {
      return new LocalVelocityGateway(instance, target);
   }

   @Override
   public String getName() {
      return "CONSOLE";
   }

   private LocalVelocityGateway(ProxyServer target, ConsoleCommandSource input) {
      this.proxyServer = target;
      this.consoleCommandSource = input;
   }

   @Override
   public boolean hasState(String target) {
      return this.consoleCommandSource.hasPermission(target);
   }
}
