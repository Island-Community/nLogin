package com.nickuc.login.storage.update;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.AuthenticatedNoticeKind;
import com.nickuc.login.updater.NoticeCatalog;


public class UpdateStore implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   public UpdateStore(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      switch (data) {
         case LOCAL_PRIVATELOGINOPTION:
         case REMOTE_PRIVATELOGINOPTION:
            target.a().loadPrimaryPasswordHashVerifier().resolvePrimaryPasswordHashVerifier("nlogin-vch", "").sendTask();
            target.a()
               .updateAuthenticatedNoticeKind(
                  data == PrivateLoginOption.LOCAL_PRIVATELOGINOPTION
                     ? AuthenticatedNoticeKind.AUTHENTICATED_NOTICE_KIND
                     : AuthenticatedNoticeKind.ACTIVE_AUTHENTICATEDNOTICEKIND
               );
            DirectPlayerContract.super.a(target, input, output, context, data);
      }
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.fetchLocalSettingsRepository().loadState() || !target.a().loadPrimaryPasswordHashVerifier().hasState("nlogin-vch");
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.MAIN_NOTICECATALOG, "/nlogin update");
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.LOCAL_PRIVATELOGINOPTION, PrivateLoginOption.REMOTE_PRIVATELOGINOPTION);
   }
}
