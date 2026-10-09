package com.nickuc.login.storage.locale;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.CachedLoginBarrier;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.ParentSettingsLookup;
import com.nickuc.login.security.hashing.PrimaryPasswordHashVerifier;
import com.nickuc.login.updater.AuthenticatedNoticeKind;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocaleSource extends LoginSource {
   private static float factor = Float.intBitsToFloat(1097859072);
   private final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
   private static float activeFactor = Float.intBitsToFloat(1077936128);
   private static float pendingFactor = Float.intBitsToFloat(1097859072);
   private static float currentFactor = Float.intBitsToFloat(1077936128);

   public LocaleSource(PasswordStore target) {
      super(target, "update", "nlogin.admin", false, true);
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      String output = this.findMessage().toLowerCase(Locale.ENGLISH);
      if (input.length != 2 && input.length != 3) {
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
         CachedSettingsGateway.handleOutgoingSenderAdapter(
            target,
            " §8⋆ §7/nlogin "
               + output
               + " channel <stable/latest/dev> §f"
               + (this.loadState() ? "Altera o canal de atualizações." : "Changes the update channel.")
         );
         CachedSettingsGateway.handleOutgoingSenderAdapter(
            target, " §8⋆ §7/nlogin " + output + " confirm §f" + (this.loadState() ? "Confirma atualizações pendentes." : "Confirm pending updates.")
         );
         CachedSettingsGateway.handleOutgoingSenderAdapter(
            target,
            " §8⋆ §7/nlogin " + output + " auto §f" + (this.loadState() ? "Ativa/desativa as atualizações automáticas." : "Enable/disable automatic updates.")
         );
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
      } else {
         String context = input[1].toLowerCase(Locale.ENGLISH);
         switch (context) {
            case "channel":
               if (input.length == 2) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(
                     target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + output + " " + context + " <stable/dev>"
                  );
               } else {
                  AuthenticatedNoticeKind item = AuthenticatedNoticeKind.processAuthenticatedNoticeKind(input[2]);
                  if (item == null) {
                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target, this.loadState() ? "§cEste canal de atualizações não existe." : "§cThis update channel does not exist."
                     );
                  } else {
                     this.passwordStore.a().updateAuthenticatedNoticeKind(item);
                     if (item == AuthenticatedNoticeKind.AUTHENTICATED_NOTICE_KIND) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target, this.loadState() ? "§7Você selecionou o §acanal estável§7." : "§7You have selected the §astable channel§7."
                        );
                     } else {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target, this.loadState() ? "§7Você selecionou o §bcanal de última versão§7." : "§7You have selected the §blatest version channel§7."
                        );
                     }
                  }
               }
               break;
            case "confirm":
               if (this.atomicBoolean.getAndSet(true)) {
                  try {
                     ParentSettingsLookup record = this.passwordStore.a();
                     CachedLoginBarrier element = record.findCachedLoginBarrier();
                     if (element.getState() || !this.passwordStore.a().loadLinkedPasswordHashVerifier().findState()) {
                        this.passwordStore.a().processTask();
                     }

                     if (this.passwordStore.a().loadLinkedPasswordHashVerifier().findState()) {
                        if (!element.getState()) {
                           CachedSettingsGateway.handleOutgoingSenderAdapter(
                              target,
                              this.loadState()
                                 ? "§aAtualização confirmada com sucesso. A nova versão do nLogin será instalada no próximo restart."
                                 : "§aUpdate confirmed successfully. The new version of nLogin will be installed at the next restart."
                           );
                           CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, currentFactor);
                           element.dispatchState(true);
                        } else {
                           if (target instanceof VerifiedServerAdapter) {
                              ((VerifiedServerAdapter)target)
                                 .saveMessage(
                                    "",
                                    this.loadState() ? "§eReinicie o servidor para aplicar o update." : "§eRestart the server to apply the update.",
                                    0,
                                    30,
                                    6
                                 );
                              CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, pendingFactor, activeFactor);
                           } else {
                              CachedSettingsGateway.handleOutgoingSenderAdapter(
                                 target, this.loadState() ? "§eReinicie o servidor para aplicar o update." : "§eRestart the server to apply the update."
                              );
                           }

                           element.dispatchState(true);
                        }
                     } else {
                        if (target instanceof VerifiedServerAdapter) {
                           ((VerifiedServerAdapter)target)
                              .saveMessage("", this.loadState() ? "§aO plugin está atualizado." : "§aThe plugin is updated.", 0, 30, 6);
                        }

                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, this.loadState() ? "§aO plugin está atualizado." : "§aThe plugin is updated");
                     }
                  } finally {
                     this.atomicBoolean.set(false);
                  }
               }
               break;
            case "auto":
               ParentSettingsLookup result = this.passwordStore.a();
               PrimaryPasswordHashVerifier request = result.loadPrimaryPasswordHashVerifier();
               boolean response = request.hasState("autoUpdate", true);
               if (response) {
                  CachedSettingsGateway.handleOutgoingSenderAdapter(
                     target, this.loadState() ? "§cVocê desativou as atualizações automáticas." : "§cYou have turned off automatic updates."
                  );
               } else {
                  CachedSettingsGateway.handleOutgoingSenderAdapter(
                     target, this.loadState() ? "§aVocê ativou as atualizações automáticas." : "§aYou have turned on automatic updates."
                  );
               }

               request.createPrimaryPasswordHashVerifier("autoUpdate", !response);
         }
      }
   }
}
