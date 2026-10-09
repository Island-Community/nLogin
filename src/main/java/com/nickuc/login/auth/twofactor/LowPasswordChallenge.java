package com.nickuc.login.auth.twofactor;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SpawnOption;
import com.nickuc.login.auth.account.LocalAccountGate;
import com.nickuc.login.auth.locale.LocaleFlow;
import com.nickuc.login.auth.password.PasswordService;
import com.nickuc.login.auth.account.SharedAccountGate;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.NoticeKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.configuration.client.WrapperConfigClientCustomClickAction;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCustomClickAction;
import com.nickuc.login.platform.packet.LoudPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.protocol.DialogHandler;
import com.nickuc.login.protocol.LenientCommandHandler;
import com.nickuc.login.protocol.SettingsInterceptor;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.spawn.SpawnState;
import io.netty.channel.Channel;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class LowPasswordChallenge implements LenientCommandHandler {
   private String loadMessage(User target, ResourceLocation input, NBT output, @Nullable Object context, Channel data, boolean value) {
      if (!(output instanceof NBTCompound)) {
         return "§4[nLogin] §cUnexpected dialog state: invalid NBT";
      }

      NBTCompound result = (NBTCompound)output;
      VerifiedServerAdapter request = context != null ? PasswordService.resolvePasswordStore(this.passwordService).b().processVerifiedServerAdapter(context) : null;
      if (value && request == null) {
         return "§4[nLogin] §cUnexpected dialog state: player is null";
      }

      if (request != null) {
         LimboCoordinator response = PasswordService.resolvePasswordStore(this.passwordService).loadLimboRegistry().loadLimboCoordinator(request);
         if (response.loadTightPlatformCatalog().isState(TightPlatformCatalog.PRIMARY_TIGHTPLATFORMCATALOG)) {
            return null;
         }
      }

      switch (input.getKey()) {
         case "login/confirm":
         case "login/exit":
            if (!"login/confirm".equals(input.getKey())) {
               if (value) {
                  LimboCoordinator parameter = PasswordService.resolvePasswordStore(this.passwordService).loadLimboRegistry().loadLimboCoordinator(request);
                  parameter.updateLenientMessageKind(LenientMessageKind.ROOT_LENIENTMESSAGEKIND, true);
                  return null;
               }

               return "";
            } else {
               LoudPacketAdapter attribute = PasswordService.resolveLoudPacketAdapter(this.passwordService, target, data);
               if (attribute == null) {
                  return null;
               } else {
                  String argument = result.getStringTagValueOrNull("password");
                  if (argument == null) {
                     return "§4[nLogin] §cUnexpected dialog state: missing password";
                  } else {
                     SpawnLookup message = attribute.loadSpawnLookup();
                     if (!message.retrieveState()) {
                        return CachedSettingsGateway.computeMessage(LoudProxyState.INCOMING_LOUDPROXYSTATE);
                     } else if (message.fetchStateAndState() && QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()) {
                        return null;
                     } else {
                        if (!PasswordService.resolvePasswordStore(this.passwordService).findIndirectPasswordResolver().checkState(message, argument)) {
                           Long event = PasswordService.resolvePasswordStore(this.passwordService)
                              .findIndirectPasswordResolver()
                              .resolveLong(message, message.fetchMessage(), target.getAddress().getAddress().getHostAddress());
                           if (event != null) {
                              return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_INTERNAL_LOUDPROXYSTATE, LocaleFlow.createMessage(event));
                           }

                           return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_SHARED_LOUDPROXYSTATE);
                        }

                        if (value) {
                           LimboCoordinator notice = PasswordService.resolvePasswordStore(this.passwordService).loadLimboRegistry().loadLimboCoordinator(request);
                           PasswordService.resolvePasswordStore(this.passwordService)
                              .findSettingsLinker()
                              .sendSpawnLookup(message, request, notice, argument, true, false);
                        } else {
                           attribute.sendMessage(argument, null);
                        }

                        return null;
                     }
                  }
               }
            }
         case "login/recover":
            if (value) {
               LimboCoordinator setting = PasswordService.resolvePasswordStore(this.passwordService).loadLimboRegistry().loadLimboCoordinator(request);
               setting.updateLenientMessageKind(LenientMessageKind.ROOT_LENIENTMESSAGEKIND, true);
               setting.updateLenientMessageKind(LenientMessageKind.TOP_LENIENTMESSAGEKIND, true);
            } else {
               LoudPacketAdapter property = PasswordService.resolveLoudPacketAdapter(this.passwordService, target, data);
               if (property == null) {
                  return null;
               }

               property.updateTask();
            }

            return null;
         case "register/confirm":
         case "register/exit":
            if (!"register/confirm".equals(input.getKey())) {
               if (value) {
                  LimboCoordinator option = PasswordService.resolvePasswordStore(this.passwordService).loadLimboRegistry().loadLimboCoordinator(request);
                  option.updateLenientMessageKind(LenientMessageKind.ROOT_LENIENTMESSAGEKIND, true);
                  return null;
               }

               return "";
            } else {
               LoudPacketAdapter entry = PasswordService.resolveLoudPacketAdapter(this.passwordService, target, data);
               if (entry == null) {
                  return null;
               } else {
                  String record = result.getStringTagValueOrNull("password1");
                  if (record == null) {
                     return "§4[nLogin] §cUnexpected dialog state: missing password 1";
                  } else {
                     String item = result.getStringTagValueOrNull("password2");
                     if (item == null) {
                        return "§4[nLogin] §cUnexpected dialog state: missing password 2";
                     } else {
                        int element = record.length();
                        if (element <= SpawnState.ACTIVE_OUTGOING_SPAWNSTATE.r()) {
                           return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_REMOTE_LOUDPROXYSTATE);
                        } else if (element >= SpawnState.ACTIVE_SECONDARY_SPAWNSTATE.r()) {
                           return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_LOCAL_LOUDPROXYSTATE);
                        } else if (SpawnState.ACTIVE_DIRECT_SPAWNSTATE.ar() && !Pbkdf2Linker.findPattern().matcher(record).matches()) {
                           return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_CACHED_LOUDPROXYSTATE);
                        } else if (!record.equals(item)) {
                           return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_STORED_LOUDPROXYSTATE);
                        } else {
                           SpawnLookup content = entry.loadSpawnLookup();
                           if (content.retrieveState()) {
                              return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_CURRENT_LOUDPROXYSTATE);
                           } else {
                              SpawnOption payload = content.loadSpawnOption();
                              if (payload != SpawnOption.SPAWN_OPTION && payload != SpawnOption.PENDING_SPAWNOPTION) {
                                 String holder = ((InetSocketAddress)data.remoteAddress()).getAddress().getHostAddress();
                                 if (SpawnState.ACTIVE_VERIFIED_SPAWNSTATE.ar() && !SpawnState.ACTIVE_INCOMING_SPAWNSTATE.a(new Object[0]).contains(holder)) {
                                    LocalAccountGate reference = PasswordService.resolvePasswordStore(this.passwordService)
                                       .findIndirectPasswordResolver()
                                       .createLocalAccountGate(holder);
                                    if (reference == null) {
                                       return CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
                                    }

                                    if (reference.canState(SpawnState.ACTIVE_AUTHENTICATED_SPAWNSTATE.r())) {
                                       return CachedSettingsGateway.computeMessage(
                                          LoudProxyState.SECONDARY_LOUDPROXYSTATE,
                                          reference.loadCollection().stream().map(SharedAccountGate::getName).collect(Collectors.joining(", "))
                                       );
                                    }
                                 }
                              }

                              String packet = Pbkdf2Linker.loadUpstreamSpawnState().computeMessage(record);
                              if (value) {
                                 LimboCoordinator session = PasswordService.resolvePasswordStore(this.passwordService)
                                    .loadLimboRegistry()
                                    .loadLimboCoordinator(request);
                                 PasswordService.resolvePasswordStore(this.passwordService)
                                    .findSettingsLinker()
                                    .processSpawnLookup(content, request, session, record, packet, true, false);
                              } else {
                                 entry.sendMessage(record, packet);
                              }

                              return null;
                           }
                        }
                     }
                  }
               }
            }
         default:
            return "§4[nLogin] §cUnexpected dialog state: invalid key \"" + input.getKey() + "\"";
      }
   }

   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      PacketTypeCommon input = target.getPacketType();
      if (input == Client.CUSTOM_CLICK_ACTION) {
         if (Pbkdf2Linker.retrieveNoticeKind() != NoticeKind.PENDING_NOTICEKIND) {
            return;
         }

         WrapperPlayClientCustomClickAction output = new WrapperPlayClientCustomClickAction(target);
         ResourceLocation context = output.getId();
         if (!"nlogin".equals(context.getNamespace())) {
            return;
         }

         Channel data = (Channel)target.getChannel();
         DialogHandler value = data.hasAttr(DialogHandler.attributeKey) ? (DialogHandler)data.attr(DialogHandler.attributeKey).get() : null;
         User result = target.getUser();
         if (result == null) {
            return;
         }

         if (value == null) {
            PasswordService.processPasswordService(this.passwordService, result);
            SettingsInterceptor.handleUser(result, "§4[nLogin] §cUnexpected dialog state: unexpected response");
            return;
         }

         Object request = target.getPlayer();
         if (request == null) {
            PasswordService.processPasswordService(this.passwordService, result);
            SettingsInterceptor.handleUser(result, "§4[nLogin] §cUnexpected dialog state: player is null");
            return;
         }

         target.setCancelled(true);
         PasswordService.resolvePasswordStore(this.passwordService).processLinkedSessionHandler(true).buildStrictCommandHandler(() -> {
            if (data.isActive()) {
               String resultValue = this.loadMessage(result, context, output.getPayload(), request, (Channel)target.getChannel(), true);
               if (resultValue != null) {
                  PasswordService.processPasswordService(this.passwordService, result);
                  SettingsInterceptor.handleUser(result, resultValue);
               } else {
                  PasswordService.processPasswordService(this.passwordService, result);
               }
            }
         });
      } else {
         if (input != com.github.retrooper.packetevents.protocol.packettype.PacketType.Configuration.Client.CUSTOM_CLICK_ACTION) {
            throw new IllegalArgumentException("Unexpected packet type! " + target.getPacketType());
         }

         if (Pbkdf2Linker.retrieveNoticeKind() != NoticeKind.ACTIVE_NOTICEKIND) {
            return;
         }

         WrapperConfigClientCustomClickAction response = new WrapperConfigClientCustomClickAction(target);
         ResourceLocation source = response.getId();
         if (!"nlogin".equals(source.getNamespace())) {
            return;
         }

         User entry = target.getUser();
         if (entry == null) {
            return;
         }

         Channel record = (Channel)target.getChannel();
         DialogHandler item = record.hasAttr(DialogHandler.attributeKey) ? (DialogHandler)record.attr(DialogHandler.attributeKey).get() : null;
         if (item == null) {
            PasswordService.processPasswordService(this.passwordService, entry);
            SettingsInterceptor.handleUser(entry, "§4[nLogin] §cUnexpected dialog state: unexpected response");
            return;
         }

         if (item.chainedPlayerContract == null) {
            PasswordService.processPasswordService(this.passwordService, entry);
            SettingsInterceptor.handleUser(entry, "§4[nLogin] §cUnexpected dialog state: missing callback");
            return;
         }

         target.setCancelled(true);
         PasswordService.resolvePasswordStore(this.passwordService).processLinkedSessionHandler(true).buildStrictCommandHandler(() -> {
            if (record.isActive()) {
               String valueValue = this.loadMessage(entry, source, response.getPayload(), null, record, false);
               if (valueValue != null) {
                  PasswordService.processPasswordService(this.passwordService, entry);
                  if (!valueValue.isEmpty()) {
                     SettingsInterceptor.handleUser(entry, valueValue);
                  } else {
                     PasswordService.processPasswordService(this.passwordService, entry);
                     entry.closeConnection();
                  }

                  item.chainedPlayerContract.resume(false);
               } else {
                  record.attr(DialogHandler.attributeKey).set(null);
                  item.chainedPlayerContract.resume(true);
                  PasswordService.processPasswordService(this.passwordService, entry);
               }
            }
         });
      }
   }

   public LowPasswordChallenge(PasswordService target) {
      this.passwordService = target;
   }
}
