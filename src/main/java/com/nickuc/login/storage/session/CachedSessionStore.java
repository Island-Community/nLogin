package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.model.QuickMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;


public class CachedSessionStore implements UpstreamAccountHandler {
   private final CachedProxyCatalog cachedProxyCatalog;

   public CachedSessionStore(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public boolean at() {
      return false;
   }

   @Override
   public boolean canState(PasswordStore target) {
      return false;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (!QuickPremiumOption.PENDING_QUICKPREMIUMOPTION.retrieveState()) {
         return false;
      }

      if (!QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()) {
         return false;
      }

      if (!QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
         return false;
      }

      SpawnLookup context = output.loadSpawnLookup();
      return context.fetchState() && !context.fetchStateAndState()
         ? PasswordGateway.resolveQuickMessageKind(output.loadMessage(), input.retrieveInetSocketAddress().getAddress())
            == QuickMessageKind.CURRENT_QUICKMESSAGEKIND
         : false;
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.PENDING_ACTIVE_LOUDPROXYSTATE);
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.PRIVATE_LOGIN_OPTION);
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      if (data == PrivateLoginOption.PRIVATE_LOGIN_OPTION && output.loadTightPlatformCatalog().isState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)) {
         PasswordGateway.savePasswordStore(target, output.loadMessage(), input.retrieveInetSocketAddress().getAddress(), QuickMessageKind.PRIMARY_QUICKMESSAGEKIND);
         output.loadObject(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
         input.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.UPSTREAM_LOUDPROXYSTATE, input));
      }
   }
}
