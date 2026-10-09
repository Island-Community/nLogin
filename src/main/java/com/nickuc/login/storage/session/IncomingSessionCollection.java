package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.model.ProxyState;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;


public class IncomingSessionCollection implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.INTERNAL_NOTICECATALOG);
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.ACTIVE_PRIVATELOGINOPTION, PrivateLoginOption.PENDING_PRIVATELOGINOPTION);
   }

   public IncomingSessionCollection(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.a().getCount() == 9 && target.fetchLocalSettingsRepository().findState();
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      switch (data) {
         case ACTIVE_PRIVATELOGINOPTION:
         case PENDING_PRIVATELOGINOPTION:
            this.a(output).updateProxyState(data == PrivateLoginOption.ACTIVE_PRIVATELOGINOPTION ? ProxyState.ACTIVE_PROXYSTATE : ProxyState.PROXY_STATE);
            DirectPlayerContract.super.a(target, input, output, context, data);
      }
   }
}
