package com.nickuc.login.command.completion;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.listener.bukkit.CachedLocaleFilter;
import com.nickuc.login.listener.bukkit.DiscordGuard;
import com.nickuc.login.listener.bukkit.LocaleFilter;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.security.hashing.MainPasswordHashVerifier;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class VerifiedUnregisterBackupCommand implements PacketAdapter {
   private final Set<String> players;
   private final String name;
   private final Set<String> activePlayers;

   @EventHandler(priority = EventPriority.MONITOR)
   public void dispatchPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent target) {
      if (this.isState(target.getMessage())) {
         target.setCancelled(false);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void processServerCommandEvent(ServerCommandEvent target) {
      if (target instanceof Cancellable && this.isState(target.getCommand())) {
         target.setCancelled(false);
      }
   }

   @Override
   public void handleObject(Object target) {
      PacketAdapter.super.handleObject(target);
      DiscordGuard input = (DiscordGuard)target;
      if (ChildLoginCheckpoint.validateState("org.bukkit.event.player.PlayerCommandSendEvent")) {
         input.a(new LocaleFilter(this.name, this.activePlayers), new OutgoingAccountHandler[0]);
      }

      if (ChildLoginCheckpoint.validateState("org.bukkit.event.server.TabCompleteEvent")) {
         input.a(new CachedLocaleFilter(this.name, this.players), new OutgoingAccountHandler[0]);
      }
   }

   private boolean isState(String target) {
      if (target.isEmpty()) {
         return false;
      }

      if (target.charAt(0) != '/') {
         target = '/' + target;
      }

      String[] input = target.split(" ");
      return input.length > 0 && this.players.contains(input[0].toLowerCase(Locale.ENGLISH));
   }

   public VerifiedUnregisterBackupCommand(DiscordGuard target) {
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
}
