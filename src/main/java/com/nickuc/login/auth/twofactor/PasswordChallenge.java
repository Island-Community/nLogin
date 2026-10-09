package com.nickuc.login.auth.twofactor;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.locale.LocaleCheckpoint;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.discord.QuickDiscordHandler;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.SecondaryMessageKind;
import com.nickuc.login.model.SecondaryPlatformCatalog;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.listener.RootListenerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.premium.UpdateLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.session.LocalSessionTable;
import com.nickuc.login.storage.locale.LocaleCollection;
import com.nickuc.login.storage.session.SessionTable;
import com.nickuc.login.storage.session.SharedSessionTable;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class PasswordChallenge extends LocaleCollection {
   private static final List<String> entries = Arrays.asList("add", "remove", "recover", "2fa");
   public final StrictMessageKind strictMessageKind;

   public void updateVerifiedServerAdapter(VerifiedServerAdapter target, SpawnLookup input, String output, String[] context) {
      String data = this.strictMessageKind.computeMessage(input);
      if (data == null) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_PRIMARY_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
      } else {
         QuickDiscordHandler value = input.fetchQuickDiscordHandler();
         switch (this.strictMessageKind) {
            case STRICT_MESSAGE_KIND:
               value.dispatchMessage(null);
               this.indirectSessionHandler.findIndirectPasswordResolver().isState(input, OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE);
               break;
            case ACTIVE_STRICTMESSAGEKIND:
               value.saveMessage(null);
               this.indirectSessionHandler.findIndirectPasswordResolver().isState(input, OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE);
               break;
            default:
               throw new IllegalStateException("Unexpected value: " + this.strictMessageKind);
         }

         this.indirectSessionHandler.verifyState(EventEnum.TWO_FACTOR_REMOVE, TwoFactorType.convert(this.strictMessageKind), target, data);
         if (this.strictMessageKind.getState()) {
            target.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.PRIVATE_LOUDPROXYSTATE, target));
         } else {
            CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_REMOTE_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
         }
      }
   }

   @Override
   public List<String> buildCollection(OutgoingSenderAdapter target, String input, String[] output) {
      return output.length <= 1 ? LocaleCheckpoint.handleCollection(entries, output) : null;
   }

   public void performVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, SpawnLookup output, String context) {
      String data = context.toLowerCase(Locale.ENGLISH);
      SecondaryAccountHandler value = input.getSecondaryAccountHandler();
      String result = this.strictMessageKind.findMessage().toLowerCase(Locale.ENGLISH);
      String request = OpenLocaleBarrier.buildMessage(result);

      for (String entry : CachedSettingsGateway.computeCollection(LoudProxyState.PENDING_PRIVATE_LOUDPROXYSTATE, target, data, result, request)) {
         if (entry.length() > 3 && entry.contains("[") && entry.contains("]")) {
            String[] record = entry.split("\\[");
            int item = entry.split("]").length;
            if ((record.length == 1 || record.length == 2) && (item == 1 || item == 2)) {
               String element = record.length == 1 ? "" : record[0];
               if (!element.contains("]")) {
                  String[] content = record[1].split("]");
                  String payload = content[0];
                  if (payload.contains("//")) {
                     String[] holder = payload.split("//");
                     if (holder.length == 2) {
                        int setting = this.strictMessageKind.computeMessage(output) != null ? 0 : 1;
                        String subject = element + holder[setting];
                        if (content.length == 2) {
                           subject = subject + content[1];
                        }

                        value.executeMessage(subject);
                        continue;
                     }
                  }

                  String option = element + payload;
                  if (content.length == 2) {
                     option = option + content[1];
                  }

                  String reference;
                  if (option.contains(data + " add")) {
                     reference = "/" + data + " add ";
                  } else if (option.contains(data + " remove")) {
                     reference = "/" + data + " remove ";
                  } else {
                     if (!option.contains(data + " 2fa")) {
                        value.executeMessage(option);
                        continue;
                     }

                     reference = "/" + data + " 2fa";
                  }

                  value.executeMessage(option, null, reference);
                  continue;
               }
            }
         }

         value.executeMessage(entry);
      }
   }

   private void dispatchRootListenerContract(
      RootListenerContract target, VerifiedServerAdapter input, SpawnLookup output, LimboCoordinator context, String data, String[] value
   ) {
      String result = this.strictMessageKind.computeMessage(output);
      if (result != null) {
         if (value.length != 1) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.LINKED_LOUDPROXYSTATE, "/" + data.toLowerCase(Locale.ENGLISH) + " <code>");
         } else {
            String request = value[0];
            if (!this.strictMessageKind.hasState(output, null, request)) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.PENDING_CACHED_LOUDPROXYSTATE);
               if (SpawnState.ACTIVE_TOP_SPAWNSTATE.r() > 0 && context.a(LenientMessageKind.DIRECT_LENIENTMESSAGEKIND) < SpawnState.ACTIVE_TOP_SPAWNSTATE.r()) {
                  context.updateLenientMessageKind(LenientMessageKind.DIRECT_LENIENTMESSAGEKIND, context.a(LenientMessageKind.DIRECT_LENIENTMESSAGEKIND) + 1);
               } else {
                  context.updateLenientMessageKind(LenientMessageKind.DIRECT_LENIENTMESSAGEKIND, 0);
                  input.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_SHARED_LOUDPROXYSTATE, input));
               }
            } else {
               SecondaryPlatformCatalog response = this.strictMessageKind.createSecondaryPlatformCatalog(output);
               if (response != null) {
                  switch (response) {
                     case ACTIVE_SECONDARYPLATFORMCATALOG:
                        if (this.strictMessageKind.retrieveState()) {
                           output.performTaskForValue();
                        }

                        String record = SecureLoginHandler.loadMessage(SecondaryMessageKind.CURRENT_SECONDARYMESSAGEKIND, 6);
                        if (!this.indirectSessionHandler.findIndirectPasswordResolver().validateState(output, record)) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                           return;
                        }

                        PasswordHashContainer.dispatchMessage(
                           "The password of " + output.retrieveMessage() + " player was changed via " + this.strictMessageKind.findMessage() + " 2FA recovery"
                        );
                        context.updateLenientMessageKind(LenientMessageKind.ACTIVE_MAIN_LENIENTMESSAGEKIND, record);
                        target.updateSpawnLookup(output, input, record);
                        this.indirectSessionHandler.findSettingsLinker().saveSpawnLookup(output, input, true, false);
                        break;
                     case PENDING_SECONDARYPLATFORMCATALOG:
                        LocalSessionTable source = (LocalSessionTable)context.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
                        if (source != null) {
                           LoudPlayerContract entry = source.getLoudPlayerContract();
                           if (entry instanceof SharedSessionTable && ((SharedSessionTable)entry).getStrictMessageKind() == this.strictMessageKind) {
                              entry.handlePasswordStore(this.indirectSessionHandler, input, context);
                           }
                        }
                  }

                  this.strictMessageKind.updateSpawnLookup(output, null, null);
                  this.indirectSessionHandler.verifyState(EventEnum.TWO_FACTOR_AUTH, TwoFactorType.convert(this.strictMessageKind), input, result);
               }
            }
         }
      } else {
         this.performVerifiedServerAdapter(input, context, output, data);
      }
   }

   public PasswordChallenge(InternalLoginOption target, StrictMessageKind input) {
      super(target);
      this.getPasswordHashCommand();
      this.strictMessageKind = input;
   }

   private void saveVerifiedServerAdapter(VerifiedServerAdapter target, SpawnLookup input, String output, String[] context) {
      String data = this.strictMessageKind.computeMessage(input);
      if (data == null) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_PRIMARY_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
      } else {
         int value = !this.strictMessageKind.validateState(input) ? 1 : 0;
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, value != 0 ? LoudProxyState.PENDING_UPSTREAM_LOUDPROXYSTATE : LoudProxyState.PENDING_INCOMING_LOUDPROXYSTATE
         );
         QuickDiscordHandler result = input.fetchQuickDiscordHandler();
         switch (this.strictMessageKind) {
            case STRICT_MESSAGE_KIND:
               result.executeState((boolean)value);
               this.indirectSessionHandler.findIndirectPasswordResolver().isState(input, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
               break;
            case ACTIVE_STRICTMESSAGEKIND:
               result.processState((boolean)value);
               this.indirectSessionHandler.findIndirectPasswordResolver().isState(input, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
               break;
            default:
               throw new IllegalStateException("Unexpected value: " + this.strictMessageKind);
         }
      }
   }

   @Override
   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      UpdateLookup context = this.indirectSessionHandler.a();
      if (context.getCount() == 9) {
         RootListenerContract data = this.strictMessageKind.processRootListenerContract(this.indirectSessionHandler);
         if (!data.findState()) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target,
               CachedSettingsGateway.loadState()
                  ? "§cA autenticação com o "
                     + this.strictMessageKind.findMessage()
                     + " não foi possível. Verifique as logs de inicialização para mais detalhes."
                  : "§cThe authentication with " + this.strictMessageKind.findMessage() + " was not possible. Check the startup logs for more details."
            );
            CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
         } else if (!(target instanceof VerifiedServerAdapter)) {
            if (!target.hasState("nlogin.admin")) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ROOT_LOUDPROXYSTATE);
            } else if (output.length == 2 && output[0].equalsIgnoreCase("remove")) {
               LiveLoginCheckpoint reference = new LiveLoginCheckpoint();
               IndirectPasswordResolver subject = this.indirectSessionHandler.findIndirectPasswordResolver();
               String setting = output[1];
               VerifiedServerAdapter property = this.indirectSessionHandler.b().resolveVerifiedServerAdapter(setting);
               SpawnLookup option;
               if (property != null) {
                  option = this.indirectSessionHandler.loadLimboRegistry().loadLimboCoordinator(property).loadSpawnLookup();
               } else {
                  option = subject.loadSpawnLookup(target, super.internalLoginOption, output, setting);
                  if (option == null) {
                     CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                     return;
                  }
               }

               if (!option.resolveStateForState()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
               } else if (this.strictMessageKind.computeMessage(option) == null) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_PRIMARY_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
               } else {
                  switch (this.strictMessageKind) {
                     case STRICT_MESSAGE_KIND:
                        option.fetchQuickDiscordHandler().dispatchMessage(null);
                        option.fetchQuickDiscordHandler().executeState(false);
                        subject.isState(option, OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE);
                        break;
                     case ACTIVE_STRICTMESSAGEKIND:
                        option.fetchQuickDiscordHandler().saveMessage(null);
                        option.fetchQuickDiscordHandler().processState(false);
                        subject.isState(option, OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE);
                        break;
                     default:
                        throw new IllegalStateException("Unexpected value: " + this.strictMessageKind);
                  }

                  if (property != null) {
                     property.buildCompletableFuture(
                        CachedSettingsGateway.computeMessage(LoudProxyState.PENDING_REMOTE_LOUDPROXYSTATE, property, this.strictMessageKind.findMessage())
                     );
                  }

                  CachedSettingsGateway.handleOutgoingSenderAdapter(
                     target,
                     CachedSettingsGateway.loadState()
                        ? "§e⚑ Esta operação foi processada em §f" + reference.loadMessage(TimeUnit.MILLISECONDS, 2) + "ms§e."
                        : "§e⚑ This operation took §f" + reference.loadMessage(TimeUnit.SECONDS, 2) + "s§e."
                  );
               }
            } else {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/" + input + " remove <player>");
            }
         } else {
            VerifiedServerAdapter value = (VerifiedServerAdapter)target;
            String result = this.strictMessageKind.resolveMessage();
            if (!this.strictMessageKind.getState() && result != null && !value.i(result)) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(value, LoudProxyState.ROOT_LOUDPROXYSTATE);
               CachedSettingsGateway.processOutgoingSenderAdapter(value, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
            } else {
               LimboRegistry request = this.indirectSessionHandler.loadLimboRegistry();
               LimboCoordinator response = request.loadLimboCoordinator(value);
               SpawnLookup source = response.loadSpawnLookup();
               if (!source.fetchState()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(value, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(value, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else if (output.length != 1 && output.length != 2) {
                  this.performVerifiedServerAdapter(value, response, source, input);
               } else {
                  String entry = output[0];
                  LocalSessionTable record = (LocalSessionTable)response.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
                  LoudPlayerContract item = record != null ? record.getLoudPlayerContract() : null;
                  int element = item instanceof SessionTable && ((SessionTable)item).getStrictMessageKind() == this.strictMessageKind ? 1 : 0;
                  boolean content = request.canState(value);
                  switch (entry.toLowerCase(Locale.ENGLISH)) {
                     case "add":
                        if (content || element != 0) {
                           this.updateVerifiedServerAdapter(value, source, response, data, input, output);
                        }
                        break;
                     case "remove":
                        if (content) {
                           this.updateVerifiedServerAdapter(value, source, input, output);
                        }
                        break;
                     case "recover":
                        if (content) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(value, LoudProxyState.LIVE_LOUDPROXYSTATE);
                           CachedSettingsGateway.updateVerifiedServerAdapter(value, SharedLoginOption.MAIN_SHAREDLOGINOPTION);
                           CachedSettingsGateway.processOutgoingSenderAdapter(value, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                        } else {
                           this.updateVerifiedServerAdapter(value, source, data, input, output);
                        }
                        break;
                     case "2fa":
                        if (content) {
                           this.saveVerifiedServerAdapter(value, source, input, output);
                        }
                        break;
                     default:
                        if (!content && element == 0) {
                           this.dispatchRootListenerContract(data, value, source, response, input, output);
                        } else if (this.strictMessageKind.canState(source, SecondaryPlatformCatalog.SECONDARY_PLATFORM_CATALOG)) {
                           this.executeVerifiedServerAdapter(value, source, response, input, output);
                        } else {
                           this.performVerifiedServerAdapter(value, response, source, input);
                        }
                  }
               }
            }
         }
      }
   }

   public abstract void updateVerifiedServerAdapter(VerifiedServerAdapter target, SpawnLookup input, RootListenerContract output, String context, String[] data);

   public void executeVerifiedServerAdapter(VerifiedServerAdapter target, SpawnLookup input, LimboCoordinator output, String context, String[] data) {
      String value = this.strictMessageKind.createMessage(input);
      if (value == null) {
         this.performVerifiedServerAdapter(target, output, input, context);
      } else {
         String result = data[0];
         if (!this.strictMessageKind.hasState(input, SecondaryPlatformCatalog.SECONDARY_PLATFORM_CATALOG, result)) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_CACHED_LOUDPROXYSTATE);
         } else {
            QuickDiscordHandler request = input.fetchQuickDiscordHandler();
            int response = this.strictMessageKind.fetchState() && !input.fetchStateAndState() && !input.getState() ? 1 : 0;
            switch (this.strictMessageKind) {
               case STRICT_MESSAGE_KIND:
                  request.executeState((boolean)response);
                  request.dispatchMessage(value);
                  this.indirectSessionHandler
                     .findIndirectPasswordResolver()
                     .isState(input, OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
                  break;
               case ACTIVE_STRICTMESSAGEKIND:
                  request.processState((boolean)response);
                  request.saveMessage(value);
                  this.indirectSessionHandler
                     .findIndirectPasswordResolver()
                     .isState(input, OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
                  break;
               default:
                  throw new IllegalStateException("Unexpected value: " + this.strictMessageKind);
            }

            this.indirectSessionHandler
               .verifyState(EventEnum.TWO_FACTOR_ADD, TwoFactorType.convert(this.strictMessageKind), target, target.getUniqueId(), target.getName(), value);
            this.strictMessageKind.updateSpawnLookup(input, null);
            this.strictMessageKind.updateSpawnLookup(input, null, null);
            CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_VERIFIED_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
            LocalSessionTable source = (LocalSessionTable)output.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
            if (source != null) {
               LoudPlayerContract entry = source.getLoudPlayerContract();
               if (entry instanceof SessionTable && ((SessionTable)entry).getStrictMessageKind() == this.strictMessageKind) {
                  entry.handlePasswordStore(this.indirectSessionHandler, target, output);
               }
            }
         }
      }
   }

   public abstract void updateVerifiedServerAdapter(
      VerifiedServerAdapter target, SpawnLookup input, LimboCoordinator output, RootListenerContract context, String data, String[] value
   );
}
