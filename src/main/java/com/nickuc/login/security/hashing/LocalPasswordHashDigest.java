package com.nickuc.login.security.hashing;

import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.sender.StrictSenderAdapter;
import com.nickuc.login.proxy.DiscordForwarder;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.PlayerAvailableCommandsEvent;
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalPasswordHashDigest implements StrictSenderAdapter {
   private final Set<String> players;
   private final String name;
   private final Set<String> activePlayers;

   private boolean isState(String target) {
      if (target.isEmpty()) {
         return false;
      }

      if (target.charAt(0) != '/') {
         return false;
      }

      String[] input = target.split(" ");
      return input.length > 0 && this.activePlayers.contains(input[0].toLowerCase(Locale.ENGLISH));
   }

   @Subscribe(order = PostOrder.LAST)
   public void processCommandExecuteEvent(CommandExecuteEvent target) {
      if (this.isState(target.getCommand())) {
         target.setResult(CommandResult.allowed());
      }
   }

   public LocalPasswordHashDigest(DiscordForwarder target) {
      this.name = target.retrieveMessage().toLowerCase(Locale.ENGLISH);
      HashSet input = new HashSet();
      input.add(this.name + 'c');
      input.add(this.name + ':' + this.name + 'c');

      for (SilentProxyState value : SilentProxyState.values()) {
         String result = value.resolveMessage().toLowerCase(Locale.ENGLISH);
         input.add(this.name + result + 'c');
         input.add(this.name + ':' + this.name + result + 'c');
      }

      this.players = MainPasswordHashVerifier.processMainPasswordHashVerifier(input);
      this.activePlayers = MainPasswordHashVerifier.processMainPasswordHashVerifier(input.stream().map(instance -> '/' + instance).collect(Collectors.toSet()));
   }

   @Subscribe(order = PostOrder.EARLY)
   public void saveTabCompleteEvent(TabCompleteEvent target) {
      List input = target.getSuggestions();
      if (!input.isEmpty()) {
         String output = target.getPartialMessage().trim();
         if (output.isEmpty() || output.charAt(0) == '/') {
            input.removeIf(targetValue -> targetValue.startsWith('/' + this.name + ':') || this.isState(targetValue));
         }
      }
   }

   @Subscribe(order = PostOrder.EARLY)
   public void savePlayerAvailableCommandsEvent(PlayerAvailableCommandsEvent target) {
      target.getRootNode().getChildren().removeIf(targetValue -> {
         String input = targetValue.getName().toLowerCase(Locale.ENGLISH);
         return input.startsWith(this.name + ':') || this.players.contains(input);
      });
   }
}
