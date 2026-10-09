package com.nickuc.login.command.completion;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.config.PasswordHashDefinition;
import com.nickuc.login.listener.RootServerAdapter;
import com.nickuc.login.listener.proxy.BungeeListener;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.security.hashing.MainPasswordHashVerifier;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.event.EventHandler;

public class SharedDeletePurgeCommand implements RootServerAdapter {
   private final Set<String> players;
   private final String name;
   private final Set<String> activePlayers;

   @EventHandler(priority = 127)
   public void handleChatEvent(ChatEvent target) {
      if (this.isState(target.getMessage())) {
         target.setCancelled(false);
      }
   }

   private boolean isState(String target) {
      if (target.isEmpty()) {
         return false;
      }

      if (target.charAt(0) != '/') {
         return false;
      }

      String[] input = target.split(" ");
      return input.length > 0 && this.players.contains(input[0].toLowerCase(Locale.ENGLISH));
   }

   public SharedDeletePurgeCommand(PasswordHashDefinition target) {
      this.name = target.retrieveMessage().toLowerCase(Locale.ENGLISH);
      HashSet input = new HashSet();
      input.add(this.name + 'c');
      input.add(this.name + ':' + this.name + 'c');

      for (SilentProxyState value : SilentProxyState.values()) {
         String result = value.resolveMessage().toLowerCase(Locale.ENGLISH);
         input.add(this.name + result + 'c');
         input.add(this.name + ':' + this.name + result + 'c');
      }

      this.activePlayers = MainPasswordHashVerifier.processMainPasswordHashVerifier(input);
      this.players = MainPasswordHashVerifier.processMainPasswordHashVerifier(input.stream().map(instance -> '/' + instance).collect(Collectors.toSet()));
   }

   @EventHandler(priority = -32)
   public void updateTabCompleteEvent(TabCompleteEvent target) {
      if (!target.isCancelled()) {
         List input = target.getSuggestions();
         if (!input.isEmpty()) {
            String output = target.getCursor().trim();
            if (output.isEmpty() || output.charAt(0) == '/') {
               input.removeIf(targetValue -> targetValue.startsWith('/' + this.name + ':') || this.isState(targetValue));
            }
         }
      }
   }

   @Override
   public void handleObject(Object target) {
      RootServerAdapter.super.handleObject(target);
      if (ChildLoginCheckpoint.validateState("io.github.waterfallmc.waterfall.event.ProxyDefineCommandsEvent")) {
         PasswordHashDefinition input = (PasswordHashDefinition)target;
         input.a(new BungeeListener(this.name, this.activePlayers), new OutgoingAccountHandler[0]);
      }
   }
}
