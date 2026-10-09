package com.nickuc.login.command.auth;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.auth.locale.LocaleFlow;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.locale.LocaleCollection;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.Locale;

public class ParentPasswordAction extends LocaleCollection {
   private void sendSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output, String context) {
      if (this.indirectSessionHandler.verifyState(EventEnum.WRONG_PASSWORD_EVENT, input)) {
         String data = input.getName();
         String value = input.resolveMessage();
         int result = output.a(LenientMessageKind.DIRECT_LENIENTMESSAGEKIND) + 1;
         output.updateLenientMessageKind(LenientMessageKind.DIRECT_LENIENTMESSAGEKIND, result);

         try {
            if (result < SpawnState.ACTIVE_TOP_SPAWNSTATE.r()) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.ACTIVE_AUTHENTICATED_LOUDPROXYSTATE);
               CachedSettingsGateway.updateVerifiedServerAdapter(input, SharedLoginOption.PRIMARY_SHAREDLOGINOPTION);
               CachedSettingsGateway.processOutgoingSenderAdapter(input, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               return;
            }

            Long request = this.indirectSessionHandler.findIndirectPasswordResolver().resolveLong(target, data, value);
            if (request != null) {
               input.buildCompletableFuture(
                  CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_INTERNAL_LOUDPROXYSTATE, input, LocaleFlow.createMessage(request))
               );
            } else {
               input.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_SHARED_LOUDPROXYSTATE, input));
            }
         } finally {
            PasswordHashContainer.dispatchMessage("The " + data + " (" + value + ") player entered an incorrect password");
         }
      }
   }

   @Override
   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (!(target instanceof VerifiedServerAdapter)) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin forcelogin" + (output.length > 0 ? " " : "") + String.join(" ", output)
         );
      } else {
         VerifiedServerAdapter context = (VerifiedServerAdapter)target;
         LimboCoordinator data = this.indirectSessionHandler.loadLimboRegistry().loadLimboCoordinator(context);
         if (data.loadTightPlatformCatalog().isState(TightPlatformCatalog.PRIMARY_TIGHTPLATFORMCATALOG)) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.LIVE_LOUDPROXYSTATE);
            CachedSettingsGateway.updateVerifiedServerAdapter(context, SharedLoginOption.MAIN_SHAREDLOGINOPTION);
            CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
         } else if (output.length == 0) {
            SecondaryAccountHandler response = data.getSecondaryAccountHandler();
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               context, LoudProxyState.LOUD_PROXY_STATE, inputValue -> response.dispatchMessage(inputValue, input.toLowerCase(Locale.ENGLISH) + " ")
            );
         } else {
            IndirectPasswordResolver value = this.indirectSessionHandler.findIndirectPasswordResolver();
            SpawnLookup result = data.loadSpawnLookup();
            if (!result.retrieveState()) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.INCOMING_LOUDPROXYSTATE);
               CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
            } else if (!result.fetchStateAndState() || !QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()) {
               String request = output[0];
               if (!value.checkState(result, request)) {
                  this.sendSpawnLookup(result, context, data, request);
               } else {
                  this.indirectSessionHandler.findSettingsLinker().sendSpawnLookup(result, context, data, request, true, true);
               }
            }
         }
      }
   }

   public ParentPasswordAction(InternalLoginOption target) {
      super(target);
      this.getPasswordHashCommand();
   }
}
