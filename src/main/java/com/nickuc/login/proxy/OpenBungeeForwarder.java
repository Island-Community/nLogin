package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.IncomingLoginGate;
import com.nickuc.login.command.BungeeProcessor;
import com.nickuc.login.command.completion.SharedDeletePurgeCommand;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashDefinition;
import com.nickuc.login.listener.proxy.BungeeGuard;
import com.nickuc.login.platform.session.CachedSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.connection.TightConnectionContract;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.protocol.ProxyCodec;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.PluginDescription;

public final class OpenBungeeForwarder implements CachedSessionHandler {
   private final PasswordHashDefinition passwordHashDefinition;

   @Override
   public ParentAccountHandler resolveParentAccountHandler() {
      ProxyServer target = this.passwordHashDefinition.findProxyServer();
      return new DeadBungeeForwarder(this.passwordHashDefinition, target, BungeeProcessor.loadBungeeProcessor(target, target.getConsole()));
   }

   @Override
   public OutgoingAccountHandler getOutgoingAccountHandler() {
      return new SharedDeletePurgeCommand(this.passwordHashDefinition);
   }

   @Override
   public TightSenderAdapter[] findValues() {
      return this.passwordHashDefinition.findValues();
   }

   public OpenBungeeForwarder(PasswordHashDefinition target) {
      this.passwordHashDefinition = target;
   }

   @Override
   public LinkedSessionHandler computeLinkedSessionHandler(boolean target) {
      return new BungeeGateway(this.passwordHashDefinition.retrieveBungeeLoader());
   }

   @Override
   public boolean verifyState(String target) {
      byte input = 0;
      PluginDescription output = this.passwordHashDefinition.retrieveBungeeLoader().getDescription();
      if (!output.getName().equals(target)) {
         PasswordHashContainer.updateMessage("Unable to start the plugin: the name has been changed to '" + output.getName() + "' :c");
         input = 1;
      }

      if (!output.getAuthor().equals("NickUC")) {
         PasswordHashContainer.updateMessage("Unable to start the plugin: the list of authors has been modified :c");
         input = 1;
      }

      return (boolean)input;
   }

   @Override
   public void sendTask() {
      try {
         Map target = this.passwordHashDefinition.fetchTable();
         if (target != null && !target.isEmpty()) {
            List input = target.entrySet()
               .stream()
               .map(instance -> (String)instance.getKey() + ": " + ((ServerInfo)instance.getValue()).getSocketAddress())
               .collect(Collectors.toList());
            this.passwordHashDefinition
               .parentDiscordNotifier
               .retrieveUpdateLookup()
               .retrieveLowLoginResolver()
               .resolveLowLoginResolver("serverList", String.join(", ", input));
         }
      } catch (NoSuchMethodError output) {
      }
   }

   @Override
   public void updateTask() {
      this.passwordHashDefinition.updateTask();
   }

   @Override
   public TightConnectionContract retrieveTightConnectionContract() {
      return new ProxyCodec(this.passwordHashDefinition);
   }

   @Override
   public OutgoingAccountHandler loadOutgoingAccountHandler() {
      return new BungeeGuard(this.passwordHashDefinition, this.passwordHashDefinition.findProxyServer());
   }

   @Override
   public void dispatchTask() {
      this.passwordHashDefinition.dispatchTask();
   }

   @Override
   public void performTask() {
      this.passwordHashDefinition.performTask();
   }

   @Override
   public void handleTask() {
      PasswordHashContainer.handleIndirectSessionHandler(this.passwordHashDefinition, false, true);
   }

   @Override
   public IncomingLoginGate loadIncomingLoginGate() {
      ProxyServer target = this.passwordHashDefinition.findProxyServer();
      return new IncomingLoginGate(target.getName(), target.getVersion(), target.getVersion(), SilentProxyState.ACTIVE_SILENTPROXYSTATE, false);
   }

   @Override
   public void processTask() {
      this.passwordHashDefinition.processTask();
   }
}
