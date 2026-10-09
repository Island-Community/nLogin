package com.nickuc.login.security.hashing;

import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashDefinition;
import com.nickuc.login.model.RemotePremiumState;
import com.nickuc.login.listener.proxy.StrictBungeeListener;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.ChainedSessionHandler;
import com.nickuc.login.proxy.BungeeForwarder;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public class PasswordHashDigest implements VerifiedServerAdapter {
   private final PasswordHashDefinition passwordHashDefinition;
   private final ProxiedPlayer proxiedPlayer;
   private final boolean enabled;
   public static final Map<ProxiedPlayer, PasswordHashDigest> sessions = new ConcurrentHashMap<>();
   private final UUID uniqueId = UUID.randomUUID();
   private ChainedSessionHandler chainedSessionHandler;
   private final ProxyServer proxyServer;

   @Override
   public boolean equals(Object target) {
      if (target != null && this.getClass() == target.getClass()) {
         PasswordHashDigest input = (PasswordHashDigest)target;
         return Objects.equals(this.uniqueId, input.uniqueId) && Objects.equals(this.proxiedPlayer, input.proxiedPlayer);
      } else {
         return false;
      }
   }

   @Override
   public void performObject(Object target) {
      if (target instanceof String) {
         this.proxiedPlayer.sendMessage(TextComponent.fromLegacyText(LocalLocaleFlow.loadMessage((String)target, true)));
      } else {
         if (!(target instanceof TextComponent)) {
            throw new IllegalArgumentException("Unsupported message value! " + target + " " + target.getClass().getCanonicalName());
         }

         this.proxiedPlayer.sendMessage((TextComponent)target);
      }
   }

   @Override
   public void performIndirectSessionHandler(IndirectSessionHandler<?> target, RemotePremiumState input, Object output, byte[] context) {
      if (!(output instanceof String)) {
         throw new IllegalArgumentException("Channel must be a string!");
      }

      if (this.proxiedPlayer.isConnected()) {
         switch (input) {
            case REMOTE_PREMIUM_STATE:
               this.proxiedPlayer.sendData((String)output, context);
               break;
            case ACTIVE_REMOTEPREMIUMSTATE:
               Server data = this.proxiedPlayer.getServer();
               if (data != null) {
                  data.sendData((String)output, context);
               }
               break;
            default:
               throw new IllegalArgumentException("Unsupported direction " + input);
         }
      }
   }

   @Override
   public ChainedSessionHandler getChainedSessionHandler() {
      if (this.chainedSessionHandler == null) {
         this.chainedSessionHandler = new BungeeForwarder(this);
      }

      return this.chainedSessionHandler;
   }

   @Override
   public int getCount() {
      return this.proxiedPlayer.getPing();
   }

   public static PasswordHashDigest processPasswordHashDigest(PasswordHashDefinition instance, ProxyServer target, Object input) {
      if (input instanceof String) {
         String output = ((String)input).toLowerCase(Locale.ENGLISH);
         ProxiedPlayer context = target.getPlayer(output);
         return context == null ? null : computePasswordHashDigest(instance, target, context);
      } else if (input instanceof ProxiedPlayer) {
         return computePasswordHashDigest(instance, target, (ProxiedPlayer)input);
      } else {
         throw new IllegalArgumentException("Unsupported player type! " + input + " " + (input != null ? input.getClass().getCanonicalName() : ""));
      }
   }

   @Override
   public void handleMessage(String target) {
      this.proxiedPlayer.sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(LocalLocaleFlow.loadMessage(target, true)));
   }

   @Override
   public String getName() {
      return this.proxiedPlayer.getName();
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.uniqueId, this.proxiedPlayer);
   }

   @Override
   public void processMessage(String target) {
      this.proxiedPlayer.chat(target);
   }

   private static PasswordHashDigest computePasswordHashDigest(PasswordHashDefinition instance, ProxyServer target, ProxiedPlayer input) {
      PasswordHashDigest output = sessions.get(input);
      if (output != null) {
         return output;
      }

      if (PasswordHashContainer.findState()) {
         StackTraceElement[] context = new Exception().getStackTrace();
         String data = context.length > 0 ? context[Math.min(3, context.length - 1)].toString() : "unknown";
         PasswordHashContainer.performMessage("[nCore] Creating player instance for %s with no cache in \"%s\" context", input.getName(), data);
      }

      return processPasswordHashDigest(instance, target, input);
   }

   @Override
   public <T> T findObject() {
      return (T)this.proxiedPlayer;
   }

   private PasswordHashDigest(PasswordHashDefinition target, ProxiedPlayer input, ProxyServer output, boolean context) {
      this.passwordHashDefinition = target;
      this.proxiedPlayer = input;
      this.proxyServer = output;
      this.enabled = context;
   }

   @Override
   public boolean findState() {
      return this.enabled;
   }

   @Override
   public void saveMessage(String target, String input, int output, int context, int data) {
      this.proxiedPlayer
         .sendTitle(
            this.proxyServer
               .createTitle()
               .title(TextComponent.fromLegacyText(LocalLocaleFlow.loadMessage(target, true)))
               .subTitle(TextComponent.fromLegacyText(LocalLocaleFlow.loadMessage(input, true)))
               .fadeIn(output)
               .fadeOut(data)
               .stay(context)
         );
   }

   @Override
   public String toString() {
      return "BungeePlayerFactory{objectId=" + this.uniqueId + ", player=" + this.proxiedPlayer + '}';
   }

   @Override
   public InetSocketAddress fetchInetSocketAddress() {
      try {
         return (InetSocketAddress)this.proxiedPlayer.getSocketAddress();
      } catch (NoSuchMethodError input) {
         return this.proxiedPlayer.getAddress();
      }
   }

   @Override
   public LinkedSessionHandler getLinkedSessionHandler() {
      return this.passwordHashDefinition.handleBungeeGateway(true);
   }

   public static PasswordHashDigest processPasswordHashDigest(PasswordHashDefinition instance, ProxyServer target, ProxiedPlayer input) {
      Channel context = StrictBungeeListener.buildChannel("create instance", input.getPendingConnection());
      byte output;
      if (context != null) {
         switch (context.getClass().getSimpleName()) {
            case "EmbeddedChannel":
            case "FakeChannel":
            case "SpoofedChannel":
               output = 1;
               break;
            default:
               output = 0;
         }
      } else {
         output = 0;
      }

      return new PasswordHashDigest(instance, input, target, (boolean)output);
   }

   @Override
   public Optional<String> loadOptional() {
      return Optional.ofNullable(this.proxiedPlayer.getLocale()).map(Locale::toLanguageTag);
   }

   @Override
   public String findMessage() {
      return this.proxiedPlayer.getDisplayName();
   }

   @Override
   public void executeTask() {
      this.proxiedPlayer.sendTitle(this.proxyServer.createTitle().reset());
   }

   @Override
   public UUID getUniqueId() {
      return this.proxiedPlayer.getUniqueId();
   }

   @Override
   public void processMessageForValue(String target) {
      this.proxiedPlayer.setDisplayName(target);
   }

   @Override
   public void performMessage(String target) {
      if (target.length() >= 2 && target.charAt(0) == '/') {
         target = target.substring(1);
      }

      this.proxyServer.getPluginManager().dispatchCommand(this.proxiedPlayer, target);
   }

   @Override
   public boolean hasState(String target) {
      return this.proxiedPlayer.hasPermission(target);
   }

   @Override
   public CompletableFuture<Void> buildCompletableFuture(String target) {
      CompletableFuture input = new CompletableFuture();
      this.proxiedPlayer.disconnect(TextComponent.fromLegacyText(LocalLocaleFlow.loadMessage(target, true)));
      input.complete(null);
      return input;
   }

   @Override
   public boolean loadState() {
      return this.proxiedPlayer.isConnected();
   }
}
