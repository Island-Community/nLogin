package com.nickuc.login.bungee;

import com.nickuc.login.api.enums.ServerConnectType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.event.bungee.connection.ServerPreConnectEvent;
import com.nickuc.login.config.PasswordHashDefinition;
import com.nickuc.login.model.VerifiedProxyCatalog;
import com.nickuc.login.loader.platform.BungeeLoader;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.listener.ListenerContract;
import com.nickuc.login.platform.player.PlayerContract;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.PasswordHashAdapter;
import com.nickuc.login.session.BungeeCoordinator;
import com.nickuc.login.spawn.VerifiedNoticeKind;
import com.nickuc.login.storage.password.PasswordStore;
import javax.annotation.Nullable;

import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.ServerConnectRequest.Builder;
import net.md_5.bungee.api.ServerConnectRequest.Result;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class BungeePlatform extends PasswordHashDefinition implements ListenerContract, SecondarySenderAdapter {
   private PasswordHashAdapter passwordHashAdapter;

   @Override
   public PasswordHashAdapter getPasswordHashAdapter() {
      return this.passwordHashAdapter;
   }

   @Override
   public Class<?> getPlayerClass() {
      return ProxiedPlayer.class;
   }

   @Override
   public VerifiedProxyCatalog createVerifiedProxyCatalog(
      VerifiedServerAdapter target, String input, ServerConnectType output, @Nullable PlayerContract<Boolean> context
   ) {
      if (!target.loadState()) {
         return VerifiedProxyCatalog.PENDING_VERIFIEDPROXYCATALOG;
      }

      ServerInfo data = this.findProxyServer().getServerInfo(input);
      if (data == null) {
         return VerifiedProxyCatalog.PENDING_VERIFIEDPROXYCATALOG;
      }

      ProxiedPlayer value = target.findObject();
      ServerPreConnectEvent result = this.fetchPasswordStore().loadObject(EventEnum.SERVER_PRE_CONNECT, target, output, data);
      if (!this.fetchPasswordStore().callEvent(result)) {
         return VerifiedProxyCatalog.ACTIVE_VERIFIEDPROXYCATALOG;
      }

      data = result.getServer();
      Server request = value.getServer();
      if (request != null && data.equals(request.getInfo())) {
         if (context != null) {
            context.done(true);
         }

         return VerifiedProxyCatalog.VERIFIED_PROXY_CATALOG;
      } else {
         Builder response = ServerConnectRequest.builder().target(data).reason(Reason.PLUGIN);
         if (context == null) {
            response.callback(
               (outputValue, contextValue) -> {
                  if (outputValue != Result.SUCCESS && outputValue != Result.ALREADY_CONNECTED) {
                     String dataValue = value.getGroups().contains("admin")
                        ? contextValue.getClass().getSimpleName() + " : " + contextValue.getMessage()
                        : contextValue.getClass().getName();
                     target.buildCompletableFuture(this.findProxyServer().getTranslation("fallback_kick", new Object[]{dataValue}));
                  }
               }
            );
         } else {
            response.callback((targetValue, inputValue) -> context.done(targetValue == Result.SUCCESS || targetValue == Result.ALREADY_CONNECTED));
         }

         value.connect(response.build());
         return VerifiedProxyCatalog.VERIFIED_PROXY_CATALOG;
      }
   }

   @Override
   public LinkedSessionHandler loadLinkedSessionHandler() {
      return this.handleBungeeGateway(true);
   }

   @Override
   public boolean checkState(String target) {
      return this.findProxyServer().getServerInfo(target) != null;
   }

   @Nullable
   @Override
   public String handleMessage(VerifiedServerAdapter target) {
      ProxiedPlayer input = target.findObject();
      Server output = input.getServer();
      return output == null ? null : output.getInfo().getName();
   }

   @Override
   public TightSenderAdapter[] findValues() {
      return VerifiedNoticeKind.values();
   }

   @Override
   public void performTask() {
      this.passwordHashAdapter = new PasswordHashAdapter(this.fetchPasswordStore(), "nlogin:main");
      super.performTask();
   }

   @Override
   public boolean callEvent(Object target) {
      this.findProxyServer().getPluginManager().callEvent((Event)target);
      return !(target instanceof Cancellable) || !((Cancellable)target).isCancelled();
   }

   public BungeePlatform(BungeeLoader target) {
      super(target, "nLogin", null);
      this.a(new PasswordStore(new BungeeCoordinator(this), this, false));
   }

   @Override
   public PasswordStore fetchPasswordStore() {
      return (PasswordStore)super.b();
   }

   @Override
   public boolean verifyState(VerifiedServerAdapter target) {
      if (!target.loadState()) {
         return false;
      }

      ProxiedPlayer input = target.findObject();
      return input.getServer() != null;
   }
}
