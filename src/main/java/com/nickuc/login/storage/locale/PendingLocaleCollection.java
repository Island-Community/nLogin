package com.nickuc.login.storage.locale;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PendingLocaleCollection extends LoginSource {
   private String loadMessage(boolean target) {
      return target ? "§7" : "§f";
   }

   private void sendOutgoingSenderAdapter(OutgoingSenderAdapter target) {
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
      if (target instanceof VerifiedServerAdapter) {
         VerifiedServerAdapter input = (VerifiedServerAdapter)target;
         LimboCoordinator output = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(input);
         SecondaryAccountHandler context = output.getSecondaryAccountHandler();
         int data = 1;

         for (MessageOption result : MessageOption.findCollection()) {
            PasswordHashRepository request = result.retrievePasswordHashRepository();
            if (request != null && request.findState() && !result.resolveState()) {
               String response = this.loadMessage((boolean)data);
               context.executeMessage(
                  " §8⋆ " + response + result.getName(),
                  this.loadState() ? "§7Clique aqui para converter os dados deste plugin." : "§7Click here to convert the data for this plugin.",
                  "nlogin converter " + result.getName().toLowerCase(Locale.ENGLISH)
               );
               data = data == 0 ? 1 : 0;
            }
         }

         context.executeMessage("");
         String item = this.loadMessage((boolean)data);
         data = data == 0 ? 1 : 0;
         String element = this.loadMessage((boolean)data);
         if (this.loadState()) {
            String content = "§7Clique aqui para converter os dados para esta database.";
            context.executeMessage(" §8⋆ " + item + "MySQL §3» " + item + "SQLite", content, "nlogin converter mysqltosqlite");
            context.executeMessage(" §8⋆ " + element + "SQLite §3» " + element + "MySQL", content, "nlogin converter sqlitetomysql");
         } else {
            String payload = "§7Click here to convert the data to this database.";
            context.executeMessage(" §8⋆ " + item + "MySQL §3» " + item + "SQLite", payload, "nlogin converter mysqltosqlite");
            context.executeMessage(" §8⋆ " + element + "SQLite §3» " + element + "MySQL", payload, "nlogin converter sqlitetomysql");
         }
      } else {
         for (MessageOption entry : MessageOption.findCollection()) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §8⋆ §7/nlogin converter " + entry.getName().toLowerCase(Locale.ENGLISH));
         }
      }

      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
   }

   public PendingLocaleCollection(PasswordStore target) {
      super(target, "migrate", "nlogin.admin", false, false, "converter");
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 2) {
         this.sendOutgoingSenderAdapter(target);
      } else {
         String output = input[1].toUpperCase(Locale.ENGLISH);
         MessageOption context = MessageOption.processMessageOption(output);
         if (context == null) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               target, this.loadState() ? "§cEste tipo de conversão não existe." : "§eThis type of conversion does not exist."
            );
         } else {
            PasswordHashRepository data = context.retrievePasswordHashRepository();
            if (data == null) {
               CachedSettingsGateway.handleOutgoingSenderAdapter(
                  target,
                  this.loadState()
                     ? "§cOps, parece que não foi possível encontrar a instância para essa conversão."
                     : "§cOops, it looks like we couldn't find the instance for this conversion."
               );
            } else if (!data.findState()) {
               CachedSettingsGateway.handleOutgoingSenderAdapter(
                  target,
                  this.loadState()
                     ? "§cDesculpe, mas essa conversão não está disponível para o software atual."
                     : "§cSorry, but this conversion is not available for current software."
               );
            } else {
               DirectNoticeCatalog value = this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract().resolveDirectNoticeCatalog();
               if (context != MessageOption.ACTIVE_UPSTREAM_MESSAGEOPTION
                  || value != DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG && value != DirectNoticeCatalog.DIRECT_NOTICE_CATALOG) {
                  if (context == MessageOption.ACTIVE_INTERNAL_MESSAGEOPTION && value == DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG) {
                     if (this.loadState()) {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cPara fazer a conversão, é necessário mudar para a database MySQL.");
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§7É necessaŕio reiniciar o servidor.");
                     } else {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cTo do the conversion, it is necessary to switch to the MySQL database.");
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§7You need to restart the server after that.");
                     }
                  } else {
                     if (target instanceof VerifiedServerAdapter) {
                        ((VerifiedServerAdapter)target)
                           .saveMessage(
                              "",
                              this.loadState() ? "§eIniciando a conversão " + context.getName() + "..." : "§eStarting the " + context.getName() + " conversion...",
                              0,
                              20,
                              10
                           );
                     } else {
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target, this.loadState() ? "§eIniciando a conversão " + context.getName() + "..." : "§eStarting the " + context.getName() + " conversion..."
                        );
                     }

                     this.passwordStore
                        .processLinkedSessionHandler(false)
                        .loadStrictCommandHandler(() -> data.executeOutgoingSenderAdapter(target), 1500L, TimeUnit.MILLISECONDS);
                  }
               } else {
                  CachedSettingsGateway.handleOutgoingSenderAdapter(
                     target,
                     this.loadState()
                        ? "§cPara fazer a conversão, é necessário mudar para a database SQLite."
                        : "§cTo do the conversion, it is necessary to switch to the SQLite database."
                  );
               }
            }
         }
      }
   }
}
