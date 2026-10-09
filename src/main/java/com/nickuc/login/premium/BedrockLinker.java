package com.nickuc.login.premium;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SharedNoticeKind;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.floodgate.FloodgateBarrier;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.IncomingSpawnState;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.ProxyState;
import com.nickuc.login.model.QuickMessageKind;
import com.nickuc.login.model.UpstreamLoginOption;
import com.nickuc.login.listener.RootServerAdapter;
import com.nickuc.login.listener.proxy.StrictBungeeListener;
import com.nickuc.login.loader.platform.BungeeLoader;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.AccountAdapter;
import com.nickuc.login.protocol.BungeeInterceptor;
import com.nickuc.login.bungee.BungeePlatform;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LowLimboTracker;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import io.netty.channel.Channel;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.function.Consumer;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.event.EventHandler;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class BedrockLinker implements RootServerAdapter {
   private final BungeePlatform BungeePlatform;
   private final PasswordStore passwordStore;

   @EventHandler
   public void updatePlayerDisconnectEvent(PlayerDisconnectEvent target) {
      ProxiedPlayer input = target.getPlayer();

      try {
         VerifiedServerAdapter output = this.BungeePlatform.b().processVerifiedServerAdapter(input);
         ((LowLimboTracker)this.passwordStore.getFloodgateResolver()).performVerifiedServerAdapter(output);
      } catch (Throwable context) {
         PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getName() + ")", context);
         input.disconnect(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
      }
   }

   private boolean validateState(PreLoginEvent target, String input, boolean output, SpawnLookup context) {
      VerifiedServerAdapter data = this.BungeePlatform.b().resolveVerifiedServerAdapter(input);
      if (data != null && data.loadState()) {
         LimboCoordinator value = this.passwordStore.loadLimboRegistry().buildLimboCoordinator(data);
         int result = value != null && System.currentTimeMillis() - value.a(LenientMessageKind.REMOTE_LENIENTMESSAGEKIND, 0L) > 7000L ? 1 : 0;
         InetAddress request = ((InetSocketAddress)target.getConnection().getSocketAddress()).getAddress();
         String response = request.getHostAddress();
         String source = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_LINKED_LOUDPROXYSTATE);
         if (result == 0
            || !output
               && (!SpawnState.ACTIVE_STORED_SPAWNSTATE.ar() || !request.equals(data.retrieveInetSocketAddress().getAddress()) && !response.equals(context.findMessage()))
            )
          {
            target.setCancelled(true);
            target.setCancelReason(TextComponent.fromLegacyText(source));
            return true;
         } else {
            data.buildCompletableFuture(source);
            return false;
         }
      } else {
         return false;
      }
   }

   private void sendPreLoginEvent(PreLoginEvent target) {
      PendingConnection input = target.getConnection();
      String output = input.getName();
      InetAddress context = ((InetSocketAddress)input.getSocketAddress()).getAddress();
      Channel data = StrictBungeeListener.buildChannel(target, input);
      if (data == null) {
         target.setCancelled(true);
         target.setCancelReason(TextComponent.fromLegacyText("Unable to get the channel from " + target.getClass().getSimpleName() + " event"));
      } else if (!this.passwordStore.resolveRootMessageHandler().verifyState(data)) {
         SecondaryConnectionContract value = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
         FloodgateBarrier result = null;
         UUID request = null;
         if (value instanceof ReadyBedrockResolver) {
            ReadyBedrockResolver response = (ReadyBedrockResolver)value;
            FloodgatePlayer source = response.loadFloodgatePlayer(input.getUniqueId());
            if (source != null) {
               String entry = source.getCorrectUsername();
               if (!output.equals(entry)) {
                  throw new IllegalStateException("Name is not equal to Bedrock correct name! " + output + " != " + entry);
               }

               result = new FloodgateBarrier(source);
               request = source.getJavaUniqueId();
            }
         }

         PrimaryLoginProcessor property = null;
         if (Pbkdf2Linker.findProxyState() != ProxyState.PENDING_PROXYSTATE && BungeeInterceptor.fetchState() && input.getVersion() >= 760) {
            try {
               property = new PrimaryLoginProcessor(BungeeInterceptor.buildUniqueId(input));
            } catch (IllegalAccessException setting) {
               PasswordHashContainer.updateMessage("Cannot retrieve connection ID: " + setting.getMessage());
            }
         }

         boolean attribute = QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState();
         if (attribute) {
            SpawnLookup item = null;
            String record;
            SharedNoticeKind element;
            String parameter;
            if (result != null) {
               parameter = output;
               record = output;
               item = this.passwordStore.findIndirectPasswordResolver().computeSpawnLookup(parameter, null, request, false);
               if (item == null) {
                  String profile = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                  target.setCancelled(true);
                  target.setCancelReason(TextComponent.fromLegacyText(profile));
                  return;
               }

               if (!this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract().fetchState() && !item.getState()) {
                  if (!this.passwordStore.findIndirectPasswordResolver().verifyState(item, output)) {
                     String player = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                     target.setCancelled(true);
                     target.setCancelReason(TextComponent.fromLegacyText(player));
                     return;
                  }

                  record = Pbkdf2Linker.handleMessage(item.loadMessage(parameter), item.fetchStateAndState());
               }

               element = null;
            } else {
               String content = input.getVirtualHost().getHostName();
               element = SharedNoticeKind.createSharedNoticeKind(content);
               if (!element.loadState() && property != null) {
                  element = property.fetchUniqueId() != null && property.hasState(this.BungeePlatform)
                     ? SharedNoticeKind.SHARED_NOTICE_KIND
                     : SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND;
                  if (element == SharedNoticeKind.SHARED_NOTICE_KIND) {
                     SpawnLookup payload = this.passwordStore.findIndirectPasswordResolver().createSpawnLookup(output, property.fetchUniqueId(), true);
                     if (payload == null) {
                        String backend = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        target.setCancelled(true);
                        target.setCancelReason(TextComponent.fromLegacyText(backend));
                        return;
                     }

                     String holder = payload.loadMessage(output);
                     QuickMessageKind reference = PasswordGateway.resolveQuickMessageKind(holder, context);
                     if (reference != QuickMessageKind.PENDING_QUICKMESSAGEKIND && payload.resolveStateForState() && !payload.fetchStateAndState()) {
                        PasswordGateway.savePasswordStore(this.passwordStore, holder, context, QuickMessageKind.ACTIVE_QUICKMESSAGEKIND);
                        item = payload;
                        if (reference != QuickMessageKind.PRIMARY_QUICKMESSAGEKIND) {
                           element = SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND;
                        }
                     }
                  }
               }

               if (!element.loadState() && this.passwordStore.fetchLocalSettingsRepository().loadState()) {
                  element = SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND;
               }

               switch (element) {
                  case SHARED_NOTICE_KIND:
                     input.setOnlineMode(true);
                     parameter = output;
                     record = Pbkdf2Linker.handleMessage(parameter, true);
                     break;
                  case ACTIVE_SHAREDNOTICEKIND:
                     if (item == null) {
                        item = this.passwordStore.findIndirectPasswordResolver().computeSpawnLookup(output, null, null, false);
                        if (item == null) {
                           String proxy = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                           target.setCancelled(true);
                           target.setCancelReason(TextComponent.fromLegacyText(proxy));
                           return;
                        }
                     }

                     if (!this.passwordStore.findIndirectPasswordResolver().verifyState(item, output)) {
                        String client = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        target.setCancelled(true);
                        target.setCancelReason(TextComponent.fromLegacyText(client));
                        return;
                     }

                     parameter = item.loadMessage(output);
                     record = Pbkdf2Linker.handleMessage(parameter, false);
                     UUID server = DeadLoginFlow.computeUniqueId(parameter);
                     if (input.isOnlineMode()) {
                        input.setOnlineMode(false);
                     }

                     try {
                        BungeeInterceptor.sendPendingConnection(input, record);
                        BungeeInterceptor.performPendingConnection(input, server);
                     } catch (IllegalAccessException option) {
                        PasswordHashContainer.handleMessage("Unable to set the correct name and id for " + parameter + ".", option);
                        target.setCancelled(true);
                        target.setCancelReason(TextComponent.fromLegacyText("§cInternal Error: Unable to set the correct name and id."));
                     }
                     break;
                  case PENDING_SHAREDNOTICEKIND:
                     String channel = CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_ROOT_LOUDPROXYSTATE, content);
                     target.setCancelled(true);
                     target.setCancelReason(TextComponent.fromLegacyText(channel));
                     return;
                  case CURRENT_SHAREDNOTICEKIND:
                     String connection;
                     if (CachedSettingsGateway.loadState()) {
                        connection = OpenLocaleBarrier.loadMessage(
                           "§4[nLogin] O hostname sendo usado §f(" + content + ") §4é válido para jogadores premium e offline ao mesmo tempo.",
                           "",
                           "§cVocê precisa definir hostnames diferentes para jogadores premium e offline.",
                           "",
                           "§ePara mais informações, veja o tutorial do username appender:",
                           "§bhttps://docs.nickuc.com/nlogin/username-appender"
                        );
                     } else {
                        connection = OpenLocaleBarrier.loadMessage(
                           "§4[nLogin] The hostname being used §f(" + content + ") §4is valid for premium and offline players at the same time.",
                           "",
                           "§cYou need to set different hostnames for premium and offline players.",
                           "",
                           "§eFor more information, check out the username appender tutorial:",
                           "§bhttps://docs.nickuc.com/nlogin/username-appender"
                        );
                     }

                     target.setCancelled(true);
                     target.setCancelReason(TextComponent.fromLegacyText(connection));
                     return;
                  default:
                     throw new IllegalArgumentException("Invalid type! " + element);
               }
            }

            if (this.validateState(target, record, result != null && item.getState(), item)) {
               return;
            }

            data.attr(AccountAdapter.attributeKey).set(new AccountAdapter(item, parameter, result, element));
         } else {
            SpawnLookup argument = this.passwordStore.findIndirectPasswordResolver().computeSpawnLookup(output, null, request, true);
            if (argument == null) {
               String notice = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
               target.setCancelled(true);
               target.setCancelReason(TextComponent.fromLegacyText(notice));
               return;
            }

            String message = output;
            if (result == null || !this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract().fetchState() && !argument.getState()) {
               if (!this.passwordStore.findIndirectPasswordResolver().verifyState(argument, output)) {
                  String session = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                  target.setCancelled(true);
                  target.setCancelReason(TextComponent.fromLegacyText(session));
                  return;
               }

               String event = argument.loadMessage(message);
               if (!message.equals(event)) {
                  try {
                     BungeeInterceptor.sendPendingConnection(input, event);
                  } catch (IllegalAccessException subject) {
                     PasswordHashContainer.handleMessage("Unable to set the correct name for " + message + ".", subject);
                     target.setCancelled(true);
                     target.setCancelReason(TextComponent.fromLegacyText("§cInternal Error: Unable to set the correct name."));
                  }

                  message = event;
               }
            }

            data.attr(AccountAdapter.attributeKey).set(new AccountAdapter(argument, message, result, null));
            if (result == null) {
               BungeeLoader packet = this.BungeePlatform.retrieveBungeeLoader();
               target.registerIntent(packet);
               String account = message;
               PrimaryLoginProcessor identity = property;
               this.BungeePlatform
                  .handleBungeeGateway(true)
                  .handleLocalBungeeBridge(
                     () -> {
                        try {
                           if (target.isCancelled()) {
                              return;
                           }

                           IncomingSpawnState requestValue = this.passwordStore.findIndirectPasswordResolver().loadIncomingSpawnState(argument, account, context, identity);
                           switch (requestValue) {
                              case INCOMING_SPAWN_STATE:
                                 target.setCancelled(true);
                                 target.setCancelReason(
                                    TextComponent.fromLegacyText(CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_TOP_LOUDPROXYSTATE, account))
                                 );
                                 return;
                              case ACTIVE_INCOMINGSPAWNSTATE:
                                 target.setCancelled(true);
                                 target.setCancelReason(
                                    TextComponent.fromLegacyText(CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_FAST_LOUDPROXYSTATE, account))
                                 );
                                 return;
                              case PENDING_INCOMINGSPAWNSTATE:
                                 this.validateState(target, account, false, argument);
                                 break;
                              case CURRENT_INCOMINGSPAWNSTATE:
                                 input.setOnlineMode(true);
                                 break;
                              default:
                                 throw new UnsupportedOperationException("Unsupported challenge result " + requestValue);
                           }
                        } finally {
                           target.completeIntent(packet);
                        }
                     }
                  );
            } else {
               this.validateState(target, message, argument.getState(), argument);
            }
         }
      }
   }

   public BedrockLinker(BungeePlatform target, PasswordStore input) {
      this.BungeePlatform = target;
      this.passwordStore = input;
   }

   @EventHandler(priority = 127)
   public void processPreLoginEvent(PreLoginEvent target) {
      if (!target.isCancelled()) {
         try {
            this.sendPreLoginEvent(target);
         } catch (Throwable output) {
            PasswordHashContainer.handleMessage(
               "Severe error during " + target.getClass().getSimpleName() + " event (" + target.getConnection().getName() + ")", output
            );
            target.setCancelled(true);
            target.setCancelReason(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
         }
      }
   }

   private void dispatchLoginEvent(LoginEvent target) {
      PendingConnection input = target.getConnection();
      String output = input.getName();
      UUID context = input.isOnlineMode() ? input.getUniqueId() : null;
      Consumer data = targetValue -> {
         target.setCancelled(true);
         target.setCancelReason(TextComponent.fromLegacyText(targetValue));
      };
      Channel value = StrictBungeeListener.buildChannel(target, input);
      if (value == null) {
         data.accept("Unable to get the channel from " + target.getClass().getSimpleName() + " event");
      } else if (!this.passwordStore.resolveRootMessageHandler().verifyState(value)) {
         AccountAdapter result = (AccountAdapter)value.attr(AccountAdapter.attributeKey).get();
         if (result == null) {
            String holder = "Unable to find connection cache for user "
               + output
               + " in "
               + target.getClass().getSimpleName()
               + " event (behavior changed by a plugin?)";
            PasswordHashContainer.performMessage(holder);
            data.accept(OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + holder, "", "§ePlease contact an administrator."));
         } else {
            if (context != null && result.floodgateBarrier != null) {
               throw new IllegalStateException("Online mode is enabled, but the player is using Bedrock!");
            }

            SpawnLookup request = result.spawnLookup;
            byte response = 0;
            IndirectPasswordResolver source = this.passwordStore.findIndirectPasswordResolver();
            if (context != null) {
               if (request != null && request.getMojangId() != null && !context.equals(request.getMojangId())) {
                  PasswordHashContainer.dispatchMessage(
                     "Mojang ID mismatch detected, is another account using an offline UUID? expected uuid = %s, received uuid = %s, expected name = %s, received name = %s",
                     request.getMojangId(),
                     context,
                     request.retrieveMessage(),
                     output
                  );
                  request = null;
                  response = 1;
               }

               SpawnLookup entry = source.computeSpawnLookup(result.name, context, null, false);
               if (entry == null) {
                  data.accept(CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                  return;
               }

               if (request == null || entry.resolveStateForState()) {
                  request = entry;
               }
            }

            AccountAdapter payload;
            value.attr(AccountAdapter.attributeKey).set(payload = result.createAccountAdapter(request, context == null && result.name != null ? result.name : output));
            UUID reference = input.getUniqueId();
            String record = ((InetSocketAddress)input.getSocketAddress()).getAddress().getHostAddress();
            String item = output;
            if (payload.floodgateBarrier != null) {
               if (this.passwordStore.findIndirectPasswordResolver().isState(data, request, record, SpawnState.ACTIVE_REMOTE_SPAWNSTATE)) {
                  return;
               }

               if (this.passwordStore.findIndirectPasswordResolver().canState(data, request, record, SpawnState.ACTIVE_UPSTREAM_SPAWNSTATE)) {
                  return;
               }

               if (!source.validateState(request, payload.floodgateBarrier.floodgatePlayer.getJavaUniqueId(), item, record)) {
                  data.accept(CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                  return;
               }

               UUID element = request.getUniqueId();
               if (element != null && !element.equals(reference) && QuickPremiumOption.SHARED_QUICKPREMIUMOPTION.retrieveState()) {
                  BungeeInterceptor.canState(target, element);
               }
            } else if (context != null) {
               if (this.passwordStore.findIndirectPasswordResolver().isState(data, request, record, SpawnState.ACTIVE_LOCAL_SPAWNSTATE)) {
                  return;
               }

               if (this.passwordStore.findIndirectPasswordResolver().canState(data, request, record, SpawnState.ACTIVE_INTERNAL_SPAWNSTATE)) {
                  return;
               }

               if (QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
                  item = Pbkdf2Linker.handleMessage(output, true);

                  try {
                     BungeeInterceptor.sendPendingConnection(input, item);
                  } catch (IllegalAccessException content) {
                     PasswordHashContainer.handleMessage("Unable to set the correct name for " + output + ".", content);
                     data.accept("§cInternal Error: Unable to set the correct name.");
                     return;
                  }
               }

               if (!source.canState(request, context, output, record, (boolean)response)) {
                  data.accept(CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                  return;
               }

               BungeeInterceptor.canState(target, request.getUniqueId());
            } else {
               if (this.passwordStore.findIndirectPasswordResolver().isState(data, request, record, null)) {
                  return;
               }

               if (this.passwordStore.findIndirectPasswordResolver().canState(data, request, record, null)) {
                  return;
               }

               UUID option = request.getUniqueId();
               if (option == null && (request.fetchState() || Pbkdf2Linker.resolveUpstreamLoginOption() == UpstreamLoginOption.UPSTREAM_LOGIN_OPTION)) {
                  request.handleUniqueId(option = DeadLoginFlow.createUniqueId(item, reference));
                  if (!source.isState(request)) {
                     data.accept(CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                     return;
                  }
               }

               if (option != null && !option.equals(reference)) {
                  BungeeInterceptor.canState(target, request.getUniqueId());
               }
            }
         }
      }
   }

   private void handlePreLoginEvent(PreLoginEvent target) {
      PendingConnection input = target.getConnection();
      String output = input.getName();
      InetAddress context = ((InetSocketAddress)input.getSocketAddress()).getAddress();
      SecondaryConnectionContract data = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
      int value = data != null && data.isState(input.getUniqueId()) ? 1 : 0;
      String result = this.passwordStore.getFloodgateResolver().resolveMessage(output, context, Boolean.valueOf((boolean)value));
      if (result != null) {
         target.setCancelled(true);
         target.setCancelReason(TextComponent.fromLegacyText(result));
      }
   }

   @EventHandler(priority = -64)
   public void dispatchPostLoginEvent(PostLoginEvent target) {
      ProxiedPlayer input = target.getPlayer();

      try {
         VerifiedServerAdapter output = this.BungeePlatform.b().processVerifiedServerAdapter(input);
         if (output.findState()) {
            return;
         }

         PendingConnection context = input.getPendingConnection();
         Channel data = StrictBungeeListener.buildChannel(target, context);
         String value = ((LowLimboTracker)this.passwordStore.getFloodgateResolver()).handleMessage(output, target, data, context.isOnlineMode());
         if (value != null) {
            input.disconnect(TextComponent.fromLegacyText(value));
         }
      } catch (Throwable result) {
         PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getName() + ")", result);
         input.disconnect(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
      }
   }

   @EventHandler(priority = -128)
   public void saveLoginEvent(LoginEvent target) {
      try {
         this.dispatchLoginEvent(target);
      } catch (Throwable output) {
         PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + target.getConnection().getName() + ")", output);
         target.setCancelled(true);
         target.setCancelReason(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
      }
   }

   @EventHandler(priority = -63)
   public void updatePreLoginEvent(PreLoginEvent target) {
      if (!target.isCancelled()) {
         try {
            this.handlePreLoginEvent(target);
         } catch (Throwable output) {
            PasswordHashContainer.handleMessage(
               "Severe error during " + target.getClass().getSimpleName() + " event (" + target.getConnection().getName() + ")", output
            );
            target.setCancelled(true);
            target.setCancelReason(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
         }
      }
   }
}
