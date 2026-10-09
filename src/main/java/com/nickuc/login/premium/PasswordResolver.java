package com.nickuc.login.premium;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.enums.event.UnregisterSource;
import com.nickuc.login.api.enums.event.UpdatePasswordSource;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PasswordResolver extends LoginSource {
   private static float factor = Float.intBitsToFloat(1077936128);
   private static float activeFactor = Float.intBitsToFloat(1097859072);

   public PasswordResolver(PasswordStore target) {
      super(target, "unregister", "nlogin.command.nlogin.unregister", true, false, "unreg");
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 2) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage() + " <player>");
      } else {
         LiveLoginCheckpoint output = new LiveLoginCheckpoint();
         String context = input[1];
         IndirectPasswordResolver data = this.passwordStore.findIndirectPasswordResolver();
         SpawnLookup value = data.loadSpawnLookup(target, super.internalLoginOption, input, context);
         if (value != null) {
            if (!value.fetchState()) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
               CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
            } else {
               String result = value.retrieveMessage();
               VerifiedServerAdapter request = this.passwordStore.b().resolveVerifiedServerAdapter(result);
               UUID response = request != null ? request.getUniqueId() : value.getUniqueId();
               if (this.passwordStore.verifyState(EventEnum.UNREGISTER, request, response, value.retrieveMessage(), UnregisterSource.BY_ADMIN)) {
                  synchronized (value.object) {
                     if (!value.fetchState()) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                        CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                        return;
                     }

                     if (request != null) {
                        result = request.getName();
                     }

                     if (!data.canState(value)) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                        CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                        return;
                     }

                     PasswordHashContainer.dispatchMessage("The " + result + " player had his account unregistered by " + target.getName() + ".");

                     try {
                        this.passwordStore.verifyState(EventEnum.PASSWORD_UPDATE_EVENT, request, response, result, null, UpdatePasswordSource.BY_ADMIN);
                     } finally {
                        if (request != null) {
                           request.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.PRIVATE_LOUDPROXYSTATE, request));
                        }
                     }

                     CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, activeFactor, factor);
                     if (value.fetchStateAndState() && value.getUniqueId().equals(value.getMojangId())) {
                        if (this.loadState()) {
                           CachedSettingsGateway.handleOutgoingSenderAdapter(
                              target, "§6O jogador está usando um UUID original. Por padrão, sua conta continuará original."
                           );
                           CachedSettingsGateway.handleOutgoingSenderAdapter(
                              target, "§6Para trocar para uma conta offline, execute §f\"/premium " + result + "\" §6no console."
                           );
                           CachedSettingsGateway.handleOutgoingSenderAdapter(
                              target, "§6Para deletá-la completamente, execute §f\"/nlogin delete " + result + "\"§6."
                           );
                        } else {
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§6The player is using a premium UUID. By default, it will remain premium.");
                           CachedSettingsGateway.handleOutgoingSenderAdapter(
                              target, "§6To switch to an offline account, run §f\"/premium " + result + "\" §6in console."
                           );
                           CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§6To delete it completely, run §f\"/nlogin delete " + result + "\"§6.");
                        }

                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     }

                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target,
                        this.loadState()
                           ? "§a✔ Você desregistrou o jogador §f" + result + " §acom sucesso."
                           : "§a✔ You have successfully unregistered the player §f" + result + "§a."
                     );
                     CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                     CachedSettingsGateway.handleOutgoingSenderAdapter(
                        target, "§e⚑ This operation took §f" + output.loadMessage(TimeUnit.MILLISECONDS, 2) + "ms§e."
                     );
                  }
               }
            }
         }
      }
   }
}
