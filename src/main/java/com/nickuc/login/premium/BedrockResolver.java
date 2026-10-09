package com.nickuc.login.premium;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SpawnOption;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.PremiumState;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.session.LocalSessionTable;
import com.nickuc.login.storage.locale.LocaleCollection;
import com.nickuc.login.storage.password.PasswordGateway;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BedrockResolver extends LocaleCollection {
   @Override
   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()) {
         if (!QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
            if (!(target instanceof VerifiedServerAdapter)) {
               if (output.length < 1) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(
                     target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/" + input.toLowerCase(Locale.ENGLISH) + " <player> [options]"
                  );
               } else {
                  LiveLoginCheckpoint item = new LiveLoginCheckpoint();
                  String element = output[0];
                  SpawnLookup holder = this.indirectSessionHandler.findIndirectPasswordResolver().loadSpawnLookup(target, super.internalLoginOption, output, element);
                  if (holder != null) {
                     if (!holder.fetchState()) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                     } else if (holder.loadSpawnOption() == SpawnOption.PENDING_SPAWNOPTION) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§c✖ Bedrock accounts cannot be modified by this command.");
                     } else if (!holder.fetchStateAndState()) {
                        holder.processUniqueId(null);
                        holder.dispatchTask();
                        holder.performTask();
                        if (!this.indirectSessionHandler
                           .findIndirectPasswordResolver()
                           .isState(holder, OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE)) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        } else {
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§a✔ §7The desired account has been marked as §6premium§7.");
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                           CachedSettingsGateway.handleOutgoingSenderAdapter(
                              target, "§e⚑ This operation took §f" + item.loadMessage(TimeUnit.SECONDS, 2) + "s§e."
                           );
                        }
                     } else {
                        SpawnLookup subject = this.indirectSessionHandler.findIndirectPasswordResolver().computeSpawnLookup(element, null, null, false);
                        if (subject == null) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        } else {
                           if (subject.resolveStateForState() && !Objects.equals(holder.loadLong(), subject.loadLong())) {
                              if (CachedSettingsGateway.loadState()) {
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(
                                    target,
                                    "§cUma conta offline já está usando o nickname \"" + subject.retrieveMessage() + "\", preferindo a conta original atual."
                                 );
                              } else {
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(
                                    target,
                                    "§cAn offline account is already using the nickname \""
                                       + subject.retrieveMessage()
                                       + "\", preferring the current premium account."
                                 );
                              }

                              CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                              if (!this.indirectSessionHandler.findIndirectPasswordResolver().verifyState(subject)) {
                                 CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                                 return;
                              }
                           }

                           UUID option = holder.getMojangId();
                           UUID setting = holder.getUniqueId();
                           if (option == null || !option.equals(setting) || output.length != 1 && (output.length != 2 || !output[1].equals("--ignore-warnings"))) {
                              holder.processUniqueId(null);
                              holder.dispatchTask();
                              if (!this.indirectSessionHandler
                                 .findIndirectPasswordResolver()
                                 .isState(holder, OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE)) {
                                 CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                              } else {
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§a✔ §7The desired account has been marked as §6offline§7.");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(
                                    target, "§e⚑ This operation took §f" + item.loadMessage(TimeUnit.SECONDS, 2) + "s§e."
                                 );
                              }
                           } else {
                              String source = input + " " + holder.loadMessage(output[0]) + " --ignore-warnings";
                              CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                              if (CachedSettingsGateway.loadState()) {
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§6Este jogador está usando um UUID da Mojang.");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§c‼ §eIsso significa que uma conta offline poderá acessar todo");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §eo progresso do dono da conta original §f\"" + setting + "\"§e.");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(
                                    target, "§c‼ §eNão é recomendado desativar o modo original, a menos que saiba exatamente o que está fazendo."
                                 );
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §ePara ignorar este aviso, rode: §f" + source);
                              } else {
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§6This player is using a Mojang UUID.");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§c‼ §eThis means that an offline account will be able to access all");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §ethe progress of the premium account owner §f\"" + setting + "\"§e.");
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(
                                    target, "§c‼ §eIt is not recommended to disable premium mode unless you know exactly what you are doing."
                                 );
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §eTo ignore this warning, run: §f" + source);
                              }

                              CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                           }
                        }
                     }
                  }
               }
            } else {
               VerifiedServerAdapter context = (VerifiedServerAdapter)target;
               LimboRegistry data = this.indirectSessionHandler.loadLimboRegistry();
               if (!data.canState(context)) {
                  LimboCoordinator payload = data.loadLimboCoordinator(context);
                  LocalSessionTable reference = (LocalSessionTable)payload.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
                  if (reference != null && reference.fetchCachedProxyCatalog() == CachedProxyCatalog.ACTIVE_CACHEDPROXYCATALOG) {
                     InternalLoginOption.INTERNAL_LOGIN_OPTION
                        .performVerifiedServerAdapter(
                           context,
                           payload,
                           "click",
                           "notification",
                           Integer.toString(CachedProxyCatalog.ACTIVE_CACHEDPROXYCATALOG.findCount()),
                           Integer.toString(PrivateLoginOption.ACTIVE_PRIVATELOGINOPTION.findCount())
                        );
                  }
               } else if (output.length != 1) {
                  String content = String.format("/%s <%s>", input, CachedSettingsGateway.computeMessage(LoudProxyState.OPEN_LOUDPROXYSTATE, context));
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.LINKED_LOUDPROXYSTATE, content);
               } else {
                  LimboCoordinator value = data.loadLimboCoordinator(context);
                  SpawnLookup result = value.loadSpawnLookup();
                  synchronized (result.object) {
                     if (!result.fetchState()) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                        CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                     } else if (result.fetchStateAndState()) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ACTIVE_PRIMARY_LOUDPROXYSTATE);
                        CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                     } else {
                        String response = output[0];
                        if (!this.indirectSessionHandler.findIndirectPasswordResolver().checkState(result, response)) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_AUTHENTICATED_LOUDPROXYSTATE);
                           CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                        } else {
                           PasswordGateway.processPasswordStore(
                              this.indirectSessionHandler, value.loadMessage(), context.retrieveInetSocketAddress().getAddress(), PremiumState.PENDING_PREMIUMSTATE
                           );
                           context.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.UPSTREAM_LOUDPROXYSTATE, context));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public BedrockResolver(InternalLoginOption target) {
      super(target);
      this.getPasswordHashCommand();
   }
}
