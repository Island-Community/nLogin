package com.nickuc.login.protocol;

import com.nickuc.login.auth.login.LocalLoginBarrier;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.auth.password.PasswordService;
import com.nickuc.login.auth.login.QuickLoginBarrier;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.SettingsRegistry;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.settings.PacketEventsSettings;
import com.github.retrooper.packetevents.util.FakeChannelUtil;
import com.github.retrooper.packetevents.util.TimeStampMode;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPluginMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import io.github.retrooper.packetevents.bungee.factory.BungeePacketEventsBuilder;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import io.github.retrooper.packetevents.util.protocolsupport.ProtocolSupportUtil;
import io.github.retrooper.packetevents.util.viaversion.ViaVersionUtil;
import io.github.retrooper.packetevents.velocity.factory.VelocityPacketEventsBuilder;
import com.nickuc.login.listener.bukkit.LocalLoginListener;
import com.nickuc.login.listener.bukkit.LocaleListener;
import com.nickuc.login.listener.bukkit.PacketFilter;
import com.nickuc.login.loader.platform.VelocityLoader;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.proxy.ProxyBridge;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginDao;
import com.nickuc.login.storage.packet.PacketArchive;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.settings.SettingsRepository;
import io.netty.channel.Channel;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Filter;
import java.util.logging.Level;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.Plugin;

public class RootMessageHandler {
   private final PacketHandler packetHandler;
   private boolean enabled;
   private final Map<PacketTypeCommon, QuickCommandHandler> sessions;
   private final SettingsRepository settingsRepository;
   private final PasswordStore passwordStore;
   private final PasswordService passwordService;
   private final Map<PacketTypeCommon, LenientCommandHandler> activeSessions = new HashMap<>();

   public boolean hasState(VerifiedServerAdapter target, Component input) {
      User output = PacketEvents.getAPI().getPlayerManager().getUser(target.findObject());
      if (output != null) {
         ClientVersion context = output.getPacketVersion();
         Object data;
         if (context.isNewerThanOrEquals(ClientVersion.V_1_19)) {
            data = new WrapperPlayServerSystemChatMessage(false, input);
         } else {
            Object value = context.isNewerThanOrEquals(ClientVersion.V_1_16)
               ? new ChatMessage_v1_16(input, ChatTypes.CHAT, new UUID(0L, 0L))
               : new ChatMessageLegacy(input, ChatTypes.CHAT);
            data = new WrapperPlayServerChatMessage((ChatMessage)value);
         }

         PacketEvents.getAPI().getProtocolManager().sendPacketSilently(output.getChannel(), (PacketWrapper)data);
      }

      return true;
   }

