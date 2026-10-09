package com.nickuc.login.session;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.account.StrictPremiumOption;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.auth.login.CachedLoginProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.discord.DiscordListener;
import com.nickuc.login.discord.LowDiscordBridge;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.NoticeKind;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.TightPlatformCatalog;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.nickuc.login.platform.packet.LoudPacketAdapter;
import com.nickuc.login.platform.listener.RootListenerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.ReadyBedrockResolver;
import com.nickuc.login.premium.SettingsLinker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.protocol.PasswordHashAdapter;
import com.nickuc.login.protocol.RootMessageHandler;
import com.nickuc.login.spawn.PrimaryMessageOption;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.floodgate.FloodgateTable;
import com.nickuc.login.storage.login.LoginCollection;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import com.nickuc.login.tasks.LoginMainQueueTask;
import com.nickuc.login.tasks.StartAsyncLoginTask;
import java.net.InetSocketAddress;
import java.util.UUID;


public class PacketCoordinator {
   private final PasswordStore passwordStore;
   private final SettingsLinker settingsLinker;

   public boolean isState(VerifiedServerAdapter target, LimboCoordinator input, SpawnLookup output, UUID context, boolean data, InetSocketAddress value) {
      if (!PrimaryMessageOption.PENDING_PRIMARYMESSAGEOPTION.ar()) {
         return false;
      }

      for (StrictMessageKind source : StrictMessageKind.values()) {
         if (source.hasState(this.passwordStore)) {
            if (source.computeMessage(output) != null) {
               RootListenerContract entry = source.processRootListenerContract(this.passwordStore);
               if (entry.findState()) {
                  if (!target.i("nlogin.bypass." + source.getName()) && !target.i("nlogin.force." + source.getName())) {
                     String record = output.findMessage();
                     if (record == null || record.equals(value.getAddress().getHostAddress())) {
                        return false;
                     }
                  }

                  if (source.validateState(output)) {
                     return false;
                  }
               }
            } else if (source.getState()) {
               if (!output.fetchStateAndState() || source.loadState()) {
                  return false;
               }

               if (!output.getState() || source.findState()) {
                  return false;
               }

               if (!target.i("nlogin.bypass." + source.getName())) {
                  return false;
               }
            }
         }
      }

      if (output.findStateForState()) {
         return false;
      }

      if (QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState() && output.fetchStateAndState()) {
         return data;
      }

      if (QuickPremiumOption.VERIFIED_QUICKPREMIUMOPTION.retrieveState() && input.d(LenientMessageKind.PRIMARY_LENIENTMESSAGEKIND)) {
         return true;
      }

      switch (this.passwordStore.findIndirectPasswordResolver().handleLoudPremiumOption(output, value.getAddress().getHostAddress())) {
         case CURRENT_LOUDPREMIUMOPTION:
            return true;
         case ACTIVE_LOUDPREMIUMOPTION:
            input.updateLenientMessageKind(LenientMessageKind.READY_LENIENTMESSAGEKIND, true);
            return false;
         default:
            return false;
      }
   }

   public void saveVerifiedServerAdapter(VerifiedServerAdapter target, boolean input) {
      if (target != null && target.loadState()) {
         if (!input) {
            this.passwordStore
               .processLinkedSessionHandler(true)
               .buildStrictCommandHandler(
                  new StartAsyncLoginTask(
                     () -> {
                        if (target.loadState()) {
                           LimboCoordinator inputValue = this.passwordStore.loadLimboRegistry().buildLimboCoordinator(target);
                           if (inputValue == null) {
                              PasswordHashContainer.performMessage("Login request failed for player " + target.getName() + ": invalid session state.");
                           } else if (!inputValue.loadTightPlatformCatalog().hasState(TightPlatformCatalog.ACTIVE_TIGHTPLATFORMCATALOG)) {
                              try {
                                 this.sendVerifiedServerAdapter(target);
                                 boolean output = this.passwordStore.findSettingsLinker().fetchActiveSessionRepository().isState(target, inputValue);
                                 inputValue.executeTightPlatformCatalog(
                                    output ? TightPlatformCatalog.PENDING_TIGHTPLATFORMCATALOG : TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG,
                                    TightPlatformCatalog.ACTIVE_TIGHTPLATFORMCATALOG
                                 );
                                 if (!output) {
                                    this.sendVerifiedServerAdapter(target, inputValue);
                                 }

                                 LoginMainQueueTask.dispatchVerifiedServerAdapter(target);
                              } catch (Throwable context) {
                                 PasswordHashContainer.handleMessage(
                                    "Severe error during the start of the asynchronous login process. (" + target.getName() + ")", context
                                 );
                                 target.buildCompletableFuture("§4[nLogin] Severe internal error detected. Please report to an admin.");
                              }
                           }
                        }
                     }
                  )
               );
         }
      }
   }

