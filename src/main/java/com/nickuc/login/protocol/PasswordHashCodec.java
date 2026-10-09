package com.nickuc.login.protocol;

import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.RemotePremiumState;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.proxy.DiscordForwarder;
import com.nickuc.login.proxy.IncomingVelocityGateway;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class PasswordHashCodec implements VerifiedServerAdapter {
   private final UUID uniqueId = UUID.randomUUID();
   public static final Map<Player, PasswordHashCodec> sessions = new ConcurrentHashMap<>();
   private final boolean enabled;
   private final ProxyServer proxyServer;
   private final DiscordForwarder discordForwarder;
   private ChainedSessionHandler chainedSessionHandler;
   private final Player player;

   @Override
   public void handleMessage(String target) {
      this.player.sendActionBar(SafeLoginService.resolveTextComponent(target));
   }

   @Override
   public Optional<String> loadOptional() {
      return Optional.of(this.player.getPlayerSettings().getLocale().toLanguageTag());
   }

   @Override
   public void processMessage(String target) {
      throw new UnsupportedOperationException("setDisplayName");
   }

   public static PasswordHashCodec processPasswordHashCodec(DiscordForwarder instance, ProxyServer target, Object input) {
      if (input instanceof String) {
         String output = ((String)input).toLowerCase(Locale.ENGLISH);
         return target.getPlayer(output).map(inputValue -> handlePasswordHashCodec(instance, target, inputValue)).orElse(null);
      } else if (input instanceof Player) {
         return handlePasswordHashCodec(instance, target, (Player)input);
      } else {
         throw new IllegalArgumentException("Unsupported player type! " + input + " " + (input != null ? input.getClass().getCanonicalName() : ""));
      }
   }

   @Override
   public LinkedSessionHandler getLinkedSessionHandler() {
      return this.discordForwarder.resolvePrimaryVelocityForwarder(true);
   }

   @Override
   public UUID getUniqueId() {
      return this.player.getUniqueId();
   }

   @Nullable
   @Override
   public InetSocketAddress fetchInetSocketAddress() {
      return this.player.getRemoteAddress();
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.uniqueId, this.player);
   }

   @Override
   public void performMessage(String target) {
      if (target.length() >= 2 && target.charAt(0) == '/') {
         target = target.substring(1);
      }

      this.proxyServer.getCommandManager().executeImmediatelyAsync(this.player, target);
   }

   @Override
   public String getName() {
      return this.player.getUsername();
   }

   @Override
   public ChainedSessionHandler getChainedSessionHandler() {
      if (this.chainedSessionHandler == null) {
         this.chainedSessionHandler = (instance, target) -> {
            throw new UnsupportedOperationException("sendPacket");
         };
      }

      return this.chainedSessionHandler;
   }

   @Override
   public CompletableFuture<Void> buildCompletableFuture(String target) {
      CompletableFuture input = new CompletableFuture();
      this.player.disconnect(SafeLoginService.resolveTextComponent(target));
      input.complete(null);
      return input;
   }

   @Override
   public String findMessage() {
      return this.player.getUsername();
   }

   @Override
   public void performIndirectSessionHandler(IndirectSessionHandler<?> target, RemotePremiumState input, Object output, byte[] context) {
      Object data;
      if (output instanceof String) {
         String value = (String)output;
         String[] result = value.split(":");
         data = result.length == 2 ? MinecraftChannelIdentifier.create(result[0], result[1]) : new LegacyChannelIdentifier(value);
      } else {
         if (!(output instanceof ChannelIdentifier)) {
            throw new IllegalArgumentException("Invalid argument for channel! " + output + " " + output.getClass().getCanonicalName());
         }

         data = (ChannelIdentifier)output;
      }

      switch (input) {
         case REMOTE_PREMIUM_STATE:
            this.player.sendPluginMessage((ChannelIdentifier)data, context);
            break;
         case ACTIVE_REMOTEPREMIUMSTATE:
            this.player.getCurrentServer().ifPresent(inputValue -> {
               if (!inputValue.getServer().getPlayersConnected().isEmpty()) {
                  inputValue.sendPluginMessage(data, context);
               }
            });
            break;
         default:
            throw new IllegalArgumentException("Unsupported direction " + input);
      }
   }

   @Override
   public <T> T findObject() {
      return (T)this.player;
   }

   @Override
   public boolean loadState() {
      return this.player.isActive();
   }

   @Override
   public void performObject(Object target) {
      if (target instanceof String) {
         this.player.sendMessage(SafeLoginService.computeTextComponent((String)target, true));
      } else {
         if (!(target instanceof Component)) {
            throw new IllegalArgumentException("Unsupported message value! " + target + " " + target.getClass().getCanonicalName());
         }

         this.player.sendMessage((Component)target);
      }
   }

   public static PasswordHashCodec loadPasswordHashCodec(DiscordForwarder instance, ProxyServer target, Player input) {
      Channel context = IncomingVelocityGateway.resolveChannel("create instance", input);
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

      return new PasswordHashCodec(instance, input, target, (boolean)output);
   }

   @Override
   public String toString() {
      return "VelocityPlayerFactory{player=" + this.player + ", objectId=" + this.uniqueId + '}';
   }

   @Override
   public boolean equals(Object target) {
      if (target != null && this.getClass() == target.getClass()) {
         PasswordHashCodec input = (PasswordHashCodec)target;
         return Objects.equals(this.uniqueId, input.uniqueId) && Objects.equals(this.player, input.player);
      } else {
         return false;
      }
   }

   @Override
   public void executeTask() {
      this.player.resetTitle();
   }

   private PasswordHashCodec(DiscordForwarder target, Player input, ProxyServer output, boolean context) {
      this.discordForwarder = target;
      this.player = input;
      this.proxyServer = output;
      this.enabled = context;
   }

   @Override
   public boolean findState() {
      return this.enabled;
   }

   private static PasswordHashCodec handlePasswordHashCodec(DiscordForwarder instance, ProxyServer target, Player input) {
      PasswordHashCodec output = sessions.get(input);
      if (output != null) {
         return output;
      }

      if (PasswordHashContainer.findState()) {
         StackTraceElement[] context = new Exception().getStackTrace();
         String data = context.length > 0 ? context[Math.min(3, context.length - 1)].toString() : "unknown";
         PasswordHashContainer.performMessage("[nCore] Creating player instance for %s with no cache in \"%s\" context", input.getUsername(), data);
      }

      return loadPasswordHashCodec(instance, target, input);
   }

   @Override
   public int getCount() {
      return (int)this.player.getPing();
   }

   @Override
   public void saveMessage(String target, String input, int output, int context, int data) {
      this.player
         .showTitle(
            Title.title(
               SafeLoginService.resolveTextComponent(target),
               SafeLoginService.resolveTextComponent(input),
               Times.times(Duration.ofMillis(output * 50L), Duration.ofMillis(context * 50L), Duration.ofMillis(data * 50L))
            )
         );
   }

   @Override
   public void processMessageForValue(String target) {
      this.player.spoofChatInput(target);
   }

   @Override
   public boolean hasState(String target) {
      return this.player.hasPermission(target);
   }
}
