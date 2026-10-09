package com.nickuc.login.listener.proxy;

import com.nickuc.login.config.PasswordHashDefinition;
import com.nickuc.login.listener.RootServerAdapter;
import com.nickuc.login.security.hashing.PasswordHashDigest;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class BungeeGuard implements RootServerAdapter {
   private final PasswordHashDefinition passwordHashDefinition;
   private final ProxyServer proxyServer;

   public BungeeGuard(PasswordHashDefinition target, ProxyServer input) {
      this.passwordHashDefinition = target;
      this.proxyServer = input;
   }

   @EventHandler(priority = 127)
   public void performPlayerDisconnectEvent(PlayerDisconnectEvent target) {
      PasswordHashDigest.sessions.remove(target.getPlayer());
   }

   @EventHandler(priority = -128)
   public void performPostLoginEvent(PostLoginEvent target) {
      ProxiedPlayer input = target.getPlayer();
      PasswordHashDigest.sessions.put(input, PasswordHashDigest.processPasswordHashDigest(this.passwordHashDefinition, this.proxyServer, input));
   }
}
