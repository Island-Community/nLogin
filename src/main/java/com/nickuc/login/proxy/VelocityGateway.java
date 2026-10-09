package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.IncomingLoginGate;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.listener.proxy.VelocityListener;
import com.nickuc.login.listener.proxy.VerifiedVelocityListener;
import com.nickuc.login.platform.session.CachedSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.connection.TightConnectionContract;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.security.hashing.LocalPasswordHashDigest;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.velocitypowered.api.util.ProxyVersion;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class VelocityGateway implements CachedSessionHandler {
   private final DiscordForwarder discordForwarder;

   @Override
   public void handleTask() {
      PasswordHashContainer.handleIndirectSessionHandler(this.discordForwarder, false, false);
   }

   @Override
   public ParentAccountHandler resolveParentAccountHandler() {
      ProxyServer target = this.discordForwarder.findProxyServer();
      return new VelocityBridge(this.discordForwarder, target, LocalVelocityGateway.createLocalVelocityGateway(target, target.getConsoleCommandSource()));
   }

   @Override
   public void dispatchTask() {
      this.discordForwarder.dispatchTask();
   }

   @Override
   public IncomingLoginGate loadIncomingLoginGate() {
      ProxyVersion target = this.discordForwarder.findProxyServer().getVersion();
      return new IncomingLoginGate(
         target.getName(), target.getVersion(), target.getVersion() + " (" + target.getVendor() + ")", SilentProxyState.PENDING_SILENTPROXYSTATE, false
      );
   }

   @Override
   public void performTask() {
      this.discordForwarder.performTask();
   }

   @Override
   public TightSenderAdapter[] findValues() {
      return this.discordForwarder.findValues();
   }

   @Override
   public TightConnectionContract retrieveTightConnectionContract() {
      return new VelocityListener(this.discordForwarder);
   }

   @Override
   public OutgoingAccountHandler loadOutgoingAccountHandler() {
      return new VerifiedVelocityListener(this.discordForwarder, this.discordForwarder.findProxyServer());
   }

   @Override
   public boolean verifyState(String target) {
      return false;
   }

   public VelocityGateway(DiscordForwarder target) {
      this.discordForwarder = target;
   }

   @Override
   public void sendTask() {
      try {
         Collection target = this.discordForwarder.findProxyServer().getAllServers();
         if (target != null && !target.isEmpty()) {
            List input = target.stream().map(instance -> {
               ServerInfo targetValue = instance.getServerInfo();
               return targetValue.getName() + ": " + targetValue.getAddress();
            }).collect(Collectors.toList());
            this.discordForwarder
               .parentDiscordNotifier
               .retrieveUpdateLookup()
               .retrieveLowLoginResolver()
               .resolveLowLoginResolver("serverList", String.join(", ", input));
         }
      } catch (NoSuchMethodError output) {
      }
   }

   @Override
   public void processTask() {
      this.discordForwarder.processTask();
   }

   @Override
   public LinkedSessionHandler computeLinkedSessionHandler(boolean target) {
      return new PrimaryVelocityForwarder(this.discordForwarder.fetchVelocityLoader());
   }

   @Override
   public OutgoingAccountHandler getOutgoingAccountHandler() {
      return new LocalPasswordHashDigest(this.discordForwarder);
   }

   @Override
   public void updateTask() {
      this.discordForwarder.updateTask();
   }
}
