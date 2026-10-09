package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.NoticeKind;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;


public class InternalSessionRepository implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.fetchLocalSettingsRepository().findState() && this.a(output).retrieveNoticeKind() != NoticeKind.NOTICE_KIND;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      switch (data) {
         case SHARED_PRIVATELOGINOPTION:
         case PRIVATE_PRIVATELOGINOPTION:
            this.a(output).executeNoticeKind(data == PrivateLoginOption.SHARED_PRIVATELOGINOPTION ? NoticeKind.ACTIVE_NOTICEKIND : NoticeKind.PENDING_NOTICEKIND);
            DirectPlayerContract.super.a(target, input, output, context, data);
      }
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.STORED_NOTICECATALOG);
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.SHARED_PRIVATELOGINOPTION, PrivateLoginOption.PRIVATE_PRIVATELOGINOPTION);
   }

   public InternalSessionRepository(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }
}
