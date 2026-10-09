package com.nickuc.login.storage.spawn;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;


public class SpawnDao implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.fetchLocalSettingsRepository().loadState();
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.ROOT_NOTICECATALOG, "/nlogin spawn set join");
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.PRIVATE_LOGIN_OPTION);
   }

   public SpawnDao(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }
}
