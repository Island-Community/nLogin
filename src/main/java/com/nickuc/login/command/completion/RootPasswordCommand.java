package com.nickuc.login.command.completion;

import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.server.NestedServerAdapter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class RootPasswordCommand implements NestedServerAdapter, CommandExecutor, TabCompleter {
   private final Server server;
   private final PasswordHashCommand<?> passwordHashCommand;
   private PluginCommand pluginCommand;

   public RootPasswordCommand(Server target, PasswordHashCommand<?> input) {
      this.server = target;
      this.passwordHashCommand = input;
   }

   public List<String> onTabComplete(CommandSender target, Command input, String output, String[] context) {
      return this.passwordHashCommand.handleCollection(target, target.getName(), target instanceof Player, output, context);
   }

   private PluginCommand loadPluginCommand() {
      try {
         Constructor target = LoginCheckpoint.buildConstructor(PluginCommand.class, String.class, Plugin.class);
         PluginCommand input = (PluginCommand)target.newInstance(
            this.passwordHashCommand.loadMessage(), this.passwordHashCommand.getIndirectSessionHandler().loadObject()
         );
         input.setAliases(this.passwordHashCommand.fetchCollection());
         String output = this.passwordHashCommand.findMessage();
         input.setDescription(output == null ? "This command does not have a description." : output);
         input.setExecutor(this);
         input.setTabCompleter(this);
         return input;
      } catch (Exception context) {
         PasswordHashContainer.handleMessage("Unable to create command: " + context.getLocalizedMessage(), context);
         return null;
      }
   }

   public boolean onCommand(CommandSender target, Command input, String output, String[] context) {
      this.passwordHashCommand.saveObject(target, target.getName(), target instanceof Player, output, context);
      return true;
   }

   @Override
   public void updateTask() {
      this.pluginCommand = this.loadPluginCommand();
      if (this.pluginCommand != null) {
         try {
            PluginManager target = this.server.getPluginManager();
            Field input = LoginCheckpoint.resolveField(target.getClass(), "commandMap");
            Object output = input.get(target);
            if (output instanceof CommandMap) {
               CommandMap context = (CommandMap)output;
               context.register(this.passwordHashCommand.getIndirectSessionHandler().retrieveMessage().toLowerCase(Locale.ENGLISH), this.pluginCommand);
            }
         } catch (Exception data) {
            PasswordHashContainer.handleMessage("Unable to register command: " + data.getLocalizedMessage(), data);
         }
      }
   }

   @Override
   public synchronized void saveTask() {
      if (this.pluginCommand != null) {
         try {
            PluginManager target = this.server.getPluginManager();
            Field input = LoginCheckpoint.resolveField(target.getClass(), "commandMap");
            Object output = input.get(target);
            if (output instanceof CommandMap) {
               CommandMap context = (CommandMap)output;
               List data = this.pluginCommand.getAliases();
               this.pluginCommand.setAliases(Collections.emptyList());
               this.pluginCommand.unregister(context);
               if (context instanceof SimpleCommandMap) {
                  Field value = LoginCheckpoint.resolveField(SimpleCommandMap.class, "knownCommands");
                  Map result = (Map)value.get(context);
                  String request = this.passwordHashCommand.getIndirectSessionHandler().retrieveMessage().toLowerCase(Locale.ENGLISH);
                  String response = this.pluginCommand.getName();
                  result.remove(request + ":" + response);
                  result.remove(response);

                  for (String entry : data) {
                     result.remove(request + ":" + entry);
                     result.remove(entry);
                  }
               }
            }
         } catch (Exception record) {
            PasswordHashContainer.handleMessage("Unable to unregister command: " + record.getLocalizedMessage(), record);
         }
      }
   }
}
