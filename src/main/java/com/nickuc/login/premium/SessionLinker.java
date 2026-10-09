package com.nickuc.login.premium;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.updater.NoticeCatalog;


public class SessionLinker implements DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   public SessionLinker(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return !target.fetchLocalSettingsRepository().loadState() && target.a().getCount() != 9 && SecureLoginHandler.retrieveRandom().nextInt(100) <= 2;
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.READY_NOTICECATALOG, targetValue -> {
         String inputValue = targetValue.trim();
         if (inputValue.length() > 2 && inputValue.charAt(0) == '[' && inputValue.charAt(targetValue.length() - 1) == ']') {
            context.executeMessage(targetValue, "https://docs.nickuc.com/nlogin/premium");
         } else {
            context.executeMessage(targetValue);
         }
      });
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.PRIVATE_LOGIN_OPTION);
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }
}
