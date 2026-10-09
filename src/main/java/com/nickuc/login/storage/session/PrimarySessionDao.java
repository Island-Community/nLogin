package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;


public class PrimarySessionDao implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   public PrimarySessionDao(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.a().getCount() == 9 && target.fetchLocalSettingsRepository().findState() && this.a(output).findState();
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.PRIVATE_NOTICECATALOG, targetValue -> {
         if (targetValue.contains("nickuc.com")) {
            context.executeMessage(targetValue, "https://docs.nickuc.com/nlogin/username-appender");
         } else {
            context.executeMessage(targetValue);
         }
      });
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.PRIVATE_LOGIN_OPTION);
   }
}
