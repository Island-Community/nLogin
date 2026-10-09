package com.nickuc.login.premium;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SpawnOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.account.LocalAccountGate;
import com.nickuc.login.auth.login.OpenLoginCheckpoint;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.auth.login.PrimaryLoginProcessor;
import com.nickuc.login.auth.login.RemoteLoginBarrier;
import com.nickuc.login.auth.account.SharedAccountGate;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.IncomingSpawnState;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LoudPremiumOption;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.PremiumState;
import com.nickuc.login.model.ProxyState;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.security.hashing.CachedPasswordHashHasher;
import com.nickuc.login.security.hashing.FastPasswordVerifier;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
import com.nickuc.login.storage.password.LocalPasswordHashRepository;
import com.nickuc.login.storage.password.PasswordGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;


public class IndirectPasswordResolver {
   private final LocalPasswordHashRepository localPasswordHashRepository = new LocalPasswordHashRepository();
   public static final String name = "127.0.0.1";
   private final PasswordStore passwordStore;
   private long timestamp;
   private String activeName;

   public boolean checkState(SpawnLookup target, String input) {
      synchronized (target.object) {
         return target.fetchState() && FastPasswordVerifier.verifyState(this.passwordStore, input, target.name, target);
      }
   }

   @Nullable
   public SpawnLookup processSpawnLookup(ResultSet target) {
      return this.localPasswordHashRepository.processSpawnLookup(target);
   }

   public IncomingSpawnState loadIncomingSpawnState(SpawnLookup target, String input, InetAddress output, @Nullable PrimaryLoginProcessor context) {
      boolean data = QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState();
      if (!target.fetchStateAndState()) {
         if (!data) {
            return IncomingSpawnState.PENDING_INCOMINGSPAWNSTATE;
         }

         ProxyState value = Pbkdf2Linker.fetchProxyState();
         PremiumState result = PasswordGateway.loadPremiumState(input, output);
         switch (result) {
            case MAIN_PREMIUMSTATE:
               if (value != ProxyState.ACTIVE_PROXYSTATE) {
                  return IncomingSpawnState.PENDING_INCOMINGSPAWNSTATE;
               }
            case PREMIUM_STATE:
               if (target.fetchState()) {
                  return IncomingSpawnState.PENDING_INCOMINGSPAWNSTATE;
               } else {
                  LoginVerifier request = null;
                  if (context != null) {
                     request = LocaleVerifier.handleLoginVerifier(this.passwordStore, input, false);
                     if (context.validateState(request)) {
                        return IncomingSpawnState.CURRENT_INCOMINGSPAWNSTATE;
                     }
                  }

                  switch (value) {
                     case ACTIVE_PROXYSTATE:
                        return LocaleVerifier.checkState(this.passwordStore, request, input)
                           ? IncomingSpawnState.CURRENT_INCOMINGSPAWNSTATE
                           : IncomingSpawnState.PENDING_INCOMINGSPAWNSTATE;
                     case PROXY_STATE:
                        if (!LocaleVerifier.checkState(this.passwordStore, request, input)) {
                           return IncomingSpawnState.PENDING_INCOMINGSPAWNSTATE;
                        }

                        PasswordGateway.processPasswordStore(this.passwordStore, input, output, PremiumState.ACTIVE_PREMIUMSTATE);
                        return IncomingSpawnState.CURRENT_INCOMINGSPAWNSTATE;
                     default:
                        return IncomingSpawnState.PENDING_INCOMINGSPAWNSTATE;
                  }
               }
            case ACTIVE_PREMIUMSTATE:
            case PENDING_PREMIUMSTATE:
               return IncomingSpawnState.CURRENT_INCOMINGSPAWNSTATE;
            case CURRENT_PREMIUMSTATE:
               PasswordGateway.processPasswordStore(this.passwordStore, input, output, PremiumState.MAIN_PREMIUMSTATE);
               return IncomingSpawnState.ACTIVE_INCOMINGSPAWNSTATE;
            case PRIMARY_PREMIUMSTATE:
               PasswordGateway.processPasswordStore(this.passwordStore, input, output, PremiumState.PREMIUM_STATE);
               return IncomingSpawnState.INCOMING_SPAWN_STATE;
            default:
               throw new IllegalArgumentException("Unsupported challenge mode! " + result);
         }
      } else {
         return !data
               && (!QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.getState() || QuickPremiumOption.INTERNAL_QUICKPREMIUMOPTION.retrieveState())
               && target.retrieveState()
            ? IncomingSpawnState.PENDING_INCOMINGSPAWNSTATE
            : IncomingSpawnState.CURRENT_INCOMINGSPAWNSTATE;
      }
   }