   public void dispatchVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, boolean output) {
      input.updateLenientMessageKind(LenientMessageKind.FAST_LENIENTMESSAGEKIND, true);
      CachedSettingsGateway.updateVerifiedServerAdapter(target, output ? SharedLoginOption.ACTIVE_SHAREDLOGINOPTION : SharedLoginOption.SHARED_LOGIN_OPTION);
      SecondaryAccountHandler context = input.getSecondaryAccountHandler();
      if (output) {
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, LoudProxyState.ACTIVE_LOUDPROXYSTATE, targetValue -> context.dispatchMessage(targetValue, "register "));
      } else {
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, LoudProxyState.LOUD_PROXY_STATE, targetValue -> context.dispatchMessage(targetValue, "login "));
      }
   }

   public void handleVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input) {
      try {
         synchronized (input.object) {
            if (input.loadTightPlatformCatalog().isState(TightPlatformCatalog.ACTIVE_TIGHTPLATFORMCATALOG)) {
               return;
            }

            input.executeTightPlatformCatalog(TightPlatformCatalog.ACTIVE_TIGHTPLATFORMCATALOG, TightPlatformCatalog.TIGHT_PLATFORM_CATALOG);
            if (this.passwordStore.verifyState(EventEnum.LOGIN_REQUEST, target)) {
               this.settingsLinker.saveVerifiedServerAdapter(target, input, input.loadSpawnLookup().findState());
            } else {
               input.executeTightPlatformCatalog(TightPlatformCatalog.TIGHT_PLATFORM_CATALOG, TightPlatformCatalog.ACTIVE_TIGHTPLATFORMCATALOG);
            }
         }
      } catch (Throwable value) {
         PasswordHashContainer.handleMessage("Severe error during the start of the login request. (" + target.getName() + ")", value);
         target.buildCompletableFuture("§4[nLogin] Severe internal error detected. Please report to an admin.");
      }
   }

   public void sendVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input) {
      input.loadObject(LenientMessageKind.OPEN_LENIENTMESSAGEKIND);
      input.updateLenientMessageKind(LenientMessageKind.SECURE_LENIENTMESSAGEKIND, 0);
      if (this.passwordStore.loadState()) {
         SecondarySenderAdapter output = this.passwordStore.findObject();
         PasswordHashAdapter context = output.getPasswordHashAdapter();
         context.handleVerifiedServerAdapter(target, 0, "stage", 0, "hash", Pbkdf2Linker.getMessage());
      }

      SpawnLookup entry = input.loadSpawnLookup();
      if (StrictPremiumOption.STRICT_PREMIUM_OPTION.retrieveState()
         && this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE
         && entry.fetchQuickDiscordHandler().getMessage() == null) {
         DiscordListener record = (DiscordListener)this.passwordStore.loadInternalAccountHandler();
         LowDiscordBridge data = record.fetchLowDiscordBridge();
         if (data != null) {
            String value = data.loadMessage(target.getUniqueId());
            if (value != null) {
               entry.fetchQuickDiscordHandler().dispatchMessage(value);
               this.passwordStore.findIndirectPasswordResolver().isState(entry, OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE);
            }
         }
      }

      boolean item = entry.findStateForState();
      if (!item) {
         if (QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState() && entry.fetchStateAndState()) {
            this.settingsLinker.executeSpawnLookup(entry, target, input);
            return;
         }

         if (QuickPremiumOption.VERIFIED_QUICKPREMIUMOPTION.retrieveState() && entry.getState() && input.d(LenientMessageKind.PRIMARY_LENIENTMESSAGEKIND)) {
            this.settingsLinker.processSpawnLookup(entry, target, input);
            return;
         }

         String element = target.resolveMessage();
         switch (this.passwordStore.findIndirectPasswordResolver().handleLoudPremiumOption(entry, element)) {
            case CURRENT_LOUDPREMIUMOPTION:
               this.settingsLinker.saveSpawnLookup(entry, target, input);
               return;
            case ACTIVE_LOUDPREMIUMOPTION:
               input.updateLenientMessageKind(LenientMessageKind.READY_LENIENTMESSAGEKIND, true);
         }
      }

      if (input.isState(LenientMessageKind.LIVE_LENIENTMESSAGEKIND)) {
         PasswordHashContainer.dispatchMessage("Forcing " + target.getName() + " login...");
         this.settingsLinker.sendSpawnLookup(entry, target, input, null, input.d(LenientMessageKind.LIVE_LENIENTMESSAGEKIND), false);
      } else {
         LoudPacketAdapter content = input.findLoudPacketAdapter();
         CachedLoginProcessor payload = content.resolveCachedLoginProcessor();
         if (payload != null) {
            if (item) {
               this.settingsLinker.processSpawnLookup(entry, target, input, payload.fetchMessage(), payload.getMessage(), true, false);
            } else {
               this.settingsLinker.sendSpawnLookup(entry, target, input, payload.fetchMessage(), true, false);
            }
         } else {
            byte result = 1;
            if (input.d(LenientMessageKind.PRIMARY_LENIENTMESSAGEKIND)) {
               SecondaryConnectionContract request = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
               if (request instanceof ReadyBedrockResolver) {
                  ReadyBedrockResolver response = (ReadyBedrockResolver)request;
                  FloodgateTable source = response.findFloodgateTable();
                  if (source != null) {
                     result = (byte)((item ? source.verifyState(target, input, entry) : source.validateState(target, input, entry)) ? 0 : 1);
                  }

                  input.updateLenientMessageKind(LenientMessageKind.OPEN_LENIENTMESSAGEKIND, SpawnState.ACTIVE_PENDING_SPAWNSTATE.r() * 2);
               }
            } else if (Pbkdf2Linker.retrieveNoticeKind() == NoticeKind.PENDING_NOTICEKIND) {
               RootMessageHandler holder = this.passwordStore.resolveRootMessageHandler();
               User subject = holder.processUser(target);
               if (subject.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_6)
                  && holder.handleClientVersion(subject).isNewerThanOrEquals(ClientVersion.V_1_21_6)) {
                  if (item) {
                     holder.fetchPasswordService().saveUser(subject, input.loadMessage(), null);
                  } else {
                     holder.fetchPasswordService().sendUser(subject, input.loadMessage(), MessageCoordinator.verifyState(this.passwordStore, entry), null);
                  }

                  result = 0;
               }
            }

            if (result != 0) {
               this.dispatchVerifiedServerAdapter(target, input, item);
            }

            if (content.resolveState()) {
               InternalLoginOption.REMOTE_INTERNALLOGINOPTION.performVerifiedServerAdapter(target, input);
            }

            LoginCollection reference = this.passwordStore.loadInternalAccountHandler().getLoginCollection();
            byte[] option = (byte[])input.d(LenientMessageKind.ACTIVE_SHARED_LENIENTMESSAGEKIND);
            if (reference != null && option != null) {
               int setting = !entry.resolveCachedPasswordHashHasher().hasState("addon.data") ? 1 : 0;
               reference.processVerifiedServerAdapter(target, option, (boolean)setting, !item);
            }
         }
      }
   }

   private void sendVerifiedServerAdapter(VerifiedServerAdapter target) {
      target.handleMessage("");
      target.executeTask();
   }

   public PacketCoordinator(PasswordStore target, SettingsLinker input) {
      this.passwordStore = target;
      this.settingsLinker = input;
   }
}
