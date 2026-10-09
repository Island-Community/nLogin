package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.model.UpstreamSpawnState;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;


public class DeadSessionRepository implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   public DeadSessionRepository(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.fetchLocalSettingsRepository().findState();
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.REMOTE_NOTICECATALOG);
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.VERIFIED_PRIVATELOGINOPTION, PrivateLoginOption.AUTHENTICATED_PRIVATELOGINOPTION);
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      switch (data) {
         case VERIFIED_PRIVATELOGINOPTION:
         case AUTHENTICATED_PRIVATELOGINOPTION:
            this.a(output)
               .handleUpstreamSpawnState(
                  data == PrivateLoginOption.VERIFIED_PRIVATELOGINOPTION
                     ? UpstreamSpawnState.PENDING_UPSTREAMSPAWNSTATE
                     : UpstreamSpawnState.PRIMARY_UPSTREAMSPAWNSTATE
               );
            DirectPlayerContract.super.a(target, input, output, context, data);
      }
   }
}