   private void processTask() {
      if (!"true".equalsIgnoreCase(System.getenv("PE_IGNORE_INCOMPATIBILITY"))) {
         String target = "https://ci.viaversion.com/job/ViaVersion-Java8/lastStableBuild/artifact/build/libs/ViaVersion-5.4.2-downgraded.jar";
         String input = "https://github.com/dmulloy2/ProtocolLib/releases/download/5.2.0/ProtocolLib.jar";
         boolean[] output = new boolean[]{false, false};
         byte context = 0;
         QuickLoginBarrier data = this.passwordStore.b().loadQuickLoginBarrier("ViaVersion");
         if (data != null && data.getMessage() != null) {
            String[] value = data.getMessage().split("\\.", 3);
            int result = Integer.parseInt(value[0]);
            int request = Integer.parseInt(value[1]);
            if (result < 4 || result == 4 && request < 5) {
               File response = MessageProcessor.handleFile(data.retrieveObject().getClass());
               File source = new File(this.passwordStore.resolveFile().getParentFile(), "ViaVersion-5.4.2.jar");
               LocalLoginBarrier entry = PendingPasswordHashHasher.getPendingPasswordHashHasher()
                  .loadLocalLoginBarrier("https://github.com/dmulloy2/ProtocolLib/releases/download/5.2.0/ProtocolLib.jar", source);
               if (entry.retrieveState()) {
                  File record = MessageProcessor.buildFile(response, MessageProcessor.resolveMessage(response) + "-%s.jar.old");
                  if (response.exists() && !response.renameTo(record) && !response.delete()) {
                     response.deleteOnExit();
                  }
               } else {
                  output[0] = true;
               }

               context = 1;
            }
         }

         QuickLoginBarrier element = this.passwordStore.b().loadQuickLoginBarrier("ProtocolLib");
         if (element != null && element.getMessage() != null) {
            int content = Integer.parseInt(element.getMessage().split("\\.", 2)[0]);
            if (content < 5) {
               File holder = MessageProcessor.handleFile(element.retrieveObject().getClass());
               File reference = new File(this.passwordStore.resolveFile().getParentFile(), "ProtocolLib-5.2.0.jar");
               LocalLoginBarrier subject = PendingPasswordHashHasher.getPendingPasswordHashHasher()
                  .loadLocalLoginBarrier("https://github.com/dmulloy2/ProtocolLib/releases/download/5.2.0/ProtocolLib.jar", reference);
               if (subject.retrieveState()) {
                  File option = MessageProcessor.buildFile(holder, MessageProcessor.resolveMessage(holder) + "-%s.jar.old");
                  if (holder.exists() && !holder.renameTo(option) && !holder.delete()) {
                     holder.deleteOnExit();
                  }
               } else {
                  output[1] = true;
               }

               context = 1;
            }
         }

         String payload = output[0] && output[1] ? "ViaVersion & ProtocolLib" : (output[0] ? "ViaVersion" : "ProtocolLib");
         if (!Arrays.equals(output, new boolean[]{false, false})) {
            if (CachedSettingsGateway.loadState()) {
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("Não foi possível atualizar o " + payload + " automaticamente.");
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("Por favor, acesse o link abaixo e faça o download abaixo:");
               if (output[0]) {
                  PasswordHashContainer.updateMessage(
                     "https://ci.viaversion.com/job/ViaVersion-Java8/lastStableBuild/artifact/build/libs/ViaVersion-5.4.2-downgraded.jar"
                  );
               }

               if (output[1]) {
                  PasswordHashContainer.updateMessage("https://github.com/dmulloy2/ProtocolLib/releases/download/5.2.0/ProtocolLib.jar");
               }

               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("O SERVIDOR DESLIGARÁ AUTOMATICAMENTE EM 30 SEGUNDOS");
               PasswordHashContainer.updateMessage("");
            } else {
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage(payload + " could not be updated automatically.");
               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("Please go to the link below and download below:");
               if (output[0]) {
                  PasswordHashContainer.updateMessage(
                     "https://ci.viaversion.com/job/ViaVersion-Java8/lastStableBuild/artifact/build/libs/ViaVersion-5.4.2-downgraded.jar"
                  );
               }

               if (output[1]) {
                  PasswordHashContainer.updateMessage("https://github.com/dmulloy2/ProtocolLib/releases/download/5.2.0/ProtocolLib.jar");
               }

               PasswordHashContainer.updateMessage("");
               PasswordHashContainer.updateMessage("THE SERVER WILL SHUT DOWN AUTOMATICALLY AFTER 30 SECONDS.");
               PasswordHashContainer.updateMessage("");
            }

            try {
               Thread.sleep(30000L);
            } catch (InterruptedException item) {
               PasswordHashContainer.sendThrowable(item);
            }
         }

         if (context != 0) {
            this.passwordStore.b().executeTask();
            throw new IllegalStateException("Incompatibility detected with PacketEvents: " + payload);
         }
      }
   }

   public boolean validateState(VerifiedServerAdapter target, String input, byte[] output) {
      if (this.passwordStore.loadState()) {
         throw new IllegalStateException("Cannot send plugin messages using PacketEvents under a proxy platform!");
      }

      WrapperPlayServerPluginMessage context = new WrapperPlayServerPluginMessage(input, output);
      PacketEvents.getAPI().getProtocolManager().sendPacket(this.createChannel(target), context);
      return true;
   }

   public void executeVerifiedServerAdapter(VerifiedServerAdapter target) {
      if (this.settingsRepository == null) {
         throw new IllegalStateException("Cannot send blank inventory using PacketEvents under a proxy platform!");
      }

      this.settingsRepository.updateChannel(this.createChannel(target));
   }

