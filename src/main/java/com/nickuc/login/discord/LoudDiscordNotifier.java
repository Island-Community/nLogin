package com.nickuc.login.discord;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictPremiumOption;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.account.LocalAccountGate;
import com.nickuc.login.auth.locale.LocaleBarrier;
import com.nickuc.login.auth.locale.LocaleFlow;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoudDiscordNotifier extends LoginSource {
   private static float factor = Float.intBitsToFloat(1097859072);
   private static float activeFactor = Float.intBitsToFloat(1077936128);

   private String handleMessage(String target) {
      return target != null ? target : (this.loadState() ? "desconhecido" : "unknown");
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 2) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage() + " <player/ip>");
      } else {
         LiveLoginCheckpoint output = new LiveLoginCheckpoint();
         String context = input[1];
         IndirectPasswordResolver data = this.passwordStore.findIndirectPasswordResolver();
         SpawnLookup value = data.loadSpawnLookup(target, super.internalLoginOption, input, context);
         if (value != null) {
            if (!value.fetchState()) {
               LocalAccountGate message = data.createLocalAccountGate(input[1]);
               if (message == null) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else if (message.loadState()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else {
                  Function notice = targetValue -> String.format(
                     " §8⋆ §7%s %s", targetValue.getName(), targetValue.loadSpawnOption().buildMessage(this.loadState(), (instance, targetValue) -> instance + "(" + targetValue + ")")
                  );
                  CachedSettingsGateway.handleOutgoingSenderAdapter(
                     target,
                     this.loadState()
                        ? "§fContas com o IP fornecido §a" + message.retrieveMessage() + "§f:"
                        : "§fAccounts with the provided IP §a" + message.retrieveMessage() + "§f:"
                  );
                  message.loadCollection()
                     .stream()
                     .limit(20L)
                     .<String>map(notice)
                     .forEach(targetValue -> CachedSettingsGateway.handleOutgoingSenderAdapter(target, targetValue));
               }
            } else if (!value.fetchState()) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
               CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
            } else {
               LocalAccountGate result = data.createLocalAccountGate(value.findMessage());
               if (result == null) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else {
                  VerifiedServerAdapter request = this.passwordStore.b().resolveVerifiedServerAdapter(context);
                  long response = System.currentTimeMillis();
                  long entry = value.findTime();
                  long item = value.loadTime();
                  UUID content = value.getUniqueId();
                  UUID payload = value.getMojangId();
                  UUID holder = value.getBedrockId();
                  if (content == null && holder != null) {
                     content = holder;
                  }

                  int reference = content != null && content.equals(payload) ? 1 : 0;
                  String subject;
                  if (content == null) {
                     subject = "§c(undefined)";
                  } else if (content.version() == 3) {
                     subject = "§c(offline)";
                  } else if (content.getMostSignificantBits() == 0L) {
                     subject = "§e(bedrock)";
                  } else if (reference != 0) {
                     subject = "§a(mojang)";
                  } else {
                     subject = "§b(random)";
                  }

                  Function option = targetValue -> String.format(
                     "§f%s %s", targetValue.getName(), targetValue.loadSpawnOption().buildMessage(this.loadState(), (instance, targetValue) -> instance + "(" + targetValue + ")")
                  );
                  int setting = request != null && this.passwordStore.loadLimboRegistry().canState(request) ? 1 : 0;
                  if (this.loadState()) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §8⋆ §7Autenticado: " + this.processMessage((boolean)setting, "sim", "não"));
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §8⋆ §7Tipo: " + value.loadSpawnOption().buildMessage(true));
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target, " §8⋆ §7Último login: " + this.buildMessage(value, item, "§fhá %s §b(" + LocaleBarrier.computeMessage(item, true) + ")")
                     );
                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target,
                        " §8⋆ §7Tempo de registro: §f" + LocaleFlow.loadMessage(entry, response, true) + " §b(" + LocaleBarrier.computeMessage(entry, true) + ")"
                     );
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §eDados:");
                     if (!result.loadState()) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target, "  §8⋆ §7Contas: %s", result.loadCollection().stream().limit(20L).<CharSequence>map(option).collect(Collectors.joining(" "))
                        );
                     }

                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7UUID: §f" + DeadLoginFlow.processMessage(content) + " " + subject);
                     if (reference == 0 && value.fetchStateAndState()) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7ID da Mojang: §f" + DeadLoginFlow.processMessage(payload));
                     }

                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7Último IP: §f" + this.handleMessage(result.retrieveMessage()));
                     QuickDiscordHandler property = value.fetchQuickDiscordHandler();
                     boolean attribute = property.resolveState();
                     if (attribute) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §eContas:");
                        String parameter = property.loadMessage();
                        if (parameter != null && StrictPremiumOption.VERIFIED_STRICTPREMIUMOPTION.retrieveState()) {
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7Email: §f" + parameter);
                        }

                        String argument = property.getMessage();
                        if (argument != null && StrictPremiumOption.STRICT_PREMIUM_OPTION.retrieveState()) {
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7Discord: §f" + argument);
                        }
                     }

                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target, " §e⚑ Esta operação foi processada em §f" + output.loadMessage(TimeUnit.MILLISECONDS, 2) + "ms§e."
                     );
                  } else {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §8⋆ §7Authenticated: " + this.processMessage((boolean)setting, "yes", "no"));
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §8⋆ §7Type: " + value.loadSpawnOption().buildMessage(false));
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target, " §8⋆ §7Last login: " + this.buildMessage(value, item, "§f%s ago §b(" + LocaleBarrier.computeMessage(item, false) + ")")
                     );
                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target,
                        " §8⋆ §7Time registered: §f" + LocaleFlow.loadMessage(entry, response, true) + " §b(" + LocaleBarrier.computeMessage(entry, false) + ")"
                     );
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §eData:");
                     if (!result.loadState()) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target, "  §8⋆ §7Accounts: %s", result.loadCollection().stream().limit(20L).<CharSequence>map(option).collect(Collectors.joining(" "))
                        );
                     }

                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7UUID: §f" + DeadLoginFlow.processMessage(content) + " " + subject);
                     if (reference == 0 && value.fetchStateAndState()) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7Mojang ID: §f" + DeadLoginFlow.processMessage(payload));
                     }

                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7Last IP: §f" + this.handleMessage(result.retrieveMessage()));
                     QuickDiscordHandler event = value.fetchQuickDiscordHandler();
                     boolean packet = event.resolveState();
                     if (packet) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §eAccounts:");
                        String session = event.loadMessage();
                        if (session != null && StrictPremiumOption.VERIFIED_STRICTPREMIUMOPTION.retrieveState()) {
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7Email: §f" + session);
                        }

                        String account = event.getMessage();
                        if (account != null && StrictPremiumOption.STRICT_PREMIUM_OPTION.retrieveState()) {
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §8⋆ §7Discord: §f" + account);
                        }
                     }

                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target, " §e⚑ This operation took §f" + output.loadMessage(TimeUnit.MILLISECONDS, 2) + "ms§e."
                     );
                  }

                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
               }
            }
         }
      }
   }

   private String processMessage(boolean target, String input, String output) {
      return target ? "§a" + input : "§c" + output;
   }

   public LoudDiscordNotifier(PasswordStore target) {
      super(target, "verify", "nlogin.command.nlogin.verify", false, false, "dupeip");
   }

   private String buildMessage(SpawnLookup target, long input, String context) {
      return this.passwordStore.b().resolveVerifiedServerAdapter(target.retrieveMessage()) == null
         ? String.format(context, LocaleFlow.loadMessage(input, System.currentTimeMillis(), true))
         : "§aonline";
   }
}
