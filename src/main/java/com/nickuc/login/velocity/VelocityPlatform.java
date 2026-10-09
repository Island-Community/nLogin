package com.nickuc.login.velocity;

import com.nickuc.login.api.enums.ServerConnectType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.event.internal.velocity.VelocityCancellableEvent;
import com.nickuc.login.api.event.velocity.connection.ServerPreConnectEvent;
import com.nickuc.login.model.VerifiedProxyCatalog;
import com.nickuc.login.loader.platform.VelocityLoader;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.listener.ListenerContract;
import com.nickuc.login.platform.player.PlayerContract;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.PasswordHashAdapter;
import com.nickuc.login.proxy.DiscordForwarder;
import com.nickuc.login.session.VelocityCoordinator;
import com.nickuc.login.spawn.VerifiedNoticeKind;
import com.nickuc.login.storage.password.PasswordStore;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder.Status;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.Nullable;

import net.kyori.adventure.text.Component;

public class VelocityPlatform extends DiscordForwarder implements ListenerContract, SecondarySenderAdapter {
   private final ChannelIdentifier channelIdentifier;
   private final ChannelIdentifier activeChannelIdentifier = new LegacyChannelIdentifier("nlogin:main");
   private PasswordHashAdapter passwordHashAdapter;

   @Override
   public void dispatchTask() {
      this.getParentDiscordNotifier().loadMemClassLoader().configureFilter(Collections.singleton("net.kyori"));
      super.dispatchTask();
   }

   @Override
   public void performTask() {
      this.passwordHashAdapter = new PasswordHashAdapter(this.fetchPasswordStore(), this.loadChannelIdentifier());
      super.performTask();
   }

   @Override
   public boolean callEvent(Object target) {
      this.findProxyServer().getEventManager().fireAndForget(target);
      return !(target instanceof VelocityCancellableEvent) || !((VelocityCancellableEvent)target).isCancelled();
   }

   public VelocityPlatform(VelocityLoader target) {
      super(target, "nLogin", null);
      this.channelIdentifier = MinecraftChannelIdentifier.create("nlogin", "main");
      this.a(new PasswordStore(new VelocityCoordinator(this), this, false));
   }

   @Override
   public boolean verifyState(VerifiedServerAdapter target) {
      if (!target.loadState()) {
         return false;
      }

      Player input = target.findObject();
      Optional output = input.getCurrentServer();
      return output.isPresent() && output.<Boolean>map(instance -> !instance.getServer().getPlayersConnected().isEmpty()).get();
   }

   @Override
   public Class<?> getPlayerClass() {
      return Player.class;
   }

   @Override
   public TightSenderAdapter[] findValues() {
      return VerifiedNoticeKind.values();
   }

   @Override
   public VerifiedProxyCatalog createVerifiedProxyCatalog(
      VerifiedServerAdapter target, String input, ServerConnectType output, @Nullable PlayerContract<Boolean> context
   ) {
      return !target.loadState()
         ? VerifiedProxyCatalog.PENDING_VERIFIEDPROXYCATALOG
         : this.findProxyServer()
            .getServer(input)
            .map(
               data -> {
                  Player value = target.findObject();
                  ServerPreConnectEvent result = this.fetchPasswordStore().loadObject(EventEnum.SERVER_PRE_CONNECT, target, output, data);
                  if (!this.fetchPasswordStore().callEvent(result)) {
                     return VerifiedProxyCatalog.ACTIVE_VERIFIEDPROXYCATALOG;
                  }

                  data = result.getServer();
                  Optional request = value.getCurrentServer();
                  if (request.isPresent() && data.getServerInfo().equals(((ServerConnection)request.get()).getServerInfo())) {
                     if (context != null) {
                        context.done(true);
                     }

                     return VerifiedProxyCatalog.VERIFIED_PROXY_CATALOG;
                  } else {
                     if (context == null) {
                        value.createConnectionRequest(data).connect().whenComplete((inputValue, outputValue) -> {
                           if (!inputValue.isSuccessful() && inputValue.getStatus() != Status.ALREADY_CONNECTED) {
                              value.disconnect((Component)inputValue.getReasonComponent().orElse(Component.text("Disconnected: Error while connecting to " + input)));
                           }
                        });
                     } else {
                        value.createConnectionRequest(data)
                           .connectWithIndication()
                           .whenComplete((targetValue, inputValue) -> context.done(targetValue != null && inputValue == null && targetValue));
                     }

                     return VerifiedProxyCatalog.VERIFIED_PROXY_CATALOG;
                  }
               }
            )
            .orElse(VerifiedProxyCatalog.PENDING_VERIFIEDPROXYCATALOG);
   }

   @Override
   public LinkedSessionHandler loadLinkedSessionHandler() {
      return this.resolvePrimaryVelocityForwarder(true);
   }

   public ChannelIdentifier loadChannelIdentifier() {
      return this.channelIdentifier;
   }

   @Override
   public PasswordHashAdapter getPasswordHashAdapter() {
      return this.passwordHashAdapter;
   }

   @Override
   public boolean checkState(String target) {
      return this.findProxyServer().getServer(target).isPresent();
   }

   public ChannelIdentifier findChannelIdentifier() {
      return this.activeChannelIdentifier;
   }

   @Nullable
   @Override
   public String handleMessage(VerifiedServerAdapter target) {
      Player input = target.findObject();
      return input.getCurrentServer().map(instance -> instance.getServerInfo().getName()).orElse(null);
   }

   @Override
   public PasswordStore fetchPasswordStore() {
      return (PasswordStore)super.b();
   }
}
