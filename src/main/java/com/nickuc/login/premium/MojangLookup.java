package com.nickuc.login.premium;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.login.OpenLoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

public class MojangLookup extends LoginSource {
   private static final List<String> entries = Arrays.asList("§a(mojang)", "§b(random)", "§c(offline)");
   private static final List<String> activeEntries = Arrays.asList("mojang", "random", "offline");
   private static float factor = Float.intBitsToFloat(1097859072);
   private static float activeFactor = Float.intBitsToFloat(1077936128);

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 3) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " <player> <uuid type/raw uuid>"
         );
      } else {
         LiveLoginCheckpoint output = new LiveLoginCheckpoint();
         IndirectPasswordResolver context = this.passwordStore.findIndirectPasswordResolver();
         SpawnLookup data = context.loadSpawnLookup(target, super.internalLoginOption, input, input[1]);
         if (data != null) {
            if (!data.resolveStateForState()) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
            } else {
               UUID value = data.getUniqueId();
               UUID result = data.getMojangId();
               int request = this.resolveCount(value, result);
               String response = input[2].toLowerCase(Locale.ENGLISH);
               UUID source = null;
               int entry = activeEntries.indexOf(response);
               if (entry == -1) {
                  try {
                     switch (response.length()) {
                        case 32:
                        case 36:
                           source = DeadLoginFlow.buildUniqueId(response);
                           entry = this.resolveCount(source, result);
                           break;
                        default:
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§eValid types: §f" + String.join(", ", activeEntries));
                           return;
                     }
                  } catch (IllegalArgumentException holder) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cInvalid unique id! " + response);
                     return;
                  }
               }

               if ((entry != 0 || request != entry) && (value == null || !value.equals(source))) {
                  String record = data.retrieveMessage();
                  if (source == null) {
                     switch (entry) {
                        case 0:
                           if (result != null) {
                              source = result;
                           } else {
                              CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§7Contacting Mojang servers, please wait...");
                              LoginVerifier item = LocaleVerifier.handleLoginVerifier(this.passwordStore, record, true);
                              if (item == null) {
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cUnable to contact Mojang's servers.");
                                 return;
                              }

                              UUID element = item.fetchUniqueId();
                              if (element == null) {
                                 CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cThe given player nickname §f(" + record + ") §cis not premium.");
                                 return;
                              }

                              source = element;
                           }
                           break;
                        case 1:
                           source = UUID.randomUUID();
                           break;
                        case 2:
                           source = DeadLoginFlow.computeUniqueId(record);
                           break;
                        default:
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cUnsupported unique id type! " + entry);
                     }
                  }

                  if (value != null && value.equals(source)) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§eThis account is already using the UUID §f%s %s", value, entries.get(entry));
                  } else {
                     OpenLoginCheckpoint reference = context.buildOpenLoginCheckpoint(source);
                     if (reference == null) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                     } else if (reference.spawnLookup != null) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target,
                           "§eAnother account §f(\"%s\") §eis already using the UUID §f%s %s",
                           reference.spawnLookup.retrieveMessage(),
                           value,
                           entries.get(entry)
                        );
                     } else {
                        synchronized (data.object) {
                           data.handleUniqueId(source);
                           if (!context.isState(data, OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE)) {
                              CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                              CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                           } else {
                              CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
                              CachedSettingsGateway.handleOutgoingSenderAdapter(
                                 target, "§a✔ You have successfully changed §f%s's §aUUID to §f%s %s", record, source, entries.get(entry)
                              );
                              CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                              CachedSettingsGateway.handleOutgoingSenderAdapter(
                                 target, "§e⚑ This operation took §f" + output.loadMessage(TimeUnit.SECONDS, 2) + "s§e."
                              );
                              PasswordHashContainer.dispatchMessage(
                                 "The " + record + " player had his UUID changed to " + source + " (" + activeEntries.get(entry) + ") by " + target.getName() + "."
                              );
                           }
                        }
                     }
                  }
               } else {
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§eThis account is already using the UUID §f" + value + " " + entries.get(entry));
               }
            }
         }
      }
   }

   private int resolveCount(@Nullable UUID target, @Nullable UUID input) {
      if (target == null) {
         return -1;
      } else if (target.version() == 3) {
         return 2;
      } else {
         return target.equals(input) ? 0 : 1;
      }
   }

   public MojangLookup(PasswordStore target) {
      super(target, "changeuuid", "nlogin.command.nlogin.changeuuid", true, false);
   }
}
