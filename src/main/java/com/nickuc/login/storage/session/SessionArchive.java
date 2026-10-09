package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.command.setup.DeletePurgeCommand;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;

public class SessionArchive extends DeletePurgeCommand {
   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      String context = target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.PENDING_SILENTPROXYSTATE ? "nantibot" : "nAntiBot";
      return target.b().canState(context) ? false : target.fetchLocalSettingsRepository().loadState();
   }

   public SessionArchive(CachedProxyCatalog target) {
      super(target, "nAntiBot");
   }

   @Override
   public void handlePasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.DIRECT_NOTICECATALOG, targetValue -> {
         if (targetValue.contains("plugin")) {
            context.processMessage(targetValue, "§bhttps://docs.nickuc.com/nantibot/", "https://docs.nickuc.com/nantibot/");
         } else {
            context.executeMessage(targetValue);
         }
      });
   }
}
