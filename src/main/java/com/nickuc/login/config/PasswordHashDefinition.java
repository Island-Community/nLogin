package com.nickuc.login.config;

import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.auth.login.PrimaryLoginGate;
import com.nickuc.login.auth.login.PrivateLoginCheckpoint;
import com.nickuc.login.auth.login.ReadyLoginHandler;
import com.nickuc.login.command.SpawnCompletionCommand;
import com.nickuc.login.discord.DiscordNotifier;
import com.nickuc.login.discord.ParentDiscordNotifier;
import com.nickuc.login.loader.LoaderBootstrap;
import com.nickuc.login.loader.platform.BungeeLoader;
import com.nickuc.login.platform.session.CachedSessionHandler;
import com.nickuc.login.platform.command.CommandHandler;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.sender.SenderAdapter;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.protocol.ProxyCodec;
import com.nickuc.login.proxy.BungeeGateway;
import com.nickuc.login.proxy.OpenBungeeForwarder;
import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public abstract class PasswordHashDefinition implements LoaderBootstrap, CommandHandler, IndirectSessionHandler<BungeeLoader> {
   private final OpenBungeeForwarder openBungeeForwarder;
   private static Method method = LoginCheckpoint.handleMethod(ProxyServer.class, "getServersCopy");
   private final BungeeLoader bungeeLoader;
   private boolean enabled = true;
   public final ParentDiscordNotifier parentDiscordNotifier;
   private final SenderAdapter senderAdapter;

   public PasswordHashDefinition(BungeeLoader target, String input, Object output) {
      this.bungeeLoader = target;
      this.openBungeeForwarder = new OpenBungeeForwarder(this);
      this.parentDiscordNotifier = new ParentDiscordNotifier(input, target.getVersion(), output, this);
      this.senderAdapter = new ReadyLoginHandler(target.getLogger());
   }

   @Override
   public boolean resolveState() {
      return this.enabled;
   }

   public void updateTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().updateTask();
      }
   }

   @Override
   public void executeTask() {
      this.enabled = false;
      SpawnCompletionCommand.updatePlugin(this.bungeeLoader);
   }

   @Override
   public File resolveFile() {
      return this.bungeeLoader.getDataFolder();
   }

   static {
      if (method == null) {
         method = LoginCheckpoint.handleMethod(ProxyServer.class, "getServers");
      }

      if (method == null) {
         throw new IllegalArgumentException("Unable to find the getServers or getServersCopy method in ProxyServer!");
      }
   }

   public void dispatchTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().dispatchTask();
      }
   }

   public ProxyCodec fetchProxyCodec() {
      return (ProxyCodec)this.parentDiscordNotifier.retrieveTightConnectionContract();
   }

   public void disable() {
      this.parentDiscordNotifier.sendTask();
      this.enabled = false;
   }

   @Nullable
   public Map<String, ServerInfo> fetchTable() {
      return LoginCheckpoint.buildObject(method, this.findProxyServer());
   }

   public void enable() {
      this.parentDiscordNotifier.handleTask();
   }

   @Override
   public ParentDiscordNotifier getParentDiscordNotifier() {
      return this.parentDiscordNotifier;
   }

   @Override
   public String getMessage() {
      return this.parentDiscordNotifier.name;
   }

   @Override
   public String toString() {
      return this.parentDiscordNotifier.toString();
   }

   public void processTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().processTask();
      }
   }

   public ProxyServer findProxyServer() {
      return this.bungeeLoader.getProxy();
   }

   public BungeeGateway handleBungeeGateway(boolean target) {
      return (BungeeGateway)this.parentDiscordNotifier.processLinkedSessionHandler(target);
   }

   public void performTask() {
      if (this.parentDiscordNotifier.fetchState()) {
         this.parentDiscordNotifier.<DiscordNotifier>loadDiscordNotifier().performTask();
      }
   }

   @Override
   public SenderAdapter fetchSenderAdapter() {
      return this.senderAdapter;
   }

   public abstract TightSenderAdapter[] findValues();

   @Override
   public Object buildObject(int target) {
      switch (target) {
         case 0:
            HashMap item = new HashMap();

            try {
               PasswordHashLoader element = new PasswordHashLoader(new File(this.resolveFile().getParentFile().getParentFile(), "config.yml"));

               for (String reference : element.loadSet("servers")) {
                  String value = element.b("servers." + reference + ".address");
                  if (value != null && !value.isEmpty()) {
                     String[] result = value.split("\\:");
                     String request = result[0];
                     int response = result.length > 1 ? PrivateLoginCheckpoint.handleInteger(result[1], 0) : 0;
                     item.put(reference, new InetSocketAddress(request, response));
                  }
               }
            } catch (Throwable record) {
               return "";
            }

            StringBuilder content = new StringBuilder();

            for (InetSocketAddress subject : item.values()) {
               String option;
               byte setting;
               if (!subject.isUnresolved()) {
                  InetAddress property = subject.getAddress();
                  setting = (byte)(!property.isLoopbackAddress()
                        && !property.isAnyLocalAddress()
                        && !PrimaryLoginGate.activePrimaryLoginGate.validateState(property)
                        && !PrimaryLoginGate.primaryLoginGate.validateState(property)
                        && !PrimaryLoginGate.pendingPrimaryLoginGate.validateState(property)
                     ? 0
                     : 1);
                  option = property.getHostAddress();
               } else {
                  setting = 0;
                  option = subject.getHostName();
                  if (option == null) {
                     setting = 1;
                  } else {
                     try {
                        option = InetAddress.getByName(option).getHostAddress();
                     } catch (UnknownHostException entry) {
                        setting = 1;
                     }
                  }
               }

               if (content.length() > 0) {
                  content.append(" ");
               }

               if (setting != 0) {
                  content.append("@remote");
               } else {
                  content.append(option);
               }

               content.append(":");
               content.append(subject.getPort());
            }

            return content.toString();
         case 1:
            try {
               PasswordHashLoader input = new PasswordHashLoader(new File(this.resolveFile().getParentFile().getParentFile(), "config.yml"));
               Collection output = (Collection)input.d("listeners");
               if (output != null && !output.isEmpty()) {
                  String context = (String)((Map)output.iterator().next()).get("host");
                  if (context != null && !context.isEmpty()) {
                     String[] data = context.split("\\:");
                     return data.length > 1 ? PrivateLoginCheckpoint.handleInteger(data[1], 0) : 0;
                  }

                  return 0;
               }

               return 0;
            } catch (Throwable source) {
               return 0;
            }
         default:
            return null;
      }
   }

   public void load() {
      this.enabled = true;
      this.parentDiscordNotifier.processTask();
   }

   @Override
   public CachedSessionHandler resolveCachedSessionHandler() {
      return this.openBungeeForwarder;
   }

   public BungeeLoader retrieveBungeeLoader() {
      return this.bungeeLoader;
   }

   @Override
   public String retrieveMessage() {
      return this.parentDiscordNotifier.activeName;
   }
}