   public boolean isState(Consumer<String> target, @Nullable SpawnLookup input, String output, @Nullable SpawnState context) {
      if (!SpawnState.ACTIVE_MAIN_SPAWNSTATE.ar()) {
         return false;
      }

      if (input != null && input.fetchState()) {
         return false;
      }

      if (SpawnState.ACTIVE_CACHED_SPAWNSTATE.a(new Object[0]).contains(output)) {
         return false;
      }

      if (context != null && context.ar()) {
         return false;
      }

      target.accept(CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_UPSTREAM_LOUDPROXYSTATE));
      return true;
   }

   public boolean checkState(SharedListenerContract target, SpawnLookup input, OutgoingSpawnState... output) {
      return this.localPasswordHashRepository.checkState(target, input, output);
   }

   public boolean isState(SpawnLookup target, OutgoingSpawnState... input) {
      return this.checkState(this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract(), target, input);
   }

   public boolean verifyState(SpawnLookup target, String input) {
      synchronized (target.object) {
         if (!target.fetchStateForState()) {
            return true;
         }

         target.activeName = input;
         target.handleTask();
         return this.isState(target, OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
      }
   }

   @Nullable
   public Long resolveLong(SpawnLookup target, String input, String output) {
      if (!SpawnState.ACTIVE_FAST_SPAWNSTATE.ar()) {
         return null;
      }

      if (output.equals(target.pendingName)) {
         return null;
      }

      CachedPasswordHashHasher context = target.resolveCachedPasswordHashHasher();
      Integer data = context.buildObject("block-tries-" + output);
      if (data == null) {
         data = 0;
      }

      data = data + 1;
      if (data < 3) {
         context.updateMessage("block-tries-" + output, data, 20L, TimeUnit.MINUTES);
         return null;
      } else {
         int value = SpawnState.ACTIVE_SAFE_SPAWNSTATE.r();
         long result = System.currentTimeMillis() + value * 60000L;
         context.updateMessage("block-" + output, result, value, TimeUnit.MINUTES);
         context.updateMessage("block-tries-" + output);
         PasswordHashContainer.processMessage(
            CachedSettingsGateway.loadState()
               ? "A conta " + input + " (" + output + ") foi bloqueada por suspeita de invasão"
               : "The account " + input + " (" + output + ") was blocked on suspicion of invasion"
         );
         return result;
      }
   }

   public boolean checkState(SpawnLookup target, String input, String output, @Nullable String context, String data) {
      return this.verifyState(this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract(), target, input, output, context, data);
   }

   public boolean canState(Consumer<String> target, @Nullable SpawnLookup input, String output, @Nullable SpawnState context) {
      if (!SpawnState.ACTIVE_VERIFIED_SPAWNSTATE.ar()) {
         return false;
      } else if (!SpawnState.ACTIVE_SHARED_SPAWNSTATE.ar()) {
         return false;
      } else if (SpawnState.ACTIVE_INCOMING_SPAWNSTATE.a(new Object[0]).contains(output)) {
         return false;
      } else if (SpawnState.ACTIVE_PRIVATE_SPAWNSTATE.ar() && input != null && input.fetchState()) {
         return false;
      } else if (context != null && context.ar()) {
         return false;
      } else {
         LocalAccountGate data = this.createLocalAccountGate(output);
         if (data == null) {
            String result = CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE);
            target.accept(result);
            return true;
         } else if (data.isState(input != null ? input.loadLong() : null, SpawnState.ACTIVE_AUTHENTICATED_SPAWNSTATE.r())) {
            String value = CachedSettingsGateway.computeMessage(
               LoudProxyState.SECONDARY_LOUDPROXYSTATE, data.loadCollection().stream().map(SharedAccountGate::getName).collect(Collectors.joining(", "))
            );
            target.accept(value);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public SpawnLookup processSpawnLookup(String target) {
      String input = target;
      byte output;
      if (!QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
         output = 1;
      } else if ((input = Pbkdf2Linker.loadMessage(target, true)) != null) {
         output = 1;
      } else if ((input = Pbkdf2Linker.loadMessage(target, false)) != null) {
         output = 0;
      } else {
         output = 1;
         input = target;
      }

      SecondaryConnectionContract context = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
      if (context instanceof ReadyBedrockResolver) {
         ReadyBedrockResolver data = (ReadyBedrockResolver)context;
         String value = data.fetchMessage();
         if (!value.isEmpty() && target.length() > value.length() && target.startsWith(value)) {
            input = target.substring(value.length());
            SharedListenerContract result = this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract();
            String request = result.resolveDirectNoticeCatalog() == DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG ? " COLLATE NOCASE" : "";
            String response = OutgoingSpawnState.buildMessage(
               "WHERE (`%s` = ?" + request + " OR `%s` = ?" + request + ") AND `%s` IS NOT NULL LIMIT 1",
               OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE,
               OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE,
               OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE
            );
            Object[] source = new Object[]{input, target};
            byte entry = 2;
            OpenLoginCheckpoint record = this.createOpenLoginCheckpoint(
               result, target, response, source, SpawnOption.PENDING_SPAWNOPTION, SpawnOption.PENDING_SPAWNOPTION, entry
            );
            return record != null ? record.resolveSpawnLookup(target) : null;
         }
      }

      return this.computeSpawnLookup(input, null, null, (boolean)output);
   }

   private boolean validateState(SpawnLookup target, SpawnLookup input, SpawnOption output) {
      if (target.loadSpawnOption() != input.loadSpawnOption()) {
         return false;
      }

      SpawnOption context = target.computeSpawnOption(true);
      SpawnOption data = input.computeSpawnOption(true);
      if (context == data) {
         return false;
      }

      SpawnLookup value;
      if (context == output) {
         value = input;
      } else {
         if (data != output) {
            return false;
         }

         value = target;
      }

      switch (output) {
         case PENDING_SPAWNOPTION:
            return false;
         case SPAWN_OPTION:
            value.dispatchTask();
            value.dispatchTaskForValue();
            return true;
         case ACTIVE_SPAWNOPTION:
            return false;
         case CURRENT_SPAWNOPTION:
            return false;
         default:
            throw new UnsupportedOperationException("Unsupported account type! " + output);
      }
   }

   @Nullable
   public SpawnLookup handleSpawnLookup(OutgoingSenderAdapter target, InternalLoginOption input, String[] output, String context, boolean data) {
      if (!QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
         SpawnLookup parameter = this.processSpawnLookup(context);
         if (parameter == null) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
            CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
         }

         return parameter;
      } else {
         String value;
         if ((value = Pbkdf2Linker.loadMessage(context, true)) == null && (value = Pbkdf2Linker.loadMessage(context, false)) == null) {
            value = context;
         }

         String result = input.findMessage() + (input == InternalLoginOption.INTERNAL_LOGIN_OPTION ? " " + output[0] : "") + " " + value;
         long request = System.currentTimeMillis();
         int source;
         if (target instanceof VerifiedServerAdapter) {
            LimboCoordinator entry = this.passwordStore.loadLimboRegistry().loadLimboCoordinator((VerifiedServerAdapter)target);
            source = result.equalsIgnoreCase(entry.b(LenientMessageKind.INCOMING_LENIENTMESSAGEKIND))
                  && request - entry.b(LenientMessageKind.OUTGOING_LENIENTMESSAGEKIND) <= TimeUnit.MINUTES.toMillis(2L)
               ? 1
               : 0;
         } else {
            source = result.equalsIgnoreCase(this.activeName) && request - this.timestamp <= TimeUnit.MINUTES.toMillis(2L) ? 1 : 0;
         }

         if (source != 0) {
            SpawnLookup message = this.processSpawnLookup(context);
            if (message == null) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
               CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
            }

            return message;
         } else {
            SharedListenerContract argument = this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract();
            String record = argument.resolveDirectNoticeCatalog() == DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG ? " COLLATE NOCASE" : "";
            String item = OutgoingSpawnState.buildMessage("WHERE `%s` = ?" + record + " LIMIT 4", OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE);
            Object[] element = new Object[]{value};

            try {
               PrimaryLoginHandler content = this.localPasswordHashRepository.processPrimaryLoginHandler(argument, item, element);

               SpawnLookup packet;
               label182: {
                  label183: {
                     label184: {
                        Object option;
                        label200: {
                           try {
                              if (content != null) {
                                 ResultSet payload = content.resolveObject();
                                 int holder = 0;
                                 ArrayList reference = new ArrayList();

                                 while (payload.next()) {
                                    if (++holder == 4) {
                                       throw new IllegalStateException(
                                          "Collision found for user with name \""
                                             + value
                                             + "\" while fetching: same last name for 4 accounts (database modified manually?)"
                                       );
                                    }

                                    packet = this.localPasswordHashRepository.processSpawnLookup(payload);
                                    if (packet == null) {
                                       option = null;
                                       break label200;
                                    }

                                    reference.add(packet);
                                 }

                                 if (reference.isEmpty()) {
                                    packet = SpawnLookup.handleSpawnLookup(value);
                                    break label184;
                                 }

                                 if (data && reference.stream().noneMatch(instance -> instance.loadSpawnOption() == SpawnOption.ACTIVE_SPAWNOPTION)) {
                                    reference.add(SpawnLookup.handleSpawnLookup(value));
                                 }

                                 if (reference.size() == 1) {
                                    packet = (SpawnLookup)reference.get(0);
                                    break label183;
                                 }

                                 reference.sort(Comparator.comparingInt(instance -> instance.loadSpawnOption().ordinal()));
                                 if (target instanceof VerifiedServerAdapter) {
                                    LimboCoordinator notice = this.passwordStore.loadLimboRegistry().loadLimboCoordinator((VerifiedServerAdapter)target);
                                    notice.updateLenientMessageKind(LenientMessageKind.OUTGOING_LENIENTMESSAGEKIND, request);
                                    notice.updateLenientMessageKind(LenientMessageKind.INCOMING_LENIENTMESSAGEKIND, result);
                                    SecondaryAccountHandler player = notice.getSecondaryAccountHandler();
                                    CachedSettingsGateway.handleOutgoingSenderAdapter(target, LoudProxyState.SAFE_LOUDPROXYSTATE, contextValue -> {
                                       if (contextValue.length() > 3 && contextValue.contains("{1}") && contextValue.contains("{2}")) {
                                          for (SpawnLookup valueValue : reference) {
                                             String resultValue = valueValue.fetchMessage();
                                             String requestValue = valueValue.loadSpawnOption().buildMessage(true, (instanceValue, targetValue) -> instanceValue + "(" + targetValue + ")");
                                             String response = input.findMessage() + (output.length > 0 ? " " + String.join(" ", output) : "");
                                             player.executeMessage(contextValue.replace("{1}", resultValue).replace("{2}", requestValue), resultValue + " " + requestValue, response);
                                          }
                                       } else {
                                          player.executeMessage(contextValue);
                                       }
                                    }, value);
                                 } else {
                                    this.timestamp = request;
                                    this.activeName = result;

                                    for (SpawnLookup profile : reference) {
                                       if (value.equalsIgnoreCase(profile.activeName)) {
                                          value = profile.activeName;
                                       }
                                    }

                                    CachedSettingsGateway.handleOutgoingSenderAdapter(
                                       target,
                                       LoudProxyState.SAFE_LOUDPROXYSTATE,
                                       inputValue -> {
                                          if (inputValue.length() > 3 && inputValue.contains("{1}") && inputValue.contains("{2}")) {
                                             for (SpawnLookup contextValue : reference) {
                                                target.dispatchMessage(
                                                   inputValue.replace("{1}", contextValue.fetchMessage())
                                                      .replace("{2}", contextValue.loadSpawnOption().buildMessage(true, (instanceValue, targetValue) -> instanceValue + "(" + targetValue + ")"))
                                                );
                                             }
                                          } else {
                                             target.dispatchMessage(inputValue);
                                          }
                                       },
                                       value
                                    );
                                 }

                                 packet = null;
                                 break label182;
                              }
                           } catch (Throwable property) {
                              if (content != null) {
                                 try {
                                    content.close();
                                 } catch (Throwable setting) {
                                    property.addSuppressed(setting);
                                 }
                              }

                              throw property;
                           }

                           if (content != null) {
                              content.close();
                           }

                           return null;
                        }

                        if (content != null) {
                           content.close();
                        }

                        return (SpawnLookup)option;
                     }

                     if (content != null) {
                        content.close();
                     }

                     return packet;
                  }

                  if (content != null) {
                     content.close();
                  }

                  return packet;
               }

               if (content != null) {
                  content.close();
               }

               return packet;
            } catch (Exception attribute) {
               PasswordHashContainer.handleMessage("Failed to fetch the account using \"" + item + "\" with values \"" + Arrays.toString(element) + "\"", attribute);
               return null;
            }
         }
      }
   }

   public IndirectPasswordResolver(PasswordStore target) {
      this.passwordStore = target;
   }

   private boolean isState(String target, UUID input) {
      if (input == null) {
         throw new IllegalArgumentException("Mojang ID cannot be null!");
      }

      SharedListenerContract output = this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract();
      String context = output.resolveDirectNoticeCatalog() == DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG ? " COLLATE NOCASE" : "";
      String data = "Unknown" + DeadLoginFlow.processMessage(input);
      String value = output.resolveDirectNoticeCatalog() != DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG
            && output.resolveDirectNoticeCatalog() != DirectNoticeCatalog.DIRECT_NOTICE_CATALOG
         ? ""
         : " LIMIT 1";
      String result = String.format(
            "UPDATE `%s` SET `%s` = ? WHERE `%s` IS NOT NULL AND `%s` != ? AND `%s` = ?" + context,
            SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]),
            OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName(),
            OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName(),
            OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName(),
            OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()
         )
         + value;
      Object[] request = new Object[]{data, DeadLoginFlow.processMessage(input), target};

      try {
         output.saveMessage(result, request);
         return true;
      } catch (Exception source) {
         PasswordHashContainer.handleMessage("Failed to rename premium using \"" + result + "\" with values \"" + Arrays.toString(request) + "\"", source);
         return false;
      }
   }

   @Nullable
   public SpawnLookup computeSpawnLookup(String target, @Nullable UUID input, @Nullable UUID output, boolean context) {
      SpawnOption data;
      if (output != null) {
         SecondaryConnectionContract value = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
         data = value != null && !value.fetchState() ? SpawnOption.ACTIVE_SPAWNOPTION : SpawnOption.PENDING_SPAWNOPTION;
         context = 1;
      } else if (input != null) {
         data = SpawnOption.SPAWN_OPTION;
      } else {
         data = SpawnOption.ACTIVE_SPAWNOPTION;
      }

      SharedListenerContract item = this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract();
      String result = item.resolveDirectNoticeCatalog() == DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG ? " COLLATE NOCASE" : "";
      String request;
      Object[] response;
      byte source;
      SpawnOption entry;
      switch (data) {
         case PENDING_SPAWNOPTION:
            request = OutgoingSpawnState.buildMessage("WHERE `%s` = ? LIMIT 1", OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE);
            response = new Object[]{DeadLoginFlow.processMessage(output)};
            source = -1;
            entry = data;
            break;
         case SPAWN_OPTION:
            request = OutgoingSpawnState.buildMessage(
               "WHERE `%s` = ? OR `%s` = ? LIMIT 2", OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE, OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE
            );
            response = new Object[]{DeadLoginFlow.processMessage(input), DeadLoginFlow.processMessage(input)};
            source = -1;
            entry = data;
            break;
         case ACTIVE_SPAWNOPTION:
            if (context != 0) {
               request = OutgoingSpawnState.buildMessage(
                  "WHERE `%s` = ?" + result + " AND `%s` IS NULL LIMIT 3",
                  OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE,
                  OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE
               );
               entry = SpawnOption.SPAWN_OPTION;
               source = 3;
            } else {
               request = OutgoingSpawnState.buildMessage(
                  "WHERE `%s` = ?" + result + " AND `%s` IS NULL AND `%s` IS NULL LIMIT 2",
                  OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE,
                  OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE,
                  OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE
               );
               entry = data;
               source = 2;
            }

            response = new Object[]{target};
            break;
         default:
            throw new IllegalArgumentException("Invalid account type! " + data);
      }

      OpenLoginCheckpoint record = this.createOpenLoginCheckpoint(item, target, request, response, data, entry, source);
      if (data == SpawnOption.ACTIVE_SPAWNOPTION && record != null && record.spawnLookup == null) {
         request = OutgoingSpawnState.buildMessage("WHERE `%s` = ? LIMIT 1", OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE);
         entry = SpawnOption.SPAWN_OPTION;
         source = -1;
         response = new Object[]{DeadLoginFlow.processMessage(DeadLoginFlow.computeUniqueId(target))};
         record = this.createOpenLoginCheckpoint(item, target, request, response, data, entry, source);
      }

      return record != null ? record.resolveSpawnLookup(target) : null;
   }

   @Nullable
   public SpawnLookup createSpawnLookup(String target, UUID input, boolean output) {
      SharedListenerContract context = this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract();
      String data = context.resolveDirectNoticeCatalog() == DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG ? " COLLATE NOCASE" : "";
      String value = OutgoingSpawnState.buildMessage(
         "WHERE `%s` = ? OR `%s` = ? OR `%s` = ?" + data + " AND `%s` IS NULL LIMIT 3",
         OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE,
         OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE,
         OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE,
         OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE
      );
      Object[] result = new Object[]{DeadLoginFlow.processMessage(input), DeadLoginFlow.processMessage(input), target};
      byte request = 3;
      SpawnOption response = output ? SpawnOption.SPAWN_OPTION : SpawnOption.ACTIVE_SPAWNOPTION;
      OpenLoginCheckpoint source = this.createOpenLoginCheckpoint(context, target, value, result, null, response, request);
      if (source != null && source.spawnLookup == null) {
         value = OutgoingSpawnState.buildMessage("WHERE `%s` = ? LIMIT 1", OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE);
         request = -1;
         result = new Object[]{DeadLoginFlow.processMessage(DeadLoginFlow.computeUniqueId(target))};
         source = this.createOpenLoginCheckpoint(context, target, value, result, null, response, request);
      }

      return source != null ? source.resolveSpawnLookup(target) : null;
   }

   @Nullable
   private OpenLoginCheckpoint createOpenLoginCheckpoint(
      SharedListenerContract target, String input, String output, Object[] context, @Nullable SpawnOption data, @Nullable SpawnOption value, int result
   ) {
      try {
         PrimaryLoginHandler request = this.localPasswordHashRepository.processPrimaryLoginHandler(target, output, context);

         OpenLoginCheckpoint reference;
         label105: {
            Object subject;
            label118: {
               try {
                  if (request != null) {
                     ResultSet response = request.resolveObject();
                     int source = 0;
                     SpawnLookup entry = null;

                     while (response.next()) {
                        if (++source == result) {
                           String holder = value != null ? value.name().toLowerCase(Locale.ENGLISH) : "any";
                           throw new IllegalStateException(
                              "Collision found for user with name \""
                                 + input
                                 + "\" while fetching preferring "
                                 + holder
                                 + ": same last name for "
                                 + (data != null ? data.name() : "any")
                                 + " account (database modified manually?)"
                           );
                        }

                        SpawnLookup record = this.localPasswordHashRepository.processSpawnLookup(response);
                        if (record == null) {
                           subject = null;
                           break label118;
                        }

                        if (entry == null) {
                           entry = record;
                        } else if (value == null || record.loadSpawnOption() == value) {
                           if (value != null && entry.loadSpawnOption() == value && !this.validateState(entry, record, value)) {
                              String item = value.name().toLowerCase(Locale.ENGLISH);
                              throw new IllegalStateException(
                                 "Collision found for user with name \""
                                    + input
                                    + "\" while fetching preferring "
                                    + item
                                    + ": same last name for "
                                    + (data != null ? data.name() : "any")
                                    + " account (database modified manually?)"
                              );
                           }

                           entry = record;
                        }
                     }

                     reference = new OpenLoginCheckpoint(entry);
                     break label105;
                  }
               } catch (Throwable content) {
                  if (request != null) {
                     try {
                        request.close();
                     } catch (Throwable element) {
                        content.addSuppressed(element);
                     }
                  }

                  throw content;
               }

               if (request != null) {
                  request.close();
               }

               return null;
            }

            if (request != null) {
               request.close();
            }

            return (OpenLoginCheckpoint)subject;
         }

         if (request != null) {
            request.close();
         }

         return reference;
      } catch (Exception payload) {
         PasswordHashContainer.handleMessage("Failed to fetch the account using \"" + output + "\" with values \"" + Arrays.toString(context) + "\"", payload);
         return null;
      }
   }

   @Nullable
   public LocalAccountGate processLocalAccountGate(SharedListenerContract target, String input) {
      if (target == null) {
         throw new IllegalStateException("Database is not loaded to perform a get address operation.");
      }

      try {
         PrimaryLoginHandler output = target.buildPrimaryLoginHandler(
            OutgoingSpawnState.buildMessage(
               "SELECT `%s`, `%s`, `%s`, `%s`, `%s` FROM `" + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]) + "` WHERE `%s` = ?",
               OutgoingSpawnState.OUTGOING_SPAWN_STATE,
               OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE,
               OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE,
               OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE,
               OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE,
               OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE
            ),
            input
         );

         LocalAccountGate element;
         try {
            ResultSet context = output.resolveObject();
            ArrayList data = new ArrayList();

            while (context.next()) {
               String value = context.getString(3);
               UUID result = DeadLoginFlow.buildUniqueId(context.getString(4));
               UUID request = DeadLoginFlow.buildUniqueId(context.getString(5));
               SpawnOption response = result != null
                  ? SpawnOption.SPAWN_OPTION
                  : (
                     request != null
                        ? SpawnOption.PENDING_SPAWNOPTION
                        : (value != null && !value.isEmpty() && !"null".equalsIgnoreCase(value) ? SpawnOption.ACTIVE_SPAWNOPTION : SpawnOption.CURRENT_SPAWNOPTION)
                  );
               data.add(new SharedAccountGate(context.getLong(1), context.getString(2), response));
            }

            element = new LocalAccountGate(input, RemoteLoginBarrier.createRemoteLoginBarrier(data));
         } catch (Throwable entry) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable source) {
                  entry.addSuppressed(source);
               }
            }

            throw entry;
         }

         if (output != null) {
            output.close();
         }

         return element;
      } catch (SQLException record) {
         PasswordHashContainer.handleMessage("Unable to load the user's address: " + input + " [invalid database data?]", record);
      } catch (Exception item) {
         PasswordHashContainer.handleMessage("Unable to load the user's address: " + input + " [corrupted data?]", item);
      }

      return null;
   }

   @Nullable
   public LocalAccountGate createLocalAccountGate(String target) {
      return this.processLocalAccountGate(this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract(), target);
   }

   public void saveSpawnLookup(SpawnLookup target, String input, @Nullable String output) {
      synchronized (target.object) {
         target.pendingName = input;
         target.activeTimestamp = System.currentTimeMillis();
         target.cachedPasswordHashHasher.performMessage("encrypted");
         target.cachedPasswordHashHasher.performMessage("pwd");
         target.cachedPasswordHashHasher.performMessage("force-register-spawn");
         target.cachedPasswordHashHasher.performMessage("force-register-commands");
         target.cachedPasswordHashHasher.performMessage("force-invalid-session");
         this.isState(
            target,
            OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE,
            OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE,
            OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE
         );
      }
   }

   public boolean verifyState(SpawnLookup target) {
      return this.checkState(this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract(), target);
   }

   public boolean hasState(SharedListenerContract target, SpawnLookup input) {
      synchronized (input.object) {
         if (!input.fetchState()) {
            return true;
         }

         String context = input.retrieveMessage();
         if (context == null) {
            throw new IllegalStateException("Account name is not loaded to perform an update operation.");
         }

         if (target == null) {
            throw new IllegalStateException("Database is not loaded to perform an update operation.");
         }

         PasswordHashContainer.dispatchMessage(
            "[" + target.resolveDirectNoticeCatalog().resolveMessage() + "] [UNREGISTER]: Removing the " + context + "'s account..."
         );
         input.updateTask();
         return this.localPasswordHashRepository.checkState(target, input);
      }
   }

   @Nullable
   public SpawnLookup loadSpawnLookup(OutgoingSenderAdapter target, InternalLoginOption input, String[] output, String context) {
      return this.handleSpawnLookup(target, input, output, context, false);
   }

   public boolean canState(SpawnLookup target) {
      return this.hasState(this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract(), target);
   }

   public boolean checkState(SharedListenerContract target, SpawnLookup input) {
      synchronized (input.object) {
         String context = input.activeName;
         Long data = input.value;
         if (context == null) {
            throw new IllegalStateException("Account name is not loaded to perform an update operation.");
         }

         if (data == null) {
            throw new IllegalStateException("Account id is not loaded to perform a delete operation.");
         }

         if (target == null) {
            throw new IllegalStateException("Database is not loaded to perform an update operation.");
         }

         if (!input.resolveStateForState()) {
            return true;
         }

         try {
            PasswordHashContainer.dispatchMessage(
               "[" + target.resolveDirectNoticeCatalog().resolveMessage() + "] [DELETE]: Deleting the " + context + "'s account..."
            );
            target.computeStrictLoginHandler(
               String.format(
                  "DELETE FROM `%s` WHERE `%s` = ?", SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]), OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName()
               ),
               data
            );
            String value = "DELETE FROM `"
               + SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])
               + "` WHERE `"
               + OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName()
               + "` = "
               + data;
            PasswordHashContainer.dispatchMessage("[" + target.resolveDirectNoticeCatalog().resolveMessage() + "] [DELETE]: \"" + value + "\"");
            String result = QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState() ? Pbkdf2Linker.handleMessage(context, input.fetchStateAndState()) : context;
            VerifiedServerAdapter request = this.passwordStore.b().resolveVerifiedServerAdapter(result);
            if (request != null) {
               request.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.PRIVATE_LOUDPROXYSTATE, request));
            }

            return true;
         } catch (SQLException source) {
            PasswordHashContainer.handleMessage("Unable to delete the " + context + " account's [database modified manually?]", source);
         } catch (Exception entry) {
            PasswordHashContainer.handleMessage("Unable to delete the " + context + " account's [corrupted data?]", entry);
         }

         return false;
      }
   }

   public boolean validateState(SpawnLookup target, UUID input, String output, String context) {
      if (input == null) {
         throw new IllegalArgumentException("Bedrock ID cannot be null!");
      }

      if (!target.fetchState()) {
         synchronized (target.object) {
            target.activeName = output;
            target.pendingName = context;
            target.timestamp = target.activeTimestamp = System.currentTimeMillis();
            if (this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract().fetchState()) {
               target.executeUniqueId(input);
            }

            target.cachedPasswordHashHasher.updateMessage("force-register-spawn", true);
            target.cachedPasswordHashHasher.updateMessage("force-register-commands", true);
            return this.isState(target);
         }
      } else if (target.getState() && !target.retrieveMessage().equals(output)) {
         target.activeName = output;
         return this.isState(target, OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE);
      } else {
         return true;
      }
   }

   public OpenLoginCheckpoint buildOpenLoginCheckpoint(UUID target) {
      if (target == null) {
         throw new IllegalArgumentException("Unique ID cannot be null!");
      }

      SharedListenerContract input = this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract();
      String output = String.format("WHERE `%s` = ? LIMIT 1", OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName());
      Object[] context = new Object[]{DeadLoginFlow.processMessage(target)};
      return this.createOpenLoginCheckpoint(input, DeadLoginFlow.processMessage(target), output, context, null, null, 2);
   }

   public boolean canState(SpawnLookup target, UUID input, String output, String context, boolean data) {
      if (input == null) {
         throw new IllegalArgumentException("Mojang ID cannot be null!");
      }

      if (input.version() != 4) {
         throw new IllegalArgumentException(
            "The Mojang ID provided is not valid! expected version = 4, received version = " + input.version() + ", username = " + output + ", uuid = " + input
         );
      }

      if (!target.fetchStateAndState()) {
         if (!this.isState(output, input)) {
            return false;
         }

         synchronized (target.object) {
            target.processUniqueId(input);
            if (target.pendingUniqueId == null) {
               target.handleUniqueId(data ? input : DeadLoginFlow.createUniqueId(output, input));
            }

            target.activeName = output;
            target.pendingName = context;
            target.timestamp = target.activeTimestamp = System.currentTimeMillis();
            target.cachedPasswordHashHasher.updateMessage("force-register-spawn", true);
            target.cachedPasswordHashHasher.updateMessage("force-register-commands", true);
            return this.isState(target);
         }
      } else {
         if (target.getMojangId() != null && !input.equals(target.getMojangId())) {
            throw new IllegalArgumentException(
               "Mojang ID mismatch! expected uuid = "
                  + target.getMojangId()
                  + ", received uuid = "
                  + input
                  + ", expected name = "
                  + target.retrieveMessage()
                  + ", received name = "
                  + output
            );
         }

         ArrayList value = new ArrayList();
         if (target.pendingUniqueId == null) {
            synchronized (target.object) {
               target.handleUniqueId(data ? input : DeadLoginFlow.createUniqueId(output, input));
               value.add(OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE);
            }
         }

         if (!target.retrieveMessage().equals(output)) {
            if (!this.isState(output, input)) {
               return false;
            }

            synchronized (target.object) {
               target.activeName = output;
               value.add(OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE);
            }
         }

         return value.isEmpty() || this.isState(target, value.toArray(new OutgoingSpawnState[0]));
      }
   }

   public LoudPremiumOption handleLoudPremiumOption(SpawnLookup target, String input) {
      if (target.fetchState() && target.activeTimestamp > 0L && QuickPremiumOption.INCOMING_QUICKPREMIUMOPTION.retrieveState()) {
         if (target.resolveCachedPasswordHashHasher().resolveObject("force-invalid-session", false)) {
            return LoudPremiumOption.LOUD_PREMIUM_OPTION;
         } else if (!input.equals(target.pendingName)) {
            return LoudPremiumOption.ACTIVE_LOUDPREMIUMOPTION;
         } else {
            int output = QuickPremiumOption.OUTGOING_QUICKPREMIUMOPTION.r();
            if (output <= 0) {
               return LoudPremiumOption.LOUD_PREMIUM_OPTION;
            } else if (input.equals("127.0.0.1")
               || input.equals("localhost")
               || input.equals("172.18.0.1")
               || QuickPremiumOption.DIRECT_QUICKPREMIUMOPTION.a(new Object[0]).stream().anyMatch(input::equals)) {
               return LoudPremiumOption.LOUD_PREMIUM_OPTION;
            } else if (target.activeName != null
               && QuickPremiumOption.SECONDARY_QUICKPREMIUMOPTION.a(new Object[0]).stream().anyMatch(targetValue -> target.activeName.equals(targetValue))) {
               return LoudPremiumOption.LOUD_PREMIUM_OPTION;
            } else {
               return System.currentTimeMillis() - target.activeTimestamp > output * 60000L
                  ? LoudPremiumOption.PENDING_LOUDPREMIUMOPTION
                  : LoudPremiumOption.CURRENT_LOUDPREMIUMOPTION;
            }
         }
      } else {
         return LoudPremiumOption.LOUD_PREMIUM_OPTION;
      }
   }

   public boolean verifyState(SharedListenerContract target, SpawnLookup input, String output, String context, @Nullable String data, String value) {
      synchronized (input.object) {
         input.handleMessage(output, context, data, value, true);
         return this.localPasswordHashRepository.checkState(target, input);
      }
   }

   public boolean validateState(SpawnLookup target, String input) {
      synchronized (target.object) {
         target.executeMessage(input, true);
         return this.isState(target);
      }
   }
}
