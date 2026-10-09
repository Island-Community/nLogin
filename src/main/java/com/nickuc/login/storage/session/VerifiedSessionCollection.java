package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;


public class VerifiedSessionCollection implements DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;
   private Boolean value;

   public Boolean resolveBoolean() {
      return this.value;
   }

   public VerifiedSessionCollection(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (this.value != null) {
         return this.value;
      }

      int context = target.a().getCount();
      return this.value = context != 1 && context != 9;
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(
         input,
         target.a().getCount() != 23 ? NoticeCatalog.SECURE_NOTICECATALOG : NoticeCatalog.OPEN_NOTICECATALOG,
         target.getMessage(),
         "https://www.nickuc.com/panel/home",
         "/nlogin contact"
      );
      this.value = false;
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.PRIVATE_LOGIN_OPTION);
   }
}
