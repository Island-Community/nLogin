package com.nickuc.login.command;

import com.nickuc.login.config.PasswordHashContainer;
import com.google.common.collect.Multimap;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Handler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class SpawnCompletionCommand {
   public static void updatePlugin(Plugin instance) {
      PluginManager target = ProxyServer.getInstance().getPluginManager();
      ClassLoader input = instance.getClass().getClassLoader();

      try {
         instance.onDisable();

         for (Handler value : instance.getLogger().getHandlers()) {
            value.close();
         }
      } catch (Throwable item) {
         PasswordHashContainer.handleMessage("Exception disabling plugin " + instance.getDescription().getName(), item);
      }

      target.unregisterListeners(instance);
      target.unregisterCommands(instance);
      ProxyServer.getInstance().getScheduler().cancel(instance);
      instance.getExecutorService().shutdownNow();

      for (Thread holder : Thread.getAllStackTraces().keySet()) {
         if (holder.getClass().getClassLoader() == input) {
            try {
               holder.interrupt();
               holder.join(2000L);
               if (holder.isAlive()) {
                  holder.interrupt();
               }
            } catch (Throwable entry) {
               PasswordHashContainer.handleMessage("Unable to stop thread that belong to plugin " + instance.getDescription().getName(), entry);
            }
         }
      }

      try {
         Field content = PluginManager.class.getDeclaredField("commandMap");
         content.setAccessible(true);
         Map reference = (Map)content.get(target);
         Iterator option = reference.entrySet().iterator();

         while (option.hasNext()) {
            Entry property = (Entry)option.next();
            if (((Command)property.getValue()).getClass().getClassLoader() == input) {
               option.remove();
            }
         }
      } catch (Throwable record) {
         PasswordHashContainer.handleMessage("Unable to cleanup commandMap " + instance.getDescription().getName(), record);
      }

      try {
         Field payload = PluginManager.class.getDeclaredField("plugins");
         payload.setAccessible(true);
         Map subject = (Map)payload.get(target);
         subject.values().remove(instance);
         Field setting = PluginManager.class.getDeclaredField("commandsByPlugin");
         setting.setAccessible(true);
         Multimap attribute = (Multimap)setting.get(target);
         attribute.removeAll(instance);
         Field result = PluginManager.class.getDeclaredField("listenersByPlugin");
         result.setAccessible(true);
         Multimap request = (Multimap)result.get(target);
         request.removeAll(instance);
      } catch (Throwable source) {
         PasswordHashContainer.handleMessage("Unable to cleanup bungee internal maps from plugin refs " + instance.getDescription().getName(), source);
      }

      if (input instanceof URLClassLoader) {
         try {
            ((URLClassLoader)input).close();
         } catch (Throwable response) {
            PasswordHashContainer.handleMessage("Unable to close the classloader for plugin " + instance.getDescription().getName(), response);
         }
      }
   }
}
