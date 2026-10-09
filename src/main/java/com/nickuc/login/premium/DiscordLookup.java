package com.nickuc.login.premium;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.config.SettingsWriter;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.notification.NoticeSender;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.backup.BackupDao;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.updater.MessageKind;
import com.nickuc.login.updater.NoticeCatalog;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class DiscordLookup implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      switch (data) {
         case ACTIVE_PRIVATELOGINOPTION:
            this.updatePasswordStore(target, input, output);
            CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.SHARED_LOUDPROXYSTATE);
            SilentPacketAdapter.super.a(target, input, output, context, data);
            break;
         case PENDING_PRIVATELOGINOPTION:
            CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.SHARED_LOUDPROXYSTATE);
            SilentPacketAdapter.super.a(target, input, output, context, data);
      }
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (!target.fetchLocalSettingsRepository().findState()) {
         return false;
      }

      LenientPremiumOption context = output.retrieveLenientPremiumOption();
      return context == CachedSettingsGateway.loadLenientPremiumOption() ? false : context != null && context != LenientPremiumOption.ROOT_LENIENTPREMIUMOPTION;
   }

   public DiscordLookup(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      LenientPremiumOption data = output.retrieveLenientPremiumOption();
      if (data == null) {
         throw new IllegalStateException("Client language is null in LanguageSelector, but the value was previously checked.");
      }

      CachedSettingsGateway.dispatchPasswordStore(target, data);
      SettingsWriter value = CachedSettingsGateway.handleSettingsWriter(data);
      List result = value.processCollectionForCollection(NoticeCatalog.PENDING_NOTICECATALOG, OpenLocaleBarrier.handleMessage(data.findMessage()));
      context.executeMessage(String.join(" §r\n", result).replace("@player", input.getName()));
      NoticeSender request = value.handleNoticeSender(MessageKind.ACTIVE_MESSAGEKIND);
      NoticeSender response = value.handleNoticeSender(MessageKind.PENDING_MESSAGEKIND);
      target.processLinkedSessionHandler(true).loadStrictCommandHandler(contextValue -> {
         if (input.loadState() && this.a(output)) {
            value.loadSettingsPublisher(SharedLoginOption.SHARED_SHAREDLOGINOPTION).sendVerifiedServerAdapter(input);
         } else {
            contextValue.performTask();
         }
      }, 20L, 20L, TimeUnit.SECONDS);
      return new BusyLoginBarrier[]{
         new BusyLoginBarrier(PrivateLoginOption.ACTIVE_PRIVATELOGINOPTION, new NoticeSender(request.findMessage(), request.as())),
         new BusyLoginBarrier(PrivateLoginOption.PENDING_PRIVATELOGINOPTION, new NoticeSender(response.findMessage(), response.as()))
      };
   }

   private void updatePasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      LenientPremiumOption context = output.retrieveLenientPremiumOption();
      if (context == null) {
         throw new IllegalStateException("Client language is null in LanguageSelector, but the value was checked before.");
      }

      if (CachedSettingsGateway.loadLenientPremiumOption() != context) {
         if (!target.fetchLocalSettingsRepository().loadState()) {
            BackupDao.createFile(target, null, false);
         }

         target.a().findState();
         File data = new File(target.resolveFile(), "premium");
         new File(data, "config.yml").delete();
         File value = new File(data, "2fa");
         new File(value, "discord.yml").delete();
         new File(value, "email.yml").delete();
         SettingsLookup.computeLoudNoticeCatalog(target, target.a(), "config_%s.yml", context, false);
         Pbkdf2Linker.loadLoudNoticeCatalog(target);
      }
   }

   @Override
   public boolean loadState() {
      return false;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }
}
