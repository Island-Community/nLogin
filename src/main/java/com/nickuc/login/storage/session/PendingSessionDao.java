package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;


public abstract class PendingSessionDao implements LoudPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;
   private final StrictMessageKind strictMessageKind;
   private final LoudProxyState loudProxyState;

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.executeOutgoingSenderAdapter(input, this.loudProxyState);
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.PRIVATE_LOGIN_OPTION);
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      String context = this.strictMessageKind.resolveMessage();
      return this.strictMessageKind.hasState(target)
         && this.strictMessageKind.resolveState()
         && (context == null || input.i(context))
         && this.strictMessageKind.computeMessage(output.loadSpawnLookup()) == null;
   }

   public PendingSessionDao(CachedProxyCatalog target, StrictMessageKind input, LoudProxyState output) {
      this.cachedProxyCatalog = target;
      this.strictMessageKind = input;
      this.loudProxyState = output;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }
}
