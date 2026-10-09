package com.nickuc.login.listener.proxy;

import com.nickuc.login.platform.sender.StrictSenderAdapter;
import com.nickuc.login.protocol.PasswordHashCodec;
import com.nickuc.login.proxy.DiscordForwarder;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;


public class VerifiedVelocityListener implements StrictSenderAdapter {
   private final ProxyServer proxyServer;
   private final DiscordForwarder discordForwarder;

   public VerifiedVelocityListener(DiscordForwarder target, ProxyServer input) {
      this.discordForwarder = target;
      this.proxyServer = input;
   }

   @Subscribe(order = PostOrder.FIRST)
   public void saveLoginEvent(LoginEvent target) {
      Player input = target.getPlayer();
      PasswordHashCodec.sessions.put(input, PasswordHashCodec.loadPasswordHashCodec(this.discordForwarder, this.proxyServer, input));
   }

   @Subscribe(order = PostOrder.LAST)
   public void executeDisconnectEvent(DisconnectEvent target) {
      PasswordHashCodec.sessions.remove(target.getPlayer());
   }
}
