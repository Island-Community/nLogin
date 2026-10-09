package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.config.SettingsWriter;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class SessionSource implements SilentPacketAdapter {
   private final CachedProxyCatalog cachedProxyCatalog;
   private static float factor = Float.intBitsToFloat(1097859072);
   private static float activeFactor = Float.intBitsToFloat(1077936128);

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.fetchLocalSettingsRepository().loadState() && !input.i("nlogin.admin");
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public boolean loadState() {
      return false;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      LenientPremiumOption data = output.retrieveLenientPremiumOption();
      if (data == null || data == LenientPremiumOption.ACTIVE_LENIENTPREMIUMOPTION) {
         data = CachedSettingsGateway.loadLenientPremiumOption();
      }

      CachedSettingsGateway.dispatchPasswordStore(target, data);
      SettingsWriter value = CachedSettingsGateway.handleSettingsWriter(data);
      List result = value.processCollectionForCollection(NoticeCatalog.CURRENT_NOTICECATALOG, "nlogin.admin");
      context.executeMessage(String.join(" §r\n", result).replace("@player", input.getName()));
      AtomicInteger request = new AtomicInteger();
      target.processLinkedSessionHandler(true).loadStrictCommandHandler(valueValue -> {
         if (input.loadState() && this.a(output)) {
            LocalSessionTable resultValue = (LocalSessionTable)output.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
            if (resultValue == null) {
               valueValue.performTask();
            } else if (input.i("nlogin.admin")) {
               output.updateLenientMessageKind(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND, resultValue.findLocalSessionTable());
               valueValue.performTask();
               CachedSettingsGateway.processOutgoingSenderAdapter(input, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
               this.b(target, input, output);
            } else {
               if (request.incrementAndGet() >= 20) {
                  request.set(0);
                  value.loadSettingsPublisher(SharedLoginOption.SHARED_SHAREDLOGINOPTION).sendVerifiedServerAdapter(input);
               }
            }
         } else {
            valueValue.performTask();
         }
      }, 1L, 1L, TimeUnit.SECONDS);
      return new BusyLoginBarrier[0];
   }

   public SessionSource(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }
}
