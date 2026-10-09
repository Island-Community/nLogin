package com.nickuc.login.premium;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.auth.locale.LocaleFlow;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginService;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.PremiumState;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.packet.LoudPacketAdapter;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.spawn.PremiumOption;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import com.nickuc.login.tasks.LoginMainQueueTask;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;

import org.geysermc.floodgate.api.player.FloodgatePlayer;


public abstract class FloodgateResolver {
   public final PasswordStore passwordStore;

   @Nullable
   public String handleMessage(VerifiedServerAdapter target, String input) {
      long output = System.nanoTime();

      try {
         if (input.isEmpty()) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cnLogin: cannot handle an empty message!");
            return null;
         }

         String[] data = input.split(" ");
         if (data.length == 0) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cnLogin: cannot handle an empty message: parts cannot be zero!");
            return null;
         }

         String value = data[0].toLowerCase(Locale.ENGLISH);
         if (this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() != SilentProxyState.PENDING_SILENTPROXYSTATE
            && (value.equals("/plugman") || value.equals("/system") || input.contains("atlas"))
            && input.contains("nlogin")) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cYou cannot control the authentication plugin from here.");
            return null;
         }

         if (!this.passwordStore.loadLimboRegistry().canState(target)) {
            boolean result;
            if (!this.passwordStore.getState()) {
               result = this.passwordStore.findIndirectPasswordHashVerifier().validateState(value);
            } else {
               List request = SpawnState.PENDING_UPSTREAM_SPAWNSTATE.a(new Object[0]);
               result = request.stream().noneMatch(targetValue -> !targetValue.isEmpty() && (targetValue.equals("*") || value.equals(targetValue)));
            }

            if (result) {
               return null;
            }
         }

         if (!this.passwordStore.getState()
            && (value.startsWith("/nlogin:") || this.passwordStore.findIndirectPasswordHashVerifier().resolveSet().stream().anyMatch(value::equals))
            && !input.equals(value)) {
            data = Arrays.copyOfRange(data, 1, data.length);
            this.passwordStore.loadLimboRegistry().loadLimboCoordinator(target).updateLenientMessageKind(LenientMessageKind.STORED_LENIENTMESSAGEKIND, data);
            return value;
         } else {
            return input;
         }
      } catch (Throwable record) {
         PasswordHashContainer.handleMessage("Severe error during chat command handler (" + target.getName() + ")", record);
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§4[nLogin] Chat command internal error detected. Please report to an admin.");
         return null;
      } finally {
         PrimaryLoginService.savePremiumOption(PremiumOption.MAIN_PREMIUMOPTION, output);
      }
   }

   public void performVerifiedServerAdapter(VerifiedServerAdapter target) {
      LoginMainQueueTask.saveVerifiedServerAdapter(target);
      LimboCoordinator input = this.passwordStore.loadLimboRegistry().buildLimboCoordinator(target);
      if (input != null) {
         SpawnLookup output = input.loadSpawnLookup();
         if (output.resolveStateForState()) {
            ArrayList context = new ArrayList();
            if (input.loadTightPlatformCatalog().isState(TightPlatformCatalog.LOCAL_TIGHTPLATFORMCATALOG)) {
               if (this.passwordStore.loadState()) {
                  SecondarySenderAdapter data = this.passwordStore.findObject();
                  String value = data.handleMessage(target);
                  if (value != null) {
                     output.resolveCachedPasswordHashHasher().updateMessage("last-server", value);
                  }
               }

               output.sendTask();
               context.add(OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE);
            }

            if (input.d(LenientMessageKind.READY_LENIENTMESSAGEKIND)) {
               output.resolveCachedPasswordHashHasher().updateMessage("force-invalid-session", true);
            }

            LenientPremiumOption result = input.fetchLenientPremiumOption();
            if (result != null && input.loadTightPlatformCatalog().isState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)) {
               output.resolveCachedPasswordHashHasher().updateMessage("language", result.primaryName);
            }

            if (output.resolveCachedPasswordHashHasher().getState()) {
               context.add(OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
            }

            if (!context.isEmpty()) {
               this.passwordStore
                  .processLinkedSessionHandler(true)
                  .buildStrictCommandHandler(() -> this.passwordStore.findIndirectPasswordResolver().isState(output, context.toArray(new OutgoingSpawnState[0])));
            }
         }
      }
   }

   public FloodgateResolver(PasswordStore target) {
      this.passwordStore = target;
   }

   public void processVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input) {
      if (!input.isState(LenientMessageKind.CACHED_LENIENTMESSAGEKIND)) {
         String output = target.loadOptional().orElse(null);
         SpawnLookup context = (SpawnLookup)input.d(LenientMessageKind.LENIENT_MESSAGE_KIND);
         if (context != null && (output == null || "en_us".equalsIgnoreCase(output))) {
            output = context.resolveCachedPasswordHashHasher().resolveObject("language", output);
         }

         if (output != null) {
            LenientPremiumOption data = LenientPremiumOption.buildLenientPremiumOption(output);
            if (data != null) {
               input.updateLenientMessageKind(LenientMessageKind.CACHED_LENIENTMESSAGEKIND, data);
            }
         }
      }

      this.passwordStore.findSettingsLinker().retrievePacketCoordinator().handleVerifiedServerAdapter(target, input);
   }

   @Nullable
   public String resolveMessage(String target, InetAddress input, @Nullable Boolean output) {
      long context = System.nanoTime();

      try {
         if (this.passwordStore.findState()) {
            return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_PRIVATE_LOUDPROXYSTATE);
         }

         if (this.passwordStore.loadLimboRegistry().canState(target, null)) {
            return OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§cUnrestricted name for plugins and/or mods.", "", "§ePlease select another nickname.");
         }

         if (QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.getState()) {
            UpdateLookup value = this.passwordStore.a();
            int result = value.getCount();
            switch (result) {
               case 0:
               case 1:
               case 9:
               case 23:
                  break;
               case 8:
               default:
                  return CachedSettingsGateway.loadState()
                     ? OpenLocaleBarrier.loadMessage(
                        "§4[nLogin] Um problema de licenciamento foi detectado.",
                        "",
                        "§cA licença atual foi desvinculada deste servidor.",
                        "",
                        "§cSe isso for um erro, por favor acesse o painel, desvincule a licença,",
                        "§cbaixe a JAR premium e instale no servidor novamente.",
                        "",
                        "§eCaso queira remover a licença deste servidor, digite §f\"nloginc unlink\" §eno console."
                     )
                     : OpenLocaleBarrier.loadMessage(
                        "§4[nLogin] A licensing problem has been detected.",
                        "",
                        "§cThe current license was unlinked from this server.",
                        "",
                        "§cIf this is an error, please go to the panel, unlink the license,",
                        "§cdownload the premium JAR and install it on the server again.",
                        "",
                        "§eIf you want to remove the license from this server, type §f\"nloginc unlink\" §ein the console."
                     );
               case 15:
                  return CachedSettingsGateway.loadState()
                     ? OpenLocaleBarrier.loadMessage(
                        "§4[nLogin] Um problema de licenciamento foi detectado.",
                        "",
                        "§cA licença atual atingiu o número máximo de servidores simultâneos ativos.",
                        "",
                        "§cSe isso for um erro, por favor acesse o painel, desvincule a licença,",
                        "§cbaixe a JAR premium e instale no servidor novamente.",
                        "",
                        "§eCaso queira remover a licença deste servidor, digite §f\"nloginc unlink\" §eno console."
                     )
                     : OpenLocaleBarrier.loadMessage(
                        "§4[nLogin] A licensing problem has been detected.",
                        "",
                        "§cThe current license has reached the maximum number of concurrent active servers.",
                        "",
                        "§cIf this is an error, please go to the panel, unlink the license,",
                        "§cdownload the premium JAR and install it on the server again.",
                        "",
                        "§eIf you want to remove the license from this server, type §f\"nloginc unlink\" §ein the console."
                     );
            }
         }

         SecondaryConnectionContract payload = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
         ReadyBedrockResolver holder = payload instanceof ReadyBedrockResolver ? (ReadyBedrockResolver)payload : null;
         int request = output != null
               && holder != null
               && holder.findCount() > 0
               && target.toLowerCase(Locale.ENGLISH).startsWith(holder.fetchMessage().toLowerCase(Locale.ENGLISH))
            ? 1
            : 0;
         if (Pbkdf2Linker.resolvePattern().matcher(target).matches()
            || request != 0 && output && Pbkdf2Linker.resolvePattern().matcher(target.substring(holder.findCount())).matches()) {
            if (request != 0 && !output) {
               return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_OUTGOING_LOUDPROXYSTATE, target);
            } else {
               PremiumState response = PasswordGateway.loadPremiumState(target, input);
               if (response == PremiumState.PRIMARY_PREMIUMSTATE) {
                  PasswordGateway.processPasswordStore(this.passwordStore, target, input, PremiumState.PREMIUM_STATE);
                  return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_TOP_LOUDPROXYSTATE, target);
               } else {
                  return null;
               }
            }
         } else {
            return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_INCOMING_LOUDPROXYSTATE);
         }
      } catch (Throwable element) {
         PasswordHashContainer.handleMessage("Severe error during pre-login handler (" + target + ")", element);
         return "§4[nLogin] Severe internal error detected. Please report to an admin.";
      } finally {
         PrimaryLoginService.savePremiumOption(PremiumOption.ACTIVE_PREMIUMOPTION, context);
      }
   }

   @Nullable
   public String createMessage(
      VerifiedServerAdapter target, SpawnLookup input, String output, InetSocketAddress context, boolean data, boolean value, LoudPacketAdapter result
   ) {
      String request = target.getName();
      if (input.fetchStateAndState() && data == 0) {
         UpdateLookup response = this.passwordStore.a();
         int source = response.getCount();
         if (QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState() || !input.retrieveState() && (source == 9 || source == 1 || source == 23)) {
            byte entry = 0;
            SecondaryConnectionContract record = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
            if (record instanceof ReadyBedrockResolver) {
               ReadyBedrockResolver item = (ReadyBedrockResolver)record;
               FloodgatePlayer element = item.computeFloodgatePlayer(output, context.getAddress().getHostAddress());
               if (element != null && element.isLinked()) {
                  // Linked check via FloodgatePlayer (2.x)
                  // Previously used LinkedPlayer; now uses direct Java ID
                  if (content.getJavaUniqueId() != null && content.getJavaUniqueId().equals(input.getMojangId())) {
                     data = 1;
                     entry = 1;
                  }
               }
            }

            if (entry == 0) {
               if (value) {
                  return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_SECONDARY_LOUDPROXYSTATE, output);
               }

               PasswordHashContainer.updateMessage("Invalid premium state for " + request + ": encryption was requested, but the connection is not encrypted!");
               PasswordHashContainer.updateMessage("This error is usually caused by other plugins or by the software the server is running.");
               return OpenLocaleBarrier.loadMessage(
                  "§4[nLogin] An invalid state was detected:",
                  "",
                  "§cYour connection is not encrypted, please contact an administrator.",
                  "",
                  "§eThis error is usually caused by other plugins or by the software the server is running."
               );
            }
         }
      }

      SecondaryConnectionContract payload = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
      if (!value || !payload.fetchState() && QuickPremiumOption.SHARED_QUICKPREMIUMOPTION.retrieveState()) {
         String holder = input.retrieveMessage();
         if (holder != null && !value && !output.equals(holder)) {
            PasswordHashContainer.updateMessage(
               "Invalid state for " + request + ": the connecting name is not equal to last registered name! (" + output + " != " + holder + ")"
            );
            PasswordHashContainer.updateMessage("This error is usually caused by other plugins or by the software the server is running.");
            return OpenLocaleBarrier.loadMessage(
               "§4[nLogin] An invalid state was detected:",
               "",
               "§cThe connecting name is not equal to last registered name. (" + output + " != " + holder + ")",
               "",
               "§eThis error is usually caused by other plugins or by the software the server is running."
            );
         }

         UUID subject = input.getUniqueId();
         if (input.fetchState() && subject == null) {
            String property = "The unique id of the player being connected (" + request + ") was not saved to the database, but the player is registered.";
            PasswordHashContainer.updateMessage(property);
            return OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + property, "", "§ePlease contact an administrator.");
         }

         UUID setting = target.getUniqueId();
         if (subject != null && !setting.equals(subject)) {
            String attribute = "The unique id of the player being connected ("
               + request
               + ") is different from the one stored in the nLogin database ("
               + setting
               + " != "
               + subject
               + ")";
            PasswordHashContainer.updateMessage(attribute);
            return OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + attribute, "", "§ePlease contact an administrator.");
         }
      }

      String reference = context.getAddress().getHostAddress();
      Long option = input.resolveCachedPasswordHashHasher().buildObject("block-" + reference);
      if (option != null && System.currentTimeMillis() < option) {
         return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_INTERNAL_LOUDPROXYSTATE, LocaleFlow.createMessage(option));
      } else if (!input.getState() || payload != null && (value || !payload.fetchState() && input.retrieveState())) {
         this.passwordStore.loadLimboRegistry().loadLimboCoordinator(target, input, output, context, (boolean)data, value, result);
         return null;
      } else {
         return CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_OUTGOING_LOUDPROXYSTATE, request);
      }
   }

   public boolean verifyState(VerifiedServerAdapter target, @Nullable String input) {
      try {
         return !this.passwordStore.loadLimboRegistry().canState(target);
      } catch (Throwable context) {
         PasswordHashContainer.handleMessage("Severe error during chat handler (" + target.getName() + ")", context);
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§4[nLogin] Chat internal error detected. Please report to an admin.");
         return true;
      }
   }
}
