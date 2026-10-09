package com.nickuc.login.premium;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SharedNoticeKind;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.floodgate.FloodgateBarrier;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginProcessor;
import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.SettingsRegistry;
import com.nickuc.login.model.IncomingSpawnState;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.ProxyState;
import com.nickuc.login.model.QuickMessageKind;
import com.nickuc.login.model.UpstreamLoginOption;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.crypto.SignatureData;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerEncryptionRequest;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.LenientCommandHandler;
import com.nickuc.login.protocol.MojangInterceptor;
import com.nickuc.login.protocol.PacketInterceptor;
import com.nickuc.login.protocol.SettingsInterceptor;
import com.nickuc.login.protocol.Sha256Bridge;
import com.nickuc.login.security.hashing.Sha256Digest;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordGateway;
import com.nickuc.login.storage.spawn.SpawnState;
import io.netty.channel.Channel;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.UUID;
import javax.annotation.Nullable;

import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class FloodgateLinker implements LenientCommandHandler {
   private final SecureRandom secureRandom;

   private void saveUser(
      User target, WrapperLoginClientLoginStart input, Channel output, Object context, SpawnLookup data, String value, String result, String request, InetAddress response
   ) {
      if (!this.validateState(target, result, response, false, data)) {
         if (!SettingsRegistry.computePasswordStore(this.settingsRegistry)
            .findIndirectPasswordResolver()
            .isState(targetValue -> SettingsInterceptor.handleUser(target, targetValue), data, response.getHostAddress(), null)) {
            if (!SettingsRegistry.computePasswordStore(this.settingsRegistry)
               .findIndirectPasswordResolver()
               .canState(targetValue -> SettingsInterceptor.handleUser(target, targetValue), data, response.getHostAddress(), null)) {
               UUID source = data.getUniqueId();
               if (source == null && (data.fetchState() || Pbkdf2Linker.resolveUpstreamLoginOption() == UpstreamLoginOption.UPSTREAM_LOGIN_OPTION)) {
                  data.handleUniqueId(source = DeadLoginFlow.createUniqueId(value, null));
                  if (!SettingsRegistry.computePasswordStore(this.settingsRegistry).findIndirectPasswordResolver().isState(data)) {
                     SettingsInterceptor.handleUser(target, CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                     return;
                  }
               }

               Runnable entry = source != null ? MojangInterceptor.computeRunnable(context, result, source, null) : null;
               PacketLink record = new PacketLink(target, data, value, source, false, entry, output, null);
               output.attr(PacketLink.attributeKey).set(record);
               PacketLink.updateMessage(result, request, response, record);
               SettingsRegistry.saveUser(target, input, result);
            }
         }
      }
   }

   private void handleUser(
      User target,
      WrapperLoginClientLoginStart input,
      Channel output,
      Object context,
      @Nullable SpawnLookup data,
      String value,
      @Nullable PrimaryLoginProcessor result,
      String request,
      InetAddress response,
      boolean source
   ) {
      if (!SettingsRegistry.computePasswordStore(this.settingsRegistry)
         .findIndirectPasswordResolver()
         .isState(targetValue -> SettingsInterceptor.handleUser(target, targetValue), data, response.getHostAddress(), SpawnState.ACTIVE_LOCAL_SPAWNSTATE)) {
         if (!SettingsRegistry.computePasswordStore(this.settingsRegistry)
            .findIndirectPasswordResolver()
            .canState(targetValue -> SettingsInterceptor.handleUser(target, targetValue), data, response.getHostAddress(), SpawnState.ACTIVE_INTERNAL_SPAWNSTATE)) {
            Sha256Digest entry = null;
            SignatureData record = (SignatureData)input.getSignatureData().orElse(null);
            if (record != null) {
               entry = new Sha256Digest(record, result != null ? result.fetchUniqueId() : null);
               if (entry.resolveState()) {
                  SettingsInterceptor.sendUser(target, "multiplayer.disconnect.invalid_public_key_signature");
                  return;
               }

               if (!entry.retrieveState()) {
                  SettingsInterceptor.sendUser(target, "multiplayer.disconnect.invalid_public_key");
                  return;
               }
            }

            byte[] item;
            if (source) {
               item = null;
            } else {
               item = new byte[4];
               this.secureRandom.nextBytes(item);
               target.sendPacketSilently(new WrapperLoginServerEncryptionRequest("", SettingsRegistry.computeKeyPair(this.settingsRegistry).getPublic(), item));
            }

            output.attr(PacketInterceptor.attributeKey).set(new PacketInterceptor(data, value, request, context, input, entry, item));
            if (source) {
               target.receivePacketSilently(input);
            }
         }
      }
   }

   private boolean validateState(User target, String input, InetAddress output, boolean context, SpawnLookup data) {
      VerifiedServerAdapter value = SettingsRegistry.createNLoginBukkit(this.settingsRegistry).b().resolveVerifiedServerAdapter(input);
      if (value == null) {
         return false;
      } else {
         LimboCoordinator result = SettingsRegistry.computePasswordStore(this.settingsRegistry).loadLimboRegistry().buildLimboCoordinator(value);
         int request = result != null && System.currentTimeMillis() - result.a(LenientMessageKind.REMOTE_LENIENTMESSAGEKIND, 0L) > 7000L ? 1 : 0;
         String response = output.getHostAddress();
         String source = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_LINKED_LOUDPROXYSTATE);
         if (request == 0
            || !context
               && (!SpawnState.ACTIVE_STORED_SPAWNSTATE.ar() || !output.equals(value.retrieveInetSocketAddress().getAddress()) && !response.equals(data.findMessage()))
            )
          {
            SettingsInterceptor.handleUser(target, source);
            return true;
         } else {
            value.buildCompletableFuture(source);
            return false;
         }
      }
   }

   private FloodgateLinker(SettingsRegistry target) {
      this.settingsRegistry = target;
      this.secureRandom = new SecureRandom();
   }

   private void saveUser(User target, Channel input, Sha256Bridge output, FloodgateBarrier context, WrapperLoginClientLoginStart data) {
      String value = data.getUsername();
      PrimaryLoginProcessor result = Pbkdf2Linker.findProxyState() != ProxyState.PENDING_PROXYSTATE
            && target.getPacketVersion().isNewerThanOrEquals(ClientVersion.V_1_19_1)
         ? new PrimaryLoginProcessor((UUID)data.getPlayerUUID().orElse(null))
         : null;
      InetAddress request = target.getAddress().getAddress();
      String response = SettingsRegistry.computePasswordStore(this.settingsRegistry).getFloodgateResolver().resolveMessage(value, request, context != null);
      if (response != null) {
         SettingsInterceptor.handleUser(target, response);
      } else {
         Object source = output.resolveObject();
         if (context != null) {
            this.sendUser(target, data, input, source, context, value, request);
         } else {
            boolean entry = QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState();
            SpawnLookup record = null;
            int item;
            String element;
            String content;
            if (entry) {
               String payload = output.loadMessage();
               SharedNoticeKind holder = SharedNoticeKind.createSharedNoticeKind(payload);
               if (!holder.loadState() && result != null) {
                  holder = result.fetchUniqueId() != null && result.hasState(SettingsRegistry.createNLoginBukkit(this.settingsRegistry))
                     ? SharedNoticeKind.SHARED_NOTICE_KIND
                     : SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND;
                  if (holder == SharedNoticeKind.SHARED_NOTICE_KIND) {
                     SpawnLookup reference = SettingsRegistry.computePasswordStore(this.settingsRegistry)
                        .findIndirectPasswordResolver()
                        .createSpawnLookup(value, result.fetchUniqueId(), true);
                     if (reference == null) {
                        String packet = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        SettingsInterceptor.handleUser(target, packet);
                        return;
                     }

                     String subject = reference.loadMessage(value);
                     QuickMessageKind option = PasswordGateway.resolveQuickMessageKind(subject, request);
                     if (option != QuickMessageKind.PENDING_QUICKMESSAGEKIND && reference.resolveStateForState() && !reference.fetchStateAndState()) {
                        PasswordGateway.savePasswordStore(
                           SettingsRegistry.computePasswordStore(this.settingsRegistry), subject, request, QuickMessageKind.ACTIVE_QUICKMESSAGEKIND
                        );
                        record = reference;
                        if (option != QuickMessageKind.PRIMARY_QUICKMESSAGEKIND) {
                           holder = SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND;
                        }
                     }
                  }
               }

               if (!holder.loadState() && SettingsRegistry.computePasswordStore(this.settingsRegistry).fetchLocalSettingsRepository().loadState()) {
                  holder = SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND;
               }

               switch (holder) {
                  case SHARED_NOTICE_KIND:
                     element = value;
                     content = Pbkdf2Linker.handleMessage(element, true);
                     item = 1;
                     break;
                  case ACTIVE_SHAREDNOTICEKIND:
                     if (record == null) {
                        record = SettingsRegistry.computePasswordStore(this.settingsRegistry)
                           .findIndirectPasswordResolver()
                           .computeSpawnLookup(value, null, null, false);
                        if (record == null) {
                           String event = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                           SettingsInterceptor.handleUser(target, event);
                           return;
                        }
                     }

                     if (!SettingsRegistry.computePasswordStore(this.settingsRegistry).findIndirectPasswordResolver().verifyState(record, value)) {
                        String notice = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        SettingsInterceptor.handleUser(target, notice);
                        return;
                     }

                     element = record.loadMessage(value);
                     content = Pbkdf2Linker.handleMessage(element, false);
                     item = 0;
                     break;
                  case PENDING_SHAREDNOTICEKIND:
                     String message = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_ROOT_LOUDPROXYSTATE, payload);
                     SettingsInterceptor.handleUser(target, message);
                     return;
                  case CURRENT_SHAREDNOTICEKIND:
                     String argument = CachedSettingsGateway.loadState()
                        ? OpenLocaleBarrier.loadMessage(
                           "§4[nLogin] O hostname sendo usado §f(" + payload + ") §4é válido para jogadores premium e offline ao mesmo tempo.",
                           "",
                           "§cVocê precisa definir hostnames diferentes para jogadores premium e offline.",
                           "",
                           "§ePara mais informações, veja o tutorial do username appender:",
                           "§bhttps://docs.nickuc.com/nlogin/username-appender"
                        )
                        : OpenLocaleBarrier.loadMessage(
                           "§4[nLogin] The hostname being used §f(" + payload + ") §4is valid for premium and offline players at the same time.",
                           "",
                           "§cYou need to set different hostnames for premium and offline players.",
                           "",
                           "§eFor more information, check out the username appender tutorial:",
                           "§bhttps://docs.nickuc.com/nlogin/username-appender"
                        );
                     SettingsInterceptor.handleUser(target, argument);
                     return;
                  default:
                     throw new IllegalArgumentException("Invalid type! " + holder);
               }
            } else {
               record = SettingsRegistry.computePasswordStore(this.settingsRegistry).findIndirectPasswordResolver().computeSpawnLookup(value, null, null, true);
               if (record == null) {
                  String parameter = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                  SettingsInterceptor.handleUser(target, parameter);
                  return;
               }

               if (!SettingsRegistry.computePasswordStore(this.settingsRegistry).findIndirectPasswordResolver().verifyState(record, value)) {
                  String attribute = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                  SettingsInterceptor.handleUser(target, attribute);
                  return;
               }

               content = element = record.loadMessage(value);
               IncomingSpawnState setting = SettingsRegistry.computePasswordStore(this.settingsRegistry)
                  .findIndirectPasswordResolver()
                  .loadIncomingSpawnState(record, element, request, result);
               switch (setting) {
                  case INCOMING_SPAWN_STATE:
                     SettingsInterceptor.handleUser(target, CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_TOP_LOUDPROXYSTATE, element));
                     return;
                  case ACTIVE_INCOMINGSPAWNSTATE:
                     SettingsInterceptor.handleUser(target, CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_FAST_LOUDPROXYSTATE, element));
                     return;
                  case PENDING_INCOMINGSPAWNSTATE:
                  case CURRENT_INCOMINGSPAWNSTATE:
                     item = setting == IncomingSpawnState.CURRENT_INCOMINGSPAWNSTATE ? 1 : 0;
                     break;
                  default:
                     throw new UnsupportedOperationException("Unsupported challenge result " + setting);
               }
            }

            boolean property = SettingsRegistry.createNLoginBukkit(this.settingsRegistry).getServer().getOnlineMode();
            if (property) {
               item = 1;
            }

            if (item != 0) {
               this.handleUser(target, data, input, source, record, element, result, value, request, property);
            } else {
               this.saveUser(target, data, input, source, record, element, content, value, request);
            }
         }
      }
   }

   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      WrapperLoginClientLoginStart input = new WrapperLoginClientLoginStart(target);
      User output = target.getUser();
      Channel context = (Channel)target.getChannel();
      String data = input.getUsername();
      Sha256Bridge value = (Sha256Bridge)context.attr(Sha256Bridge.attributeKey).getAndSet(null);
      if (value == null) {
         throw new IllegalStateException("Handshake data is null in LOGIN state for " + data + "!");
      }

      SecondaryConnectionContract request = SettingsRegistry.computePasswordStore(this.settingsRegistry)
         .loadInternalAccountHandler()
         .retrieveSecondaryConnectionContract();
      FloodgateBarrier result;
      if (request instanceof ReadyBedrockResolver) {
         FloodgatePlayer response = (FloodgatePlayer)context.attr(((ReadyBedrockResolver)request).fetchAttributeKey()).get();
         if (response == null && context.pipeline().get("floodgate_data_handler") != null) {
            SettingsInterceptor.handleUser(output, "Invalid state for Bedrock, please rejoin");
            return;
         }

         result = response != null ? new FloodgateBarrier(response) : null;
      } else {
         result = null;
      }

      target.setCancelled(true);
      SettingsRegistry.createNLoginBukkit(this.settingsRegistry).processLinkedSessionHandler(true).buildStrictCommandHandler(() -> {
         try {
            if (!context.isOpen() || !context.isActive()) {
               return;
            }

            this.saveUser(output, context, value, result, input);
         } catch (Throwable requestValue) {
            SettingsInterceptor.handleUser(output, "[nLogin] Exception during login process. Check the console for more details.");
            PasswordHashContainer.handleMessage("Unexpected exception during login process for " + data + " (probably this is a bug!)", requestValue);
         }
      });
   }

   private void sendUser(User target, WrapperLoginClientLoginStart input, Channel output, Object context, FloodgateBarrier data, String value, InetAddress result) {
      String request = data.floodgatePlayer.getCorrectUsername();
      UUID response = data.floodgatePlayer.getJavaUniqueId();
      SpawnLookup source = SettingsRegistry.computePasswordStore(this.settingsRegistry)
         .findIndirectPasswordResolver()
         .computeSpawnLookup(request, null, response, false);
      if (source == null) {
         String holder = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
         SettingsInterceptor.handleUser(target, holder);
      } else if (!this.validateState(target, request, result, source.getState(), source)) {
         if (!SettingsRegistry.computePasswordStore(this.settingsRegistry)
            .findIndirectPasswordResolver()
            .isState(targetValue -> SettingsInterceptor.handleUser(target, targetValue), source, result.getHostAddress(), SpawnState.ACTIVE_REMOTE_SPAWNSTATE)) {
            if (!SettingsRegistry.computePasswordStore(this.settingsRegistry)
               .findIndirectPasswordResolver()
               .canState(targetValue -> SettingsInterceptor.handleUser(target, targetValue), source, result.getHostAddress(), SpawnState.ACTIVE_UPSTREAM_SPAWNSTATE)) {
               if (!SettingsRegistry.computePasswordStore(this.settingsRegistry)
                  .findIndirectPasswordResolver()
                  .validateState(source, response, request, result.getHostAddress())) {
                  SettingsInterceptor.handleUser(target, CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
               } else {
                  if (!SettingsRegistry.computePasswordStore(this.settingsRegistry)
                        .loadInternalAccountHandler()
                        .retrieveSecondaryConnectionContract()
                        .fetchState()
                     && !source.getState()) {
                     if (!SettingsRegistry.computePasswordStore(this.settingsRegistry).findIndirectPasswordResolver().verifyState(source, value)) {
                        String payload = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        SettingsInterceptor.handleUser(target, payload);
                        return;
                     }

                     String entry = source.retrieveMessage();
                     request = entry != null ? entry : value;
                     if (QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
                        request = Pbkdf2Linker.handleMessage(request, source.fetchStateAndState());
                     }
                  }

                  UUID content = source.getUniqueId();
                  UUID record = content != null && QuickPremiumOption.SHARED_QUICKPREMIUMOPTION.retrieveState()
                     ? content
                     : data.floodgatePlayer.getCorrectUniqueId();
                  Runnable item = MojangInterceptor.computeRunnable(context, request, record, null);
                  PacketLink element = new PacketLink(target, source, request, record, true, item, output, null);
                  output.attr(PacketLink.attributeKey).set(element);
                  PacketLink.updateMessage(request, value, result, element);
                  input.setPlayerUUID(record);
                  input.setUsername(request);
                  SettingsRegistry.saveUser(target, input, request);
               }
            }
         }
      }
   }
}
