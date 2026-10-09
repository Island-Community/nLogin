package com.nickuc.login.proxy;

import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.platform.server.NestedServerAdapter;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

public final class SharedBungeeLink extends Command implements NestedServerAdapter, TabExecutor {
   private final PasswordHashCommand<?> passwordHashCommand;
   private final ProxyServer proxyServer;

   public Iterable<String> onTabComplete(CommandSender target, String[] input) {
      List output = this.passwordHashCommand.handleCollection(target, target.getName(), target instanceof ProxiedPlayer, this.getName(), input);
      return output != null ? output : Collections.emptyList();
   }

   @Override
   public void updateTask() {
      this.proxyServer.getPluginManager().registerCommand((Plugin)this.passwordHashCommand.getIndirectSessionHandler().loadObject(), this);
   }

   @Override
   public void saveTask() {
      this.proxyServer.getPluginManager().unregisterCommand(this);
   }

   public void execute(CommandSender target, String[] input) {
      this.passwordHashCommand.saveObject(target, target.getName(), target instanceof ProxiedPlayer, this.getName(), input);
   }

   public SharedBungeeLink(ProxyServer target, PasswordHashCommand<?> input) {
      super(input.loadMessage(), null, input.fetchCollection().toArray(new String[0]));
      this.proxyServer = target;
      this.passwordHashCommand = input;
   }
}
