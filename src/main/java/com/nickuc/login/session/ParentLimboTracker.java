package com.nickuc.login.session;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.api.enums.LoginType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.auth.login.PrivateLoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LocalPremiumState;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SettingsLinker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.spawn.PrimaryMessageOption;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginCollection;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import com.nickuc.login.tasks.CmdAfterAuthTask;
import com.nickuc.login.tasks.LoginMainQueueTask;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ParentLimboTracker {
   private final SettingsLinker settingsLinker;
   private final PasswordStore passwordStore;

   private void dispatchVerifiedServerAdapter(VerifiedServerAdapter target, boolean input) {
      List output = input ? SpawnState.PENDING_PRIVATE_SPAWNSTATE.a(new Object[0]) : SpawnState.PENDING_INTERNAL_SPAWNSTATE.a(new Object[0]);
      if (!output.isEmpty()) {
         Runnable context = () -> output.forEach(
            inputValue -> {
               inputValue = inputValue.trim();
               if (!inputValue.isEmpty()) {
                  String outputValue = target.getName();
                  Object[] contextValue = new Object[]{false, true, 0, null};
                  String[] data = inputValue.split(" ");
                  StringBuilder value = new StringBuilder();

                  for (int result = 0; result < data.length; result++) {
                     String request = data[result];
                     if (!request.isEmpty()) {
                        if (request.charAt(0) == '@') {
                           switch (request.toLowerCase(Locale.ENGLISH)) {
                              case "@console":
                                 contextValue[0] = true;
                                 continue;
                              case "@proxy":
                                 contextValue[1] = false;
                                 continue;
                              case "@delay":
                                 if (result + 1 < data.length) {
                                    contextValue[2] = PrivateLoginCheckpoint.handleInteger(data[++result], 0);
                                    continue;
                                 }
                                 break;
                              case "@server":
                                 if (result + 1 < data.length) {
                                    String entry = data[++result];
                                    if (entry != null) {
                                       contextValue[3] = entry.contains(",") ? entry.split(",") : new String[]{entry};
                                    }
                                    continue;
                                 }
                           }
                        }

                        if (value.length() > 0) {
                           value.append(" ");
                        }

                        value.append(request);
                     }
                  }

                  String element = value.toString().replace("@player", outputValue).replace("@address", target.resolveMessage());
                  if (!element.isEmpty()) {
                     boolean content = (Boolean)contextValue[0];
                     int payload = this.passwordStore.loadState() && contextValue[1] ? 1 : 0;
                     int holder = Math.max((Integer)contextValue[2], 0) * 50;
                     String[] reference = (String[])contextValue[3];
                     Runnable record = () -> {
                        if (payload) {
                           SecondarySenderAdapter valueValue = this.passwordStore.findObject();
                           if (reference != null) {
                              String resultValue = valueValue.handleMessage(target);
                              if (Arrays.stream(reference).noneMatch(targetValue -> targetValue.equals(resultValue))) {
                                 return;
                              }
                           }

                           valueValue.getPasswordHashAdapter().updateVerifiedServerAdapter(target, 4, "action", 1, "command", element, "isConsole", content);
                        } else if (content) {
                           this.passwordStore.b().findAuthenticatedServerAdapter().performMessage(element);
                        } else {
                           if (this.passwordStore.loadState() && reference != null) {
                              SecondarySenderAdapter requestValue = this.passwordStore.findObject();
                              String response = requestValue.handleMessage(target);
                              if (Arrays.stream(reference).noneMatch(targetValue -> targetValue.equals(response))) {
                                 return;
                              }
                           }

                           if (element.charAt(0) == '/'
                              && this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() != SilentProxyState.SILENT_PROXY_STATE) {
                              target.performMessage(element);
                           } else {
                              target.processMessageForValue(element);
                           }
                        }
                     };
                     if (holder == 0) {
                        record.run();
                     } else {
                        this.passwordStore
                           .processLinkedSessionHandler(false)
                           .loadStrictCommandHandler(new CmdAfterAuthTask(record), holder, TimeUnit.MILLISECONDS);
                     }
                  }
               }
            }
         );
         this.passwordStore.processLinkedSessionHandler(false).buildStrictCommandHandler(new CmdAfterAuthTask(context));
      }
   }

   public void saveSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output, LoginType context, String data) {
      try {
         if (!input.loadState()) {
            return;
         }

         output.executeTightPlatformCatalog(TightPlatformCatalog.LOCAL_TIGHTPLATFORMCATALOG, TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG);
         LoginMainQueueTask.saveVerifiedServerAdapter(input);
         boolean value = target.loadState();
         if (context != LoginType.REGISTER) {
            this.passwordStore.findIndirectPasswordResolver().saveSpawnLookup(target, input.resolveMessage(), data);
         }

         if (this.passwordStore.loadState()) {
            SecondarySenderAdapter result = this.passwordStore.findObject();
            result.getPasswordHashAdapter()
               .updateVerifiedServerAdapter(
                  input,
                  2,
                  "type",
                  context.ordinal(),
                  "sessionFromProxy",
                  false,
                  "restoreLimbo",
                  !output.isState(LenientMessageKind.ACTIVE_VERIFIED_LENIENTMESSAGEKIND)
                     && !PrimaryMessageOption.LOCAL_PRIMARYMESSAGEOPTION.ar()
                     && !PrimaryMessageOption.CACHED_PRIMARYMESSAGEOPTION.ar()
               );
         }

         LoginCollection record = this.passwordStore.loadInternalAccountHandler().getLoginCollection();
         if (record != null) {
            record.executeVerifiedServerAdapter(input, LocalPremiumState.LOCAL_PREMIUM_STATE);
         }

         this.dispatchVerifiedServerAdapter(input, context == LoginType.REGISTER || value);
         this.passwordStore.verifyState(EventEnum.AUTHENTICATE_EVENT, input);
         byte request = 1;
         if (this.passwordStore.loadState()
            && (
               output.isState(LenientMessageKind.ACTIVE_VERIFIED_LENIENTMESSAGEKIND)
                  || PrimaryMessageOption.LOCAL_PRIMARYMESSAGEOPTION.ar()
                  || PrimaryMessageOption.CACHED_PRIMARYMESSAGEOPTION.ar()
            )) {
            SecondarySenderAdapter response = this.passwordStore.findObject();
            int source = Math.max(PrimaryMessageOption.PRIMARY_PRIMARYMESSAGEOPTION.r(), 0);
            if (source > 0) {
               request = 0;
               this.passwordStore.processLinkedSessionHandler(true).loadStrictCommandHandler(() -> {
                  if (input.loadState()) {
                     if (response.buildServerConnectType(input, output) == null) {
                        this.executeVerifiedServerAdapter(input, output, true);
                     }
                  }
               }, source, TimeUnit.MILLISECONDS);
            } else if (response.buildServerConnectType(input, output) != null) {
               request = 0;
            }
         }

         if (request != 0) {
            this.executeVerifiedServerAdapter(input, output, true);
         }
      } catch (Throwable entry) {
         PasswordHashContainer.handleMessage("Severe error during authentication completion. (" + input.getName() + ")", entry);
         input.buildCompletableFuture("§4[nLogin] Severe internal error detected. Please report to an admin.");
      }
   }

   public void executeVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, boolean output) {
      if (output) {
         this.settingsLinker.executeVerifiedServerAdapter(target, input);
      }

      LoudPlayerContract context = input.loadObject(LenientMessageKind.ACTIVE_LOCAL_LENIENTMESSAGEKIND);
      if (context != null) {
         context.sendPasswordStore(this.passwordStore, target, input);
      }

      String data = input.loadObject(LenientMessageKind.ACTIVE_MAIN_LENIENTMESSAGEKIND);
      if (data != null) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_DIRECT_LOUDPROXYSTATE, data);
      }
   }

   public ParentLimboTracker(PasswordStore target, SettingsLinker input) {
      this.passwordStore = target;
      this.settingsLinker = input;
   }
}
