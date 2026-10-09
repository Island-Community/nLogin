package com.nickuc.login.tasks;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.PrimaryLoginService;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.command.StrictCommandHandler;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.spawn.PremiumOption;
import com.nickuc.login.spawn.PrimaryMessageOption;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.session.LocalSessionTable;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LoginMainQueueTask implements Runnable {
   private static final Map<VerifiedServerAdapter, LoginQueueCycle> sessions = new ConcurrentHashMap<>();
   private static final Set<VerifiedServerAdapter> players = ConcurrentHashMap.newKeySet();
   private static StrictCommandHandler strictCommandHandler;
   private final PasswordStore passwordStore;

   public static int retrieveCount() {
      return players.size();
   }

   public void updateTask() {
      if (this.passwordStore.resolveState()) {
         if (!players.isEmpty()) {
            long target = System.nanoTime();
            LimboRegistry output = this.passwordStore.loadLimboRegistry();

            for (VerifiedServerAdapter data : players) {
               try {
                  LoginQueueCycle value = sessions.computeIfAbsent(data, instance -> new LoginQueueCycle());
                  if (!value.findState()) {
                     return;
                  }

                  if (!data.loadState()) {
                     saveVerifiedServerAdapter(data);
                     return;
                  }

                  LimboCoordinator result = output.buildLimboCoordinator(data);
                  if (result == null) {
                     saveVerifiedServerAdapter(data);
                     return;
                  }

                  synchronized (result.object) {
                     TightPlatformCatalog response = result.loadTightPlatformCatalog();
                     if (response.isState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)) {
                        LocalSessionTable source = (LocalSessionTable)result.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
                        if (response != TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG
                           || source == null
                           || !(source.getLoudPlayerContract() instanceof UpstreamAccountHandler)
                           || !((UpstreamAccountHandler)source.getLoudPlayerContract()).at()) {
                           saveVerifiedServerAdapter(data);
                           return;
                        }
                     }

                     result.saveTask();
                     SpawnLookup option = result.loadSpawnLookup();
                     boolean entry = option.findStateForState();
                     if (response.hasState(TightPlatformCatalog.PENDING_TIGHTPLATFORMCATALOG)) {
                        int record = result.a(LenientMessageKind.SECURE_LENIENTMESSAGEKIND);
                        if (this.passwordStore.loadState()
                           && !option.getState()
                           && record == 5
                           && PrimaryMessageOption.ACTIVE_PRIMARYMESSAGEOPTION.ar()
                           && !result.isState(LenientMessageKind.ACTIVE_REMOTE_LENIENTMESSAGEKIND)) {
                           SecondarySenderAdapter item = this.passwordStore.findObject();
                           String element = item.handleMessage(data);
                           if (item.isState(result, element)) {
                              result.getSecondaryAccountHandler()
                                 .executeMessage(
                                    result.loadState()
                                       ? "\n§4[nLogin] Erro de configuração detectado no servidor proxy:\n\n§aSe este for um servidor de autenticação, certifique-se de instalar o nLogin no servidor §f\""
                                          + element
                                          + "\"§a.\n§cSe este NÃO for um servidor de autenticação, remova o servidor §f\""
                                          + element
                                          + "\" §cda opção §f\""
                                          + PrimaryMessageOption.PRIMARY_MESSAGE_OPTION.retrieveBusyLoginProcessor().fetchNames()[0]
                                          + "\"§c.\n"
                                       : "\n§4[nLogin] Error detected on proxy server setup:\n\n§aIf this is an authentication server, make sure you have installed nLogin on the §f\""
                                          + element
                                          + "\" §aserver.\n§cIf this is NOT an authentication server, remove the §f\""
                                          + element
                                          + "\" §cserver from the §f\""
                                          + PrimaryMessageOption.PRIMARY_MESSAGE_OPTION.retrieveBusyLoginProcessor().fetchNames()[0]
                                          + "\" §coption.\n"
                                 );
                           }
                        }

                        if (response.canState(TightPlatformCatalog.PRIMARY_TIGHTPLATFORMCATALOG)) {
                           if (Boolean.TRUE.equals(result.loadObject(LenientMessageKind.ROOT_LENIENTMESSAGEKIND))) {
                              this.passwordStore.findSettingsLinker().retrievePacketCoordinator().dispatchVerifiedServerAdapter(data, result, entry);
                           }

                           if (Boolean.TRUE.equals(result.loadObject(LenientMessageKind.TOP_LENIENTMESSAGEKIND))) {
                              InternalLoginOption.REMOTE_INTERNALLOGINOPTION.performVerifiedServerAdapter(data, result);
                           }
                        }
                     }

                     Integer setting = result.findInteger();
                     if (setting != null) {
                        if (setting <= 0) {
                           data.buildCompletableFuture(
                              CachedSettingsGateway.computeMessage(
                                 entry ? LoudProxyState.ACTIVE_SECURE_LOUDPROXYSTATE : LoudProxyState.ACTIVE_SAFE_LOUDPROXYSTATE, data
                              )
                           );
                           saveVerifiedServerAdapter(data);
                           return;
                        }

                        if (SpawnState.PENDING_REMOTE_SPAWNSTATE.ar()
                           && SpawnState.PENDING_LOCAL_SPAWNSTATE.ar()
                           && result.d(LenientMessageKind.FAST_LENIENTMESSAGEKIND)) {
                           int property = setting != 1 ? 1 : 0;
                           String attribute = CachedSettingsGateway.computeMessage(
                              property != 0 ? LoudProxyState.CURRENT_LOUDPROXYSTATE : LoudProxyState.PENDING_LOUDPROXYSTATE, data, setting
                           );
                           String content = CachedSettingsGateway.computeMessage(
                              property != 0 ? LoudProxyState.MAIN_LOUDPROXYSTATE : LoudProxyState.PRIMARY_LOUDPROXYSTATE, data
                           );
                           if (!content.isEmpty()) {
                              String payload = content.split("///")[entry ? 1 : 0];
                              attribute = attribute.replace("{" + content + "}", payload);
                           }

                           data.handleMessage(attribute);
                        }
                     }
                  }
               } catch (Exception subject) {
                  PasswordHashContainer.handleMessage("Unable to handle queue cycle for " + data.getName(), subject);
               }
            }

            PrimaryLoginService.savePremiumOption(PremiumOption.PREMIUM_OPTION, target);
         }
      }
   }

   public LoginMainQueueTask(PasswordStore target) {
      this.passwordStore = target;
   }

   public static boolean verifyState(VerifiedServerAdapter instance) {
      return players.contains(instance);
   }

   public static void saveVerifiedServerAdapter(VerifiedServerAdapter instance) {
      players.remove(instance);
      sessions.remove(instance);
   }

   public static void dispatchPasswordStore(PasswordStore instance) {
      if (strictCommandHandler == null) {
         strictCommandHandler = instance.processLinkedSessionHandler(true)
            .processStrictCommandHandler(new LoginMainQueueTask(instance), 250L, 250L, TimeUnit.MILLISECONDS);
      }
   }

   public static void dispatchVerifiedServerAdapter(VerifiedServerAdapter instance) {
      if (!players.add(instance)) {
         throw new IllegalStateException("Already added in main login queue (race condition?)");
      }
   }
}
