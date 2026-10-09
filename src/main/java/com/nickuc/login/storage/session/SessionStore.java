package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.PremiumState;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;


public class SessionStore implements UpstreamAccountHandler {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public boolean at() {
      return true;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (!QuickPremiumOption.PENDING_QUICKPREMIUMOPTION.retrieveState()) {
         return false;
      }

      if (!QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()) {
         return false;
      }

      if (QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
         return false;
      }

      SpawnLookup context = output.loadSpawnLookup();
      return !context.fetchState() || !context.fetchStateAndState() && context.retrieveStateForState();
   }

   public SessionStore(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public boolean canState(PasswordStore target) {
      return true;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      switch (data) {
         case ACTIVE_PRIVATELOGINOPTION:
            SpawnLookup result = output.loadSpawnLookup();
            if (result.fetchStateAndState()) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.ACTIVE_PRIMARY_LOUDPROXYSTATE);
               CachedSettingsGateway.processOutgoingSenderAdapter(input, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               return;
            }

            PasswordGateway.processPasswordStore(target, output.loadMessage(), input.retrieveInetSocketAddress().getAddress(), PremiumState.PENDING_PREMIUMSTATE);
            if (result.retrieveStateForState()) {
               result.dispatchTaskForValue();
               target.findIndirectPasswordResolver().isState(result, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
            }

            output.loadObject(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
            input.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.UPSTREAM_LOUDPROXYSTATE, input));
            break;
         case PENDING_PRIVATELOGINOPTION:
            SpawnLookup value = output.loadSpawnLookup();
            if (value.retrieveStateForState()) {
               value.dispatchTaskForValue();
               target.findIndirectPasswordResolver().isState(value, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
            }

            UpstreamAccountHandler.super.a(target, input, output, context, data);
      }
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.ACTIVE_READY_LOUDPROXYSTATE);
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.ACTIVE_PRIVATELOGINOPTION, PrivateLoginOption.PENDING_PRIVATELOGINOPTION);
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }
}
