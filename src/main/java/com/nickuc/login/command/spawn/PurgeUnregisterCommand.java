package com.nickuc.login.command.spawn;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.locale.LocaleCheckpoint;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.listener.bukkit.VerifiedLoginGuard;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.security.hashing.PrimaryPasswordHashVerifier;
import com.nickuc.login.session.BusyLimboStore;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.spawn.IndirectLoginKind;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.tasks.SynchronizeWithServerThreadTask;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PurgeUnregisterCommand extends LoginSource {
   private static final List<String> entries = Arrays.asList("set", "unset", "teleport", "list");
   private static float factor = Float.intBitsToFloat(1097859072);
   private static float activeFactor = Float.intBitsToFloat(1077936128);
   private static float pendingFactor = Float.intBitsToFloat(1077936128);
   private static float currentFactor = Float.intBitsToFloat(1097859072);

   @Override
   public List<String> computeCollection(OutgoingSenderAdapter target, String input, String[] output) {
      switch (output.length) {
         case 1:
            return LocaleCheckpoint.handleCollection(entries, output);
         case 2:
         case 3:
            return LocaleCheckpoint.handleCollection(IndirectLoginKind.entries, output);
         default:
            return null;
      }
   }

   public static void updatePasswordStore(PasswordStore instance, SecondaryAccountHandler target, LenientPremiumOption input) {
      target.executeMessage("");
      String output = "/nlogin spawn set %s";

      for (IndirectLoginKind result : IndirectLoginKind.values()) {
         target.dispatchMessage(
            String.format(
               " §8⋆ %s §7%s: §f%s", computeMessage(instance, result), result.activeName, result.processMessage(input == LenientPremiumOption.LENIENT_PREMIUM_OPTION)
            ),
            String.format("/nlogin spawn set %s", result.name)
         );
      }

      target.executeMessage("");
   }

   private static String computeMessage(PasswordStore instance, IndirectLoginKind target) {
      int input = target == IndirectLoginKind.MAIN_INDIRECTLOGINKIND
         ? (
            instance.a().loadPrimaryPasswordHashVerifier().hasState(IndirectLoginKind.INDIRECT_LOGIN_KIND.fetchMessage())
                  && instance.a().loadPrimaryPasswordHashVerifier().isState(target.fetchMessage())
               ? 1
               : 0
         )
         : instance.a().loadPrimaryPasswordHashVerifier().hasState(target.fetchMessage());
      return input != 0 ? "§a✔" : "§c✖";
   }

   private void updateVerifiedServerAdapter(VerifiedServerAdapter target, SecondaryAccountHandler input, IndirectLoginKind output) {
      target.getLinkedSessionHandler()
         .buildStrictCommandHandler(
            new SynchronizeWithServerThreadTask(
               () -> {
                  try {
                     String context = this.passwordStore.a().loadPrimaryPasswordHashVerifier().loadMessage(output.fetchMessage());
                     if (context == null) {
                        input.executeMessage(this.loadState() ? "§cNenhum spawn foi definido ainda." : "§cNo spawn has been defined yet.");
                        return;
                     }

                     Location data = VerifiedLoginGuard.resolveLocation(context);
                     if (data == null) {
                        input.executeMessage(this.loadState() ? "§cFalhou ao decodificar a localização." : "§cFailed to decode spawn location.");
                        return;
                     }

                     Player value = target.findObject();
                     if (BungeeWriter.fetchState()) {
                        value.teleportAsync(data, TeleportCause.PLUGIN);
                     } else {
                        value.teleport(data, TeleportCause.PLUGIN);
                     }

                     input.executeMessage(this.loadState() ? "§aVocê foi teleportado para o spawn." : "§aYou have been teleported to the spawn.");
                  } catch (Exception result) {
                     PasswordHashContainer.handleMessage("Failed to teleport to spawn: " + result.getLocalizedMessage(), result);
                     input.executeMessage(
                        this.loadState()
                           ? "§cDesculpe, mas não foi possível teleportar para o spawn."
                           : "§cSorry, but it was not possible to teleport to the spawn."
                     );
                  }
               }
            )
         );
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (!(target instanceof VerifiedServerAdapter)) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.TOP_LOUDPROXYSTATE);
      } else {
         VerifiedServerAdapter output = (VerifiedServerAdapter)target;
         LimboCoordinator context = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
         SecondaryAccountHandler data = context.getSecondaryAccountHandler();
         if (input.length != 2 && input.length != 3) {
            String item = this.loadState() ? "tipo" : "type";
            String content = "spawn";
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target, " §8⋆ §7/nlogin " + content + " set <" + item + "> §f" + (this.loadState() ? "Define um spawn." : "Sets a spawn.")
            );
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target, " §8⋆ §7/nlogin " + content + " unset <" + item + "> §f" + (this.loadState() ? "Remove um spawn." : "Unsets a spawn.")
            );
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target, " §8⋆ §7/nlogin " + content + " teleport <" + item + "> §f" + (this.loadState() ? "Teleporta a um spawn." : "Teleports to a spawn.")
            );
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target, " §8⋆ §7/nlogin " + content + " list §f" + (this.loadState() ? "Lista os tipos de spawn." : "Lists the spawn types.")
            );
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
         } else {
            String value = input[1].toLowerCase(Locale.ENGLISH);
            if (input.length == 2) {
               if (value.equals("list")) {
                  if (this.passwordStore.loadState()) {
                     SecondarySenderAdapter element = this.passwordStore.findObject();
                     element.getPasswordHashAdapter()
                        .updateVerifiedServerAdapter(output, 3, "action", 4, "message.render-spawn-list", this.lenientPremiumOption.activeName);
                  } else {
                     updatePasswordStore(this.passwordStore, data, this.lenientPremiumOption);
                  }
               } else if (entries.contains(value)) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(
                     target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin spawn " + value + " <" + (this.loadState() ? "tipo" : "type") + ">"
                  );
               } else {
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, this.loadState() ? "§cSub-comando desconhecido." : "§cUnknown subcommand.");
               }
            } else {
               IndirectLoginKind result = IndirectLoginKind.handleIndirectLoginKind(input[2].toLowerCase(Locale.ENGLISH));
               if (result == null) {
                  String payload = String.join(", ", IndirectLoginKind.entries);
                  CachedSettingsGateway.handleOutgoingSenderAdapter(
                     target,
                     this.loadState()
                        ? "§cEste tipo de spawn não existe. Valores válidos: <" + payload + ">"
                        : "§cThis type of spawn does not exist. Valid spawns: <" + payload + ">"
                  );
               } else {
                  PrimaryPasswordHashVerifier request = this.passwordStore.a().loadPrimaryPasswordHashVerifier();
                  switch (value) {
                     case "set":
                        if (this.passwordStore.loadState()) {
                           SecondarySenderAdapter subject = this.passwordStore.findObject();
                           if (!subject.canState(output, context)) {
                              data.executeMessage(
                                 this.loadState()
                                    ? "§cVocê deve estar em um servidor de autenticação para rodar este comando."
                                    : "§cYou must be on an authentication server to run this command."
                              );
                              return;
                           }

                           String setting = result != IndirectLoginKind.MAIN_INDIRECTLOGINKIND
                              ? "§cUnexpected error, this is a bug! Please report to /nlogin support"
                              : String.format(
                                 this.loadState()
                                    ? "§cVocê precisa definir um spawn %s para poder usar o recurso de última localização."
                                    : "§cYou need to set a %s spawn to use the last location feature.",
                                 IndirectLoginKind.INDIRECT_LOGIN_KIND.activeName
                              );
                           subject.getPasswordHashAdapter()
                              .updateVerifiedServerAdapter(
                                 output,
                                 3,
                                 "type",
                                 result.name,
                                 "action",
                                 0,
                                 "message.render-spawn-list",
                                 this.lenientPremiumOption.activeName,
                                 "message.success",
                                 this.loadState() ? "§aA localização foi definida com sucesso." : "§aLocation has been set successfully.",
                                 "message.error",
                                 setting
                              );
                        } else {
                           if (result == IndirectLoginKind.MAIN_INDIRECTLOGINKIND) {
                              request.createPrimaryPasswordHashVerifier(result.fetchMessage(), true).sendTask();
                           } else {
                              request.resolvePrimaryPasswordHashVerifier(result.fetchMessage(), VerifiedLoginGuard.createMessage(((Player)target.c()).getLocation()))
                                 .sendTask();
                           }

                           updatePasswordStore(this.passwordStore, data, this.lenientPremiumOption);
                           if (result == IndirectLoginKind.MAIN_INDIRECTLOGINKIND && !request.hasState(IndirectLoginKind.INDIRECT_LOGIN_KIND.fetchMessage())) {
                              data.executeMessage(
                                 String.format(
                                    this.loadState()
                                       ? "§cVocê precisa definir um spawn %s para poder usar o recurso de última localização."
                                       : "§cYou need to set a %s spawn to use the last location feature.",
                                    IndirectLoginKind.INDIRECT_LOGIN_KIND.activeName
                                 )
                              );
                              CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                           } else {
                              CachedSettingsGateway.handleOutgoingSenderAdapter(
                                 target, this.loadState() ? "§aA localização foi definida com sucesso." : "§aLocation has been set successfully."
                              );
                              CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, currentFactor, pendingFactor);
                           }

                           BusyLimboStore option = (BusyLimboStore)this.passwordStore.findSettingsLinker();
                           option.loadSpawnSession().processPasswordStore(this.passwordStore, false);
                        }
                        break;
                     case "unset":
                        if (this.passwordStore.loadState()) {
                           SecondarySenderAdapter holder = this.passwordStore.findObject();
                           if (!holder.canState(output, context)) {
                              data.executeMessage(
                                 this.loadState()
                                    ? "§cVocê deve estar em um servidor de autenticação para rodar este comando."
                                    : "§cYou must be on an authentication server to run this command."
                              );
                              return;
                           }

                           holder.getPasswordHashAdapter()
                              .updateVerifiedServerAdapter(
                                 output,
                                 3,
                                 "type",
                                 result.name,
                                 "action",
                                 1,
                                 "message.render-spawn-list",
                                 this.lenientPremiumOption.activeName,
                                 "message.success",
                                 this.loadState() ? "§cVocê deletou a localização do spawn." : "§cYou have deleted the spawn location.",
                                 "message.not-set",
                                 this.loadState() ? "§cNenhum spawn foi definido ainda." : "§cThis spawn has not been defined yet."
                              );
                        } else {
                           String reference = request.loadMessage(result.fetchMessage());
                           if (reference != null) {
                              if (result == IndirectLoginKind.MAIN_INDIRECTLOGINKIND) {
                                 request.createPrimaryPasswordHashVerifier(result.fetchMessage(), false).sendTask();
                              } else {
                                 request.createPrimaryPasswordHashVerifier(result.fetchMessage()).sendTask();
                              }

                              updatePasswordStore(this.passwordStore, data, this.lenientPremiumOption);
                              CachedSettingsGateway.handleOutgoingSenderAdapter(
                                 target, this.loadState() ? "§cVocê deletou a localização do spawn." : "§cYou have deleted the spawn location."
                              );
                              CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
                              BusyLimboStore record = (BusyLimboStore)this.passwordStore.findSettingsLinker();
                              record.loadSpawnSession().processPasswordStore(this.passwordStore, false);
                           } else {
                              CachedSettingsGateway.handleOutgoingSenderAdapter(
                                 target, this.loadState() ? "§cNenhum spawn foi definido ainda." : "§cThis spawn has not been defined yet."
                              );
                              CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                           }
                        }
                        break;
                     case "teleport":
                     case "tp":
                        if (result == IndirectLoginKind.MAIN_INDIRECTLOGINKIND) {
                           data.executeMessage(
                              this.loadState() ? "Você não pode teleportar para esse tipo de spawn." : "You cannot teleport to this type of spawn."
                           );
                           return;
                        }

                        if (this.passwordStore.loadState()) {
                           SecondarySenderAdapter entry = this.passwordStore.findObject();
                           if (!entry.canState(output, context)) {
                              data.executeMessage(
                                 this.loadState()
                                    ? "§cVocê deve estar em um servidor de autenticação para rodar este comando."
                                    : "§cYou must be on an authentication server to run this command."
                              );
                              return;
                           }

                           entry.getPasswordHashAdapter()
                              .updateVerifiedServerAdapter(
                                 output,
                                 3,
                                 "type",
                                 result.name,
                                 "action",
                                 2,
                                 "message.success",
                                 this.loadState() ? "§aVocê foi teleportado para o spawn." : "§aYou have been teleported to the spawn.",
                                 "message.not-set",
                                 this.loadState() ? "§cNenhum spawn foi definido ainda." : "§cThis spawn has not been defined yet.",
                                 "message.decode-error",
                                 this.loadState() ? "§cFalhou ao decodificar a localização." : "§cFailed to decode spawn location.",
                                 "message.unknown-error",
                                 this.loadState()
                                    ? "§cDesculpe, mas não foi possível teleportar para o spawn."
                                    : "§cSorry, but it was not possible to teleport to the spawn."
                              );
                        } else {
                           this.updateVerifiedServerAdapter(output, data, result);
                        }
                  }
               }
            }
         }
      }
   }

   public PurgeUnregisterCommand(PasswordStore target) {
      super(target, "spawn", "nlogin.admin", false, false);
   }
}
