package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.MainLoginHandler;
import com.nickuc.login.auth.login.PrimaryLoginGate;
import com.nickuc.login.discord.DiscordNotifier;
import com.nickuc.login.discord.ParentDiscordNotifier;
import com.nickuc.login.listener.proxy.VelocityListener;
import com.nickuc.login.loader.LoaderBootstrap;
import com.nickuc.login.loader.platform.VelocityLoader;
import com.nickuc.login.platform.session.CachedSessionHandler;
import com.nickuc.login.platform.command.CommandHandler;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.sender.SenderAdapter;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

public abstract class DiscordForwarder implements LoaderBootstrap, CommandHandler, IndirectSessionHandler<VelocityLoader> {
   private final SenderAdapter senderAdapter;
   private final VelocityGateway velocityGateway;
   private final VelocityLoader velocityLoader;
   public final ParentDiscordNotifier parentDiscordNotifier;
   private boolean enabled;

   @Override
   public File resolveFile() {
      return this.velocityLoader.getDataDirectory();
   }

   public void enable() {
      this.parentDiscordNotifier.handleTask();
   }

   public VelocityLoader fetchVelocityLoader() {
      return this.velocityLoader;
   }

   @Override
   public boolean resolveState() {
      return this.enabled;
   }

   @Override
   public String retrieveMessage() {
      return this.parentDiscordNotifier.activeName;
   }

   public ProxyServer findProxyServer() {
      return this.velocityLoader.getServer();
   }

   public DiscordForwarder(VelocityLoader target, String input, Object output) {
      this.velocityLoader = target;
      this.velocityGateway = new VelocityGateway(this);
      this.parentDiscordNotifier = new ParentDiscordNotifier(input, target.getVersion(), output, this);
      this.senderAdapter = new MainLoginHandler(target.getLogger());
   }

   public void load() {
      this.enabled = true;
      this.parentDiscordNotifier.processTask();
   }

   @Override
   public void executeTask() {
      this.enabled = false;
   }

   @Override
   public CachedSessionHandler resolveCachedSessionHandler() {
      return this.velocityGateway;
   }

   @Override
   public String getMessage() {
      return this.velocityLoader.getVersion();
   }

   public abstract TightSenderAdapter[] findValues();

   public void performTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().performTask();
      }
   }

   public void processTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().processTask();
      }
   }

   public void updateTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().updateTask();
      }
   }

   @Override
   public Object buildObject(int target) {
      switch (target) {
         case 0:
            Collection input = this.findProxyServer().getAllServers();
            if (input != null && !input.isEmpty()) {
               StringBuilder output = new StringBuilder();

               for (RegisteredServer data : input) {
                  InetSocketAddress value = data.getServerInfo().getAddress();
                  String result;
                  byte request;
                  if (!value.isUnresolved()) {
                     InetAddress response = value.getAddress();
                     request = (byte)(!response.isLoopbackAddress()
                           && !response.isAnyLocalAddress()
                           && !PrimaryLoginGate.activePrimaryLoginGate.validateState(response)
                           && !PrimaryLoginGate.primaryLoginGate.validateState(response)
                           && !PrimaryLoginGate.pendingPrimaryLoginGate.validateState(response)
                        ? 0
                        : 1);
                     result = response.getHostAddress();
                  } else {
                     request = 0;
                     result = value.getHostName();
                     if (result == null) {
                        request = 1;
                     } else {
                        try {
                           result = InetAddress.getByName(result).getHostAddress();
                        } catch (UnknownHostException source) {
                           request = 1;
                        }
                     }
                  }

                  if (output.length() > 0) {
                     output.append(" ");
                  }

                  if (request != 0) {
                     output.append("@remote");
                  } else {
                     output.append(result);
                  }

                  output.append(":");
                  output.append(value.getPort());
               }

               return output.toString();
            }
         default:
            return null;
         case 1:
            return this.findProxyServer().getBoundAddress().getPort();
      }
   }

   @Override
   public ParentDiscordNotifier getParentDiscordNotifier() {
      return this.parentDiscordNotifier;
   }

   public void dispatchTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().dispatchTask();
      }
   }

   public VelocityListener retrieveVelocityListener() {
      return (VelocityListener)this.parentDiscordNotifier.retrieveTightConnectionContract();
   }

   @Override
   public SenderAdapter fetchSenderAdapter() {
      return this.senderAdapter;
   }

   public PrimaryVelocityForwarder resolvePrimaryVelocityForwarder(boolean target) {
      return (PrimaryVelocityForwarder)this.parentDiscordNotifier.processLinkedSessionHandler(target);
   }

   public void disable() {
      this.parentDiscordNotifier.sendTask();
      this.enabled = false;
   }

   @Override
   public String toString() {
      return this.parentDiscordNotifier.toString();
   }
}