   public void disable() {
      PacketEvents.getAPI().getEventManager().unregisterListener(this.packetHandler);
      PacketEvents.getAPI().terminate();
   }

   @Nullable
   public Channel handleChannel(Object target, Object input) {
      try {
         Channel output = (Channel)PacketEvents.getAPI().getPlayerManager().getChannel(input);
         if (output == null) {
            PasswordHashContainer.updateMessage("Unable to get the channel from " + target.getClass().getCanonicalName() + " event");
         }

         return output;
      } catch (Exception context) {
         PasswordHashContainer.handleMessage("Unable to get the channel from " + target.getClass().getCanonicalName() + " event", context);
         return null;
      }
   }

   public RootMessageHandler(PasswordStore target) {
      this.sessions = new HashMap<>();
      this.packetHandler = new PacketHandler(this);
      this.enabled = true;
      this.passwordStore = target;
      this.executeTask();
      if (target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE && target.getState()) {
         this.passwordService = null;
      } else {
         LoginDao input = new LoginDao(target);
         this.sessions.put(Server.SYSTEM_CHAT_MESSAGE, input.messageAdapter);
         this.sessions.put(Server.CHAT_MESSAGE, input.messageHandler);
         this.activeSessions.put(Client.CHAT_COMMAND_UNSIGNED, input.localeAdapter);
         this.activeSessions.put(Client.CHAT_COMMAND, input.localeInterceptor);
         this.activeSessions.put(Client.CHAT_MESSAGE, input.messageHandler);
         this.activeSessions.put(Client.KEEP_ALIVE, new PacketArchive(target));
         this.passwordService = new PasswordService(target);
         if (target.findIncomingLoginGate().retrieveSilentProxyState().loadState()) {
            this.activeSessions
               .put(com.github.retrooper.packetevents.protocol.packettype.PacketType.Login.Client.LOGIN_SUCCESS_ACK, this.passwordService.passwordHasher);
            this.activeSessions
               .put(
                  com.github.retrooper.packetevents.protocol.packettype.PacketType.Configuration.Client.SELECT_KNOWN_PACKS,
                  this.passwordService.pendingSettingsInterceptor
               );
            this.activeSessions
               .put(
                  com.github.retrooper.packetevents.protocol.packettype.PacketType.Configuration.Client.CONFIGURATION_END_ACK,
                  this.passwordService.lowSettingsDefinition
               );
            this.sessions
               .put(
                  com.github.retrooper.packetevents.protocol.packettype.PacketType.Configuration.Server.CONFIGURATION_END,
                  this.passwordService.lowSettingsDefinition
               );
         }

         this.activeSessions
            .put(
               com.github.retrooper.packetevents.protocol.packettype.PacketType.Configuration.Client.CUSTOM_CLICK_ACTION,
               this.passwordService.lowPasswordChallenge
            );
         this.activeSessions.put(Client.CUSTOM_CLICK_ACTION, this.passwordService.lowPasswordChallenge);
      }

      if (target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE) {
         BukkitPlatform context = target.findObject();
         this.settingsRepository = new SettingsRepository(target);
         this.sessions.put(Server.SET_SLOT, this.settingsRepository.sharedPacketInterceptor);
         this.sessions.put(Server.WINDOW_ITEMS, this.settingsRepository.packetBridge);
         if (target.getState()) {
            this.activeSessions.put(com.github.retrooper.packetevents.protocol.packettype.PacketType.Login.Client.LOGIN_START, new PacketFilter());
            LocalLoginListener output = new LocalLoginListener(context);
            this.activeSessions
               .put(com.github.retrooper.packetevents.protocol.packettype.PacketType.Configuration.Client.PLUGIN_MESSAGE, output.settingsListener);
            this.activeSessions.put(Client.PLUGIN_MESSAGE, output.pendingMessageListener);
         } else {
            SettingsRegistry value = new SettingsRegistry(context);
            this.sessions.put(com.github.retrooper.packetevents.protocol.packettype.PacketType.Login.Server.ENCRYPTION_REQUEST, value.mojangBridge);
            this.activeSessions.put(com.github.retrooper.packetevents.protocol.packettype.PacketType.Login.Client.LOGIN_START, value.floodgateLinker);
            this.activeSessions.put(com.github.retrooper.packetevents.protocol.packettype.PacketType.Login.Client.ENCRYPTION_RESPONSE, value.mojangBridge);
            this.activeSessions.put(Client.CLIENT_SETTINGS, new LocaleListener(context));
            this.activeSessions.put(com.github.retrooper.packetevents.protocol.packettype.PacketType.Handshaking.Client.HANDSHAKE, new Sha256Handler());
         }
      } else {
         this.settingsRepository = null;
         ProxyBridge data = new ProxyBridge(target);
         this.activeSessions.put(com.github.retrooper.packetevents.protocol.packettype.PacketType.Login.Client.ENCRYPTION_RESPONSE, data);
         this.sessions.put(com.github.retrooper.packetevents.protocol.packettype.PacketType.Login.Server.ENCRYPTION_REQUEST, data);
      }

      PacketEvents.getAPI().getEventManager().registerListener(this.packetHandler);
   }

