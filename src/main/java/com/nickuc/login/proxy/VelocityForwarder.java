package com.nickuc.login.proxy;

import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.platform.server.NestedServerAdapter;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.SimpleCommand.Invocation;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import java.util.Collections;
import java.util.List;


public class VelocityForwarder implements NestedServerAdapter, SimpleCommand {
   private final ProxyServer proxyServer;
   private final PasswordHashCommand<?> passwordHashCommand;

   @Override
   public void saveTask() {
      this.proxyServer.getCommandManager().unregister(this.passwordHashCommand.loadMessage());
   }

   @Override
   public void updateTask() {
      CommandManager target = this.proxyServer.getCommandManager();
      CommandMeta input = target.metaBuilder(this.passwordHashCommand.loadMessage())
         .aliases(this.passwordHashCommand.fetchCollection().toArray(new String[0]))
         .build();
      target.register(input, this);
   }

   public VelocityForwarder(ProxyServer target, PasswordHashCommand<?> input) {
      this.proxyServer = target;
      this.passwordHashCommand = input;
   }

   public List<String> processCollection(Invocation target) {
      CommandSource input = target.source();
      boolean output = input instanceof Player;
      String context = output ? ((Player)input).getUsername() : "CONSOLE";
      List data = this.passwordHashCommand.handleCollection(input, context, output, target.alias(), (String[])target.arguments());
      return data != null ? data : Collections.emptyList();
   }

   public void sendSimpleCommand(Invocation target) {
      CommandSource input = target.source();
      boolean output = input instanceof Player;
      String context = output ? ((Player)input).getUsername() : "CONSOLE";
      this.passwordHashCommand.saveObject(input, context, output, target.alias(), (String[])target.arguments());
   }
}
