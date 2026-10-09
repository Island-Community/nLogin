package com.nickuc.login.bedrock;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SharedNoticeKind;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.floodgate.FloodgateBarrier;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginProcessor;
import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.IncomingSpawnState;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.ProxyState;
import com.nickuc.login.model.QuickMessageKind;
import com.nickuc.login.model.UpstreamLoginOption;
import com.nickuc.login.platform.sender.StrictSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.ReadyBedrockResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.protocol.AccountAdapter;
import com.nickuc.login.protocol.BusyLoginListener;
import com.nickuc.login.proxy.IncomingVelocityGateway;
import com.nickuc.login.proxy.VerifiedVelocityForwarder;
import com.nickuc.login.velocity.VelocityPlatform;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LowLimboTracker;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import com.velocitypowered.api.event.Continuation;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.ResultedEvent.ComponentResult;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.InboundConnection;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import net.kyori.adventure.text.Component;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class FloodgateHandler implements StrictSenderAdapter {
   public static final AttributeKey<FloodgatePlayer> attributeKey = BusyLoginListener.loadAttributeKey("floodgate-player");
   private final VelocityPlatform VelocityPlatform;
   private final PasswordStore passwordStore;

   @Subscribe
   public void saveDisconnectEvent(DisconnectEvent target) {
      Player input = target.getPlayer();

      try {
         VerifiedServerAdapter output = this.VelocityPlatform.b().processVerifiedServerAdapter(input);
         ((LowLimboTracker)this.passwordStore.getFloodgateResolver()).performVerifiedServerAdapter(output);
      } catch (Throwable context) {
         PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getUsername() + ")", context);
         input.disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
      }
   }

   @Subscribe(order = PostOrder.LAST)
   public void sendGameProfileRequestEvent(GameProfileRequestEvent target) {
      Consumer input = targetValue -> VerifiedVelocityForwarder.updateInboundConnection(target.getConnection(), SafeLoginService.resolveTextComponent(targetValue));

      try {
         this.performGameProfileRequestEvent(target, input);
      } catch (Throwable context) {
         PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + target.getUsername() + ")", context);
         input.accept("§4[nLogin] Severe internal error detected. Please report to an admin.");
      }
   }

   private void processPreLoginEvent(PreLoginEvent target) {
      InboundConnection input = target.getConnection();
      String output = target.getUsername();
      InetAddress context = input.getRemoteAddress().getAddress();
      Channel data = IncomingVelocityGateway.resolveChannel(target, input);
      if (data == null) {
         target.setResult(PreLoginComponentResult.denied(Component.text("Unable to get the channel from " + target.getClass().getCanonicalName() + " event")));
      } else if (!this.passwordStore.resolveRootMessageHandler().verifyState(data)) {
         byte value = 0;
         if (this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract() instanceof ReadyBedrockResolver
            && data.hasAttr(attributeKey)) {
            FloodgatePlayer result = (FloodgatePlayer)data.attr(attributeKey).get();
            if (result != null) {
               output = result.getCorrectUsername();
               value = 1;
            }
         }

         String request = this.passwordStore.getFloodgateResolver().resolveMessage(output, context, Boolean.valueOf((boolean)value));
         if (request != null) {
            target.setResult(PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent(request)));
         }
      }
   }

   private void performGameProfileRequestEvent(GameProfileRequestEvent target, Consumer<String> input) {
      InboundConnection output = target.getConnection();
      InetSocketAddress context = output.getRemoteAddress();
      Channel data = IncomingVelocityGateway.resolveChannel(target, output);
      if (data == null) {
         input.accept("Unable to get the channel from " + target.getClass().getCanonicalName() + " event");
      } else if (!this.passwordStore.resolveRootMessageHandler().verifyState(data)) {
         AccountAdapter value = (AccountAdapter)data.attr(AccountAdapter.attributeKey).get();
         if (value == null) {
            String argument = "Unable to find connection cache for user "
               + target.getUsername()
               + " in "
               + target.getClass().getSimpleName()
               + " event (behavior changed by a plugin?)";
            PasswordHashContainer.performMessage(argument);
            input.accept(OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + argument, "", "§ePlease contact an administrator."));
         } else {
            GameProfile result = target.getGameProfile();
            GameProfile request = target.getOriginalProfile();
            String response = result.getName();
            UUID source = target.isOnlineMode() ? request.getId() : null;
            UUID entry = value.floodgateBarrier != null ? value.floodgateBarrier.floodgatePlayer.getJavaUniqueId() : null;
            String record = request.getName();
            if (source != null && value.floodgateBarrier != null) {
               throw new IllegalStateException("Online mode is enabled, but the player is using Bedrock!");
            }

            IndirectPasswordResolver item = this.passwordStore.findIndirectPasswordResolver();
            SpawnLookup element = value.spawnLookup;
            byte content = 0;
            if (source != null) {
               if (element != null && element.getMojangId() != null && !source.equals(element.getMojangId())) {
                  PasswordHashContainer.dispatchMessage(
                     "Mojang ID mismatch detected, is another account using an offline UUID? expected uuid = %s, received uuid = %s, expected name = %s, received name = %s",
                     element.getMojangId(),
                     source,
                     element.retrieveMessage(),
                     record
                  );
                  element = null;
                  content = 1;
               }

               SpawnLookup payload = item.computeSpawnLookup(record, source, null, false);
               if (payload == null) {
                  input.accept(CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                  return;
               }

               if (element == null || payload.resolveStateForState()) {
                  element = payload;
               }
            }

            if (element == null || value.name == null) {
               element = item.computeSpawnLookup(response, source, entry, false);
               if (element == null) {
                  String notice = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                  input.accept(notice);
                  return;
               }
            }

            byte message = QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState();
            if (message == (value.sharedNoticeKind == null ? 1 : 0)) {
               String event = "Invalid state for username appender, please rejoin";
               input.accept(event);
            } else {
               String holder;
               String reference;
               if (value.floodgateBarrier == null && message != 0) {
                  Optional packet = output.getVirtualHost();
                  if (!packet.isPresent()) {
                     throw new IllegalStateException("Missing virtual hosting for " + response);
                  }

                  String option = ((InetSocketAddress)packet.get()).getHostName();
                  switch (value.sharedNoticeKind) {
                     case SHARED_NOTICE_KIND:
                        holder = response;
                        reference = Pbkdf2Linker.handleMessage(holder, true);
                        break;
                     case ACTIVE_SHAREDNOTICEKIND:
                        if (!this.passwordStore.findIndirectPasswordResolver().verifyState(element, response)) {
                           String profile = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                           input.accept(profile);
                           return;
                        }

                        holder = element.loadMessage(response);
                        reference = Pbkdf2Linker.handleMessage(holder, false);
                        break;
                     case PENDING_SHAREDNOTICEKIND:
                        String player = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_ROOT_LOUDPROXYSTATE, option);
                        input.accept(player);
                        return;
                     case CURRENT_SHAREDNOTICEKIND:
                        String setting;
                        if (CachedSettingsGateway.loadState()) {
                           setting = OpenLocaleBarrier.loadMessage(
                              "§4[nLogin] O hostname sendo usado §f(" + option + ") §4é válido para jogadores premium e offline ao mesmo tempo.",
                              "",
                              "§cVocê precisa definir hostnames diferentes para jogadores premium e offline.",
                              "",
                              "§ePara mais informações, veja o tutorial do username appender:",
                              "§bhttps://docs.nickuc.com/nlogin/username-appender"
                           );
                        } else {
                           setting = OpenLocaleBarrier.loadMessage(
                              "§4[nLogin] The hostname being used §f(" + option + ") §4is valid for premium and offline players at the same time.",
                              "",
                              "§cYou need to set different hostnames for premium and offline players.",
                              "",
                              "§eFor more information, check out the username appender tutorial:",
                              "§bhttps://docs.nickuc.com/nlogin/username-appender"
                           );
                        }

                        input.accept(setting);
                        return;
                     default:
                        throw new IllegalArgumentException("Invalid type! " + value.sharedNoticeKind);
                  }
               } else if (source == null && value.floodgateBarrier == null) {
                  if (!this.passwordStore.findIndirectPasswordResolver().verifyState(element, response)) {
                     String subject = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                     input.accept(subject);
                     return;
                  }

                  holder = reference = element.loadMessage(response);
               } else {
                  reference = response;
                  holder = response;
               }

               data.attr(AccountAdapter.attributeKey).set(value.createAccountAdapter(element, holder));
               InetAddress session = context.getAddress();
               String account = session.getHostAddress();
               VerifiedServerAdapter identity = this.VelocityPlatform.b().resolveVerifiedServerAdapter(reference);
               if (identity != null && identity.loadState()) {
                  LimboCoordinator property = this.passwordStore.loadLimboRegistry().buildLimboCoordinator(identity);
                  int attribute = property != null && System.currentTimeMillis() - property.a(LenientMessageKind.REMOTE_LENIENTMESSAGEKIND, 0L) > 7000L ? 1 : 0;
                  String parameter = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_LINKED_LOUDPROXYSTATE);
                  if (attribute == 0
                     || value.floodgateBarrier == null
                        && (
                           !SpawnState.ACTIVE_STORED_SPAWNSTATE.ar()
                              || !session.equals(identity.retrieveInetSocketAddress().getAddress()) && !account.equals(element.findMessage())
                        )) {
                     input.accept(parameter);
                     return;
                  }

                  identity.buildCompletableFuture(parameter);
               }

               GameProfile connection;
               if (value.floodgateBarrier != null) {
                  if (this.passwordStore.findIndirectPasswordResolver().isState(input, element, account, SpawnState.ACTIVE_REMOTE_SPAWNSTATE)) {
                     return;
                  }

                  if (this.passwordStore.findIndirectPasswordResolver().canState(input, element, account, SpawnState.ACTIVE_UPSTREAM_SPAWNSTATE)) {
                     return;
                  }

                  if (!item.validateState(element, value.floodgateBarrier.floodgatePlayer.getJavaUniqueId(), holder, account)) {
                     input.accept(CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                     return;
                  }

                  if (!this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract().fetchState() && !element.getState()) {
                     if (!this.passwordStore.findIndirectPasswordResolver().verifyState(element, holder)) {
                        String server = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        input.accept(server);
                        return;
                     }

                     reference = element.loadMessage(holder);
                     if (QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
                        reference = Pbkdf2Linker.handleMessage(reference, element.fetchStateAndState());
                     }
                  }

                  UUID channel = element.getUniqueId();
                  connection = new GameProfile(
                     channel != null && QuickPremiumOption.SHARED_QUICKPREMIUMOPTION.retrieveState() ? channel : result.getId(), reference, result.getProperties()
                  );
               } else if (source != null) {
                  if (this.passwordStore.findIndirectPasswordResolver().isState(input, element, account, SpawnState.ACTIVE_LOCAL_SPAWNSTATE)) {
                     return;
                  }

                  if (this.passwordStore.findIndirectPasswordResolver().canState(input, element, account, SpawnState.ACTIVE_INTERNAL_SPAWNSTATE)) {
                     return;
                  }

                  if (!item.canState(element, source, holder, account, (boolean)content)) {
                     input.accept(CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                     return;
                  }

                  connection = new GameProfile(element.getUniqueId(), reference, result.getProperties());
               } else {
                  if (this.passwordStore.findIndirectPasswordResolver().isState(input, element, account, null)) {
                     return;
                  }

                  if (this.passwordStore.findIndirectPasswordResolver().canState(input, element, account, null)) {
                     return;
                  }

                  UUID client = DeadLoginFlow.computeUniqueId(holder);
                  UUID proxy = element.getUniqueId();
                  if (proxy == null && (element.fetchState() || Pbkdf2Linker.resolveUpstreamLoginOption() == UpstreamLoginOption.UPSTREAM_LOGIN_OPTION)) {
                     element.handleUniqueId(proxy = DeadLoginFlow.createUniqueId(holder, client));
                     if (!item.isState(element)) {
                        input.accept(CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                        return;
                     }
                  }

                  connection = new GameProfile(proxy != null ? proxy : client, reference, result.getProperties());
               }

               if (connection.getName().length() > 16) {
                  throw new IllegalArgumentException("Username longer than 16 characters! " + connection.getName());
               }

               target.setGameProfile(connection);
            }
         }
      }
   }

   private void sendPreLoginEvent(PreLoginEvent target, Continuation input) {
      InboundConnection output = target.getConnection();
      Channel context = IncomingVelocityGateway.resolveChannel(target, output);
      if (context == null) {
         target.setResult(PreLoginComponentResult.denied(Component.text("Unable to get the channel from " + target.getClass().getCanonicalName() + " event")));
         input.resume();
      } else if (!this.passwordStore.resolveRootMessageHandler().verifyState(context)) {
         FloodgateBarrier data = null;
         String value = null;
         UUID result = null;
         if (this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract() instanceof ReadyBedrockResolver
            && context.hasAttr(attributeKey)) {
            FloodgatePlayer request = (FloodgatePlayer)context.attr(attributeKey).get();
            if (request != null) {
               value = request.getCorrectUsername();
               result = request.getJavaUniqueId();
               data = new FloodgateBarrier(request);
            }
         }

         String holder = value != null ? value : target.getUsername();
         InetAddress response = output.getRemoteAddress().getAddress();
         PrimaryLoginProcessor source;
         if (Pbkdf2Linker.findProxyState() != ProxyState.PENDING_PROXYSTATE && output.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_19_1) >= 0) {
            source = new PrimaryLoginProcessor(target.getUniqueId());
         } else {
            source = null;
         }

         if (QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
            if (data != null) {
               context.attr(AccountAdapter.attributeKey).set(new AccountAdapter(null, null, data, SharedNoticeKind.SHARED_NOTICE_KIND));
               input.resume();
               return;
            }

            Optional entry = output.getVirtualHost();
            if (!entry.isPresent()) {
               input.resumeWithException(new IllegalStateException("Missing virtual hosting for " + holder));
               return;
            }

            String record = ((InetSocketAddress)entry.get()).getHostName();
            SharedNoticeKind item = SharedNoticeKind.createSharedNoticeKind(record);
            if (!item.loadState() && source != null) {
               item = source.fetchUniqueId() != null && source.hasState(this.VelocityPlatform)
                  ? SharedNoticeKind.SHARED_NOTICE_KIND
                  : SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND;
               if (item == SharedNoticeKind.SHARED_NOTICE_KIND) {
                  SpawnLookup element = this.passwordStore.findIndirectPasswordResolver().createSpawnLookup(holder, source.fetchUniqueId(), true);
                  if (element == null) {
                     String parameter = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                     target.setResult(PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent(parameter)));
                     input.resume();
                     return;
                  }

                  String content = element.loadMessage(holder);
                  QuickMessageKind payload = PasswordGateway.resolveQuickMessageKind(content, response);
                  if (payload != QuickMessageKind.PENDING_QUICKMESSAGEKIND && element.resolveStateForState() && !element.fetchStateAndState()) {
                     PasswordGateway.savePasswordStore(this.passwordStore, content, response, QuickMessageKind.ACTIVE_QUICKMESSAGEKIND);
                     if (payload != QuickMessageKind.PRIMARY_QUICKMESSAGEKIND) {
                        item = SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND;
                     }
                  }
               }
            }

            if (!item.loadState() && this.passwordStore.fetchLocalSettingsRepository().loadState()) {
               item = SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND;
            }

            context.attr(AccountAdapter.attributeKey).set(new AccountAdapter(null, null, null, item));
            switch (item) {
               case SHARED_NOTICE_KIND:
                  target.setResult(PreLoginComponentResult.forceOnlineMode());
                  break;
               case ACTIVE_SHAREDNOTICEKIND:
                  target.setResult(PreLoginComponentResult.forceOfflineMode());
                  break;
               case PENDING_SHAREDNOTICEKIND:
                  String attribute = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_ROOT_LOUDPROXYSTATE, record);
                  target.setResult(PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent(attribute)));
                  input.resume();
                  return;
               case CURRENT_SHAREDNOTICEKIND:
                  String property;
                  if (CachedSettingsGateway.loadState()) {
                     property = OpenLocaleBarrier.loadMessage(
                        "§4[nLogin] O hostname sendo usado §f(" + record + ") §4é válido para jogadores premium e offline ao mesmo tempo.",
                        "",
                        "§cVocê precisa definir hostnames diferentes para jogadores premium e offline.",
                        "",
                        "§ePara mais informações, veja o tutorial do username appender:",
                        "§bhttps://docs.nickuc.com/nlogin/username-appender"
                     );
                  } else {
                     property = OpenLocaleBarrier.loadMessage(
                        "§4[nLogin] The hostname being used §f(" + record + ") §4is valid for premium and offline players at the same time.",
                        "",
                        "§cYou need to set different hostnames for premium and offline players.",
                        "",
                        "§eFor more information, check out the username appender tutorial:",
                        "§bhttps://docs.nickuc.com/nlogin/username-appender"
                     );
                  }

                  target.setResult(PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent(property)));
                  input.resume();
                  return;
               default:
                  throw new IllegalArgumentException("Invalid type! " + item);
            }

            input.resume();
         } else {
            SpawnLookup reference = this.passwordStore.findIndirectPasswordResolver().computeSpawnLookup(holder, null, result, true);
            if (reference == null) {
               String setting = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
               target.setResult(PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent(setting)));
               input.resume();
               return;
            }

            if (!this.passwordStore.findIndirectPasswordResolver().verifyState(reference, holder)) {
               String option = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
               target.setResult(PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent(option)));
               input.resume();
               return;
            }

            String subject = reference.loadMessage(holder);
            context.attr(AccountAdapter.attributeKey).set(new AccountAdapter(reference, subject, data, null));
            if (data == null) {
               this.VelocityPlatform.resolvePrimaryVelocityForwarder(true).processVelocityLink(() -> {
                  try {
                     IncomingSpawnState resultValue = this.passwordStore.findIndirectPasswordResolver().loadIncomingSpawnState(reference, subject, response, source);
                     switch (resultValue) {
                        case INCOMING_SPAWN_STATE:
                           String sourceValue = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_TOP_LOUDPROXYSTATE, subject);
                           target.setResult(PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent(sourceValue)));
                           break;
                        case ACTIVE_INCOMINGSPAWNSTATE:
                           String requestValue = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_FAST_LOUDPROXYSTATE, subject);
                           target.setResult(PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent(requestValue)));
                           break;
                        case CURRENT_INCOMINGSPAWNSTATE:
                           target.setResult(PreLoginComponentResult.forceOnlineMode());
                        case PENDING_INCOMINGSPAWNSTATE:
                           break;
                        default:
                           throw new UnsupportedOperationException("Unsupported challenge result " + resultValue);
                     }

                     input.resume();
                  } catch (Throwable responseValue) {
                     input.resumeWithException(responseValue);
                  }
               });
            } else {
               input.resume();
            }
         }
      }
   }

   @Subscribe(order = PostOrder.LATE)
   public void handlePreLoginEvent(PreLoginEvent target, Continuation input) {
      if (!target.getResult().isAllowed()) {
         input.resume();
      } else {
         try {
            this.sendPreLoginEvent(target, input);
         } catch (Throwable context) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + target.getUsername() + ")", context);
            target.setResult(
               PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."))
            );
         }
      }
   }

   @Subscribe(order = PostOrder.EARLY)
   public void handlePreLoginEvent(PreLoginEvent target) {
      if (target.getResult().isAllowed()) {
         try {
            this.processPreLoginEvent(target);
         } catch (Throwable output) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + target.getUsername() + ")", output);
            target.setResult(
               PreLoginComponentResult.denied(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."))
            );
         }
      }
   }

   public FloodgateHandler(VelocityPlatform target, PasswordStore input) {
      this.VelocityPlatform = target;
      this.passwordStore = input;
   }

   @Subscribe
   public void saveLoginEvent(LoginEvent target) {
      if (target.getResult().isAllowed()) {
         Player input = target.getPlayer();

         try {
            VerifiedServerAdapter output = this.VelocityPlatform.b().processVerifiedServerAdapter(input);
            if (output.findState()) {
               return;
            }

            Channel context = this.passwordStore.resolveRootMessageHandler().handleChannel(target, input);
            String data = ((LowLimboTracker)this.passwordStore.getFloodgateResolver()).handleMessage(output, target, context, input.isOnlineMode());
            if (data != null) {
               target.setResult(ComponentResult.denied(SafeLoginService.resolveTextComponent(data)));
            }
         } catch (Throwable value) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getUsername() + ")", value);
            input.disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
         }
      }
   }
}