   private void executeTask() {
      this.processTask();
      Filter target = instance -> PasswordHashContainer.findState()
         || instance.getLevel().intValue() >= Level.WARNING.intValue()
            && !"PacketEvents caught an unhandled exception while calling your listener.".equals(instance.getMessage());
      PacketEventsSettings input = new PacketEventsSettings()
         .debug(false)
         .checkForUpdates(false)
         .kickOnPacketException(false)
         .reEncodeByDefault(false)
         .timeStampMode(TimeStampMode.MILLIS)
         .logFilter(target);
      PacketEventsAPI output;
      switch (this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState()) {
         case SILENT_PROXY_STATE:
            output = SpigotPacketEventsBuilder.build((Plugin)this.passwordStore.loadObject(), input);
            break;
         case ACTIVE_SILENTPROXYSTATE:
            output = BungeePacketEventsBuilder.build((net.md_5.bungee.api.plugin.Plugin)this.passwordStore.loadObject(), input);
            break;
         case PENDING_SILENTPROXYSTATE:
            VelocityLoader context = (VelocityLoader)this.passwordStore.loadObject();
            output = VelocityPacketEventsBuilder.build(context.getServer(), context.getPluginContainer(), context.getLogger(), context.getDataDirectory().toPath(), input);
            break;
         default:
            throw new IllegalStateException("Platform " + this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() + " not implemented!");
      }

      PacketEvents.setAPI(output);
      PacketEvents.getAPI().load();
      PacketEvents.getAPI().init();
   }

   public PasswordService fetchPasswordService() {
      if (this.passwordService == null) {
         throw new IllegalStateException("Cannot get dialog adapter under proxy mode!");
      } else {
         return this.passwordService;
      }
   }

   public Channel createChannel(VerifiedServerAdapter target) {
      Object input = PacketEvents.getAPI().getPlayerManager().getChannel(target.findObject());
      if (input == null) {
         throw new IllegalArgumentException("Unable to find channel for " + target.getName() + " " + target.getUniqueId());
      } else {
         return (Channel)input;
      }
   }

   public ClientVersion handleClientVersion(User target) {
      if (this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE) {
         if (this.enabled) {
            ViaVersionUtil.checkIfViaIsPresent();
            ProtocolSupportUtil.checkIfProtocolSupportIsPresent();
            this.enabled = false;
         }

         int input;
         if (ProtocolSupportUtil.isAvailable()) {
            input = ProtocolSupportUtil.getProtocolVersion(target.getAddress());
         } else if (ViaVersionUtil.isAvailable()) {
            input = ViaVersionUtil.getProtocolVersion(target);
         } else {
            input = target.getClientVersion().getProtocolVersion();
         }

         return ClientVersion.getById(input);
      } else {
         return target.getClientVersion();
      }
   }

   public boolean verifyState(Object target) {
      return FakeChannelUtil.isFakeChannel(target);
   }

   public User processUser(VerifiedServerAdapter target) {
      User input = PacketEvents.getAPI().getPlayerManager().getUser(target.findObject());
      if (input == null) {
         throw new IllegalArgumentException("Unable to find user for " + target.getName() + " " + target.getUniqueId());
      } else {
         return input;
      }
   }
}
