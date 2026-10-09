package com.nickuc.login.discord;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.notification.NoticeSender;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.updater.NoticeCatalog;
import java.util.concurrent.TimeUnit;


public class DiscordBridge implements SilentPacketAdapter {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      NoticeSender data = PrivateLoginOption.MAIN_PRIVATELOGINOPTION.computeNoticeSender(input);
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.PRIMARY_NOTICECATALOG, targetValue -> {
         if (targetValue.contains("docs.nickuc.com")) {
            context.executeMessage(targetValue, "https://docs.nickuc.com/nlogin/");
         } else if (targetValue.contains("Discord")) {
            context.executeMessage(targetValue, "https://www.nickuc.com/discord");
         } else if (targetValue.contains("github.com")) {
            context.executeMessage(targetValue, "https://www.github.com/nickuc-com");
         } else if (targetValue.contains("OpeNLogin")) {
            context.executeMessage(targetValue, "https://www.spigotmc.org/resources/openlogin-1-7x-1-21x.57272/");
         } else if (targetValue.contains("nickuc.com")) {
            context.executeMessage(targetValue, "https://www.nickuc.com");
         } else {
            context.executeMessage(targetValue);
         }
      });
      target.processLinkedSessionHandler(true).loadStrictCommandHandler(contextValue -> {
         if (input.loadState() && this.a(output)) {
            CachedSettingsGateway.updateVerifiedServerAdapter(input, SharedLoginOption.UPSTREAM_SHAREDLOGINOPTION, data.findMessage());
         } else {
            contextValue.performTask();
         }
      }, 60L, 30L, TimeUnit.SECONDS);
      return new BusyLoginBarrier[]{new BusyLoginBarrier(PrivateLoginOption.MAIN_PRIVATELOGINOPTION, data)};
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      input.executeTask();
      SilentPacketAdapter.super.a(target, input, output, context, data);
   }

   public DiscordBridge(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public int buildCount(boolean target) {
      return 9000;
   }

   @Override
   public void processPasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      target.processLinkedSessionHandler(true)
         .loadStrictCommandHandler(
            () -> {
               if (input.loadState() && this.a(output)) {
                  CachedSettingsGateway.processOutgoingSenderAdapter(input, QuickProxyState.LOCAL_ACTIVE_QUICKPROXYSTATE);
                  input.getLinkedSessionHandler()
                     .loadStrictCommandHandler(
                        () -> CachedSettingsGateway.processOutgoingSenderAdapter(input, QuickProxyState.MAIN_FAST_QUICKPROXYSTATE), 2L, TimeUnit.SECONDS
                     );
                  CachedSettingsGateway.updateVerifiedServerAdapter(input, SharedLoginOption.PRIVATE_SHAREDLOGINOPTION);
                  target.processLinkedSessionHandler(true).loadStrictCommandHandler(() -> {
                     if (input.loadState() && this.a(output)) {
                        CachedSettingsGateway.updateVerifiedServerAdapter(input, SharedLoginOption.INTERNAL_SHAREDLOGINOPTION);
                     }
                  }, 5L, TimeUnit.SECONDS);
               }
            },
            1500L,
            TimeUnit.MILLISECONDS
         );
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.fetchLocalSettingsRepository().loadState();
   }

   @Override
   public boolean loadState() {
      return false;
   }
}
