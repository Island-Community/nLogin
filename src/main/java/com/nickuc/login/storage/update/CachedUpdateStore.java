package com.nickuc.login.storage.update;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;


public class CachedUpdateStore implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   public CachedUpdateStore(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      switch (data) {
         case CACHED_PRIVATELOGINOPTION:
         case STORED_PRIVATELOGINOPTION:
            target.a()
               .loadPrimaryPasswordHashVerifier()
               .createPrimaryPasswordHashVerifier("autoUpdate", data == PrivateLoginOption.CACHED_PRIVATELOGINOPTION)
               .sendTask();
            DirectPlayerContract.super.a(target, input, output, context, data);
      }
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.fetchLocalSettingsRepository().loadState() || !target.a().loadPrimaryPasswordHashVerifier().hasState("autoUpdate");
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.LOCAL_NOTICECATALOG, "/nlogin update");
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.CACHED_PRIVATELOGINOPTION, PrivateLoginOption.STORED_PRIVATELOGINOPTION);
   }
}
