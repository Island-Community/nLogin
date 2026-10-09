package com.nickuc.login.discord;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.LocalLoginBarrier;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.command.setup.DeletePurgeCommand;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.StrictPlatformCatalog;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.session.LocalSessionTable;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import java.io.File;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class StrictDiscordNotifier extends LoginSource {
   private void sendOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String output, Consumer<Boolean> context) {
      if (!this.passwordStore
         .b()
         .canState(
            this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.PENDING_SILENTPROXYSTATE
               ? input.toLowerCase(Locale.ENGLISH)
               : input
         )) {
         File data = new File(this.passwordStore.resolveFile().getParentFile(), input + ".jar");
         LocalLoginBarrier value = PendingPasswordHashHasher.getPendingPasswordHashHasher().loadLocalLoginBarrier(output, data);
         context.accept(value.findCount() == 200 && value.retrieveState());
      } else {
         CachedSettingsGateway.handleOutgoingSenderAdapter(
            target, this.loadState() ? "§cVocê já tem este plugin instalado no seu servidor." : "§cYou already have this plugin installed on your server."
         );
      }
   }

   public StrictDiscordNotifier(PasswordStore target) {
      super(target, "install", "nlogin.admin", false, true);
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 2) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " <dependency>"
         );
      } else {
         if (target instanceof VerifiedServerAdapter) {
            VerifiedServerAdapter output = (VerifiedServerAdapter)target;
            if (!this.passwordStore.loadLimboRegistry().canState(output)) {
               LimboCoordinator context = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
               LocalSessionTable data = (LocalSessionTable)context.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
               if (data == null) {
                  return;
               }

               if (!(data.getLoudPlayerContract() instanceof DeletePurgeCommand)) {
                  return;
               }
            }
         }

         BiConsumer source = (inputValue, outputValue) -> {
            if (this.loadState()) {
               if (inputValue) {
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §7A instalação foi realizada com sucesso!");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §7O plugin já estará em funcionamento no próximo restart.");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §eEm caso de problemas, contate nossa equipe:");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §bhttps://www.nickuc.com/discord");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
               } else {
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §aA instalação não pôde ser realizada!");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §7Instale manualmente a partir do link abaixo:");
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §b" + outputValue);
                  CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
               }
            } else if (inputValue) {
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §7The installation was successful!");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §7The plugin will be up and running in the next restart.");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §eIn case of problems, please contact our team:");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §bhttps://www.nickuc.com/discord");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
            } else {
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §aThe installation could not be completed!");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §7Please install manually from the link below:");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §b" + outputValue);
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
            }
         };
         StrictPlatformCatalog entry = StrictPlatformCatalog.createStrictPlatformCatalog(CachedSettingsGateway.loadLenientPremiumOption().activeName);
         String record = entry != null ? "&lang=" + entry.getMessage() : "";
         String value = input[1].toLowerCase(Locale.ENGLISH);
         switch (value) {
            case "nantibot":
               CachedSettingsGateway.handleOutgoingSenderAdapter(
                  target, this.loadState() ? "§aIniciando a instalação do plugin nAntiBot..." : "§aStarting the installation of the nAntiBot plugin..."
               );
               String item = "https://repo.nickuc.com/download?name=nAntiBot" + record;
               this.sendOutgoingSenderAdapter(target, "nAntiBot", item, inputValue -> source.accept(inputValue, item));
               break;
            case "nchat":
               if (this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() != SilentProxyState.SILENT_PROXY_STATE) {
                  CachedSettingsGateway.handleOutgoingSenderAdapter(
                     target,
                     this.loadState()
                        ? "§cNão foi possível instalar o plugin: incompatível com a platforma atual."
                        : "§cUnable to install plugin: incompatible with the current platform."
                  );
                  return;
               }

               CachedSettingsGateway.handleOutgoingSenderAdapter(
                  target, this.loadState() ? "§aIniciando a instalação do plugin nChat..." : "§aStarting the installation of the nChat plugin..."
               );
               String response = "https://repo.nickuc.com/download?name=nChat" + record;
               this.sendOutgoingSenderAdapter(target, "nChat", response, outputValue -> {
                  if (outputValue) {
                     PluginManager contextValue = ((Server)this.passwordStore.b().c()).getPluginManager();
                     Plugin dataValue = contextValue.getPlugin("Legendchat");
                     if (dataValue != null && !"1.0".equals(dataValue.getDescription().getVersion())) {
                        File valueValue = MessageProcessor.handleFile(dataValue.getClass());
                        if (!valueValue.delete()) {
                           valueValue.deleteOnExit();
                        }
                     }

                     Plugin request = contextValue.getPlugin("UltimateChat");
                     if (request != null) {
                        File result = MessageProcessor.handleFile(request.getClass());
                        if (!result.delete()) {
                           result.deleteOnExit();
                        }
                     }
                  }

                  source.accept(outputValue, response);
               });
         }
      }
   }
}
