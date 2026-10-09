package com.nickuc.login.listener.proxy;

import com.nickuc.login.listener.RootServerAdapter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.event.EventHandler;

public class BungeeListener implements RootServerAdapter {
   private final String name;
   private final Set<String> players;

   @EventHandler(priority = -32)
   public void updateProxyDefineCommandsEvent(Object target) {
      try {
         Class<?> eventType = Class.forName("io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent");
         if (!eventType.isInstance(target)) {
            return;
         }
         Method getReceiver = eventType.getMethod("getReceiver");
         Object receiver = getReceiver.invoke(target);
         if (receiver instanceof ProxiedPlayer) {
            Method getCommands = eventType.getMethod("getCommands");
            @SuppressWarnings("unchecked")
            Map<String, ?> commands = (Map<String, ?>) getCommands.invoke(target);
            Collection<?> values = commands.values();
            values.removeIf(entry -> {
               try {
                  String input = (String) entry.getClass().getMethod("getName").invoke(entry);
                  input = input.toLowerCase(Locale.ENGLISH);
                  return input.startsWith(this.name + ':') || this.players.contains(input);
               } catch (ReflectiveOperationException ex) {
                  return false;
               }
            });
         }
      } catch (ClassNotFoundException ignored) {
      } catch (ReflectiveOperationException ex) {
         throw new IllegalStateException("Unable to filter Waterfall commands", ex);
      }
   }

   public BungeeListener(String target, Set<String> input) {
      this.name = target;
      this.players = input;
   }
}
