package com.nickuc.login.storage.bungee;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.auth.login.LocalLoginBarrier;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;
import java.io.File;


public class BungeeDao implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   public BungeeDao(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.VERIFIED_NOTICECATALOG);
      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.INTERNAL_PRIVATELOGINOPTION, PrivateLoginOption.UPSTREAM_PRIVATELOGINOPTION);
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.fetchLocalSettingsRepository().loadState() && target.loadState();
   }

   private void updatePasswordStore(PasswordStore target, VerifiedServerAdapter input) {
      SecondarySenderAdapter output = target.findObject();
      output.getPasswordHashAdapter().updateVerifiedServerAdapter(input, 4, "action", 2, "plugin", "BungeeGuard");
      if (target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.ACTIVE_SILENTPROXYSTATE && !target.b().canState("BungeeGuard")) {
         PasswordHashContainer.processMessage("§9Downloading BungeeGuard...");
         File context = target.resolveFile().getParentFile();
         File data = new File(context, "BungeeGuard.jar");
         if (data.exists() && !data.delete()) {
            data.deleteOnExit();
            PasswordHashContainer.updateMessage("Unable to delete " + data + " file");
            return;
         }

         LocalLoginBarrier value = PendingPasswordHashHasher.getPendingPasswordHashHasher()
            .loadLocalLoginBarrier("https://github.com/nickuc/BungeeGuard/releases/latest/download/BungeeGuard.jar", data);
         if (value.findCount() != 200 || !value.retrieveState()) {
            PasswordHashContainer.updateMessage("Unable to download BungeeGuard, code = " + value.findCount());
         }
      }
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      switch (data) {
         case INTERNAL_PRIVATELOGINOPTION:
            this.updatePasswordStore(target, input);
         case UPSTREAM_PRIVATELOGINOPTION:
            DirectPlayerContract.super.a(target, input, output, context, data);
      }
   }
}
