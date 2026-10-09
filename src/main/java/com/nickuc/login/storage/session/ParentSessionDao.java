package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.command.setup.DeletePurgeCommand;
import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.NoticeCatalog;

public class ParentSessionDao extends DeletePurgeCommand {
   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (target.findIncomingLoginGate().retrieveSilentProxyState() != SilentProxyState.SILENT_PROXY_STATE) {
         return false;
      } else {
         ParentAccountHandler context = target.b();
         if (context.canState("nChat")) {
            return false;
         } else {
            return !target.fetchLocalSettingsRepository().loadState()
               ? false
               : CachedSettingsGateway.loadState() && (context.canState("Legendchat") || context.canState("UltimateChat"));
         }
      }
   }

   @Override
   public void handlePasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, NoticeCatalog.LINKED_NOTICECATALOG, targetValue -> {
         if (targetValue.contains("plugin")) {
            context.processMessage(targetValue, "§bhttps://docs.nickuc.com/nchat/", "https://docs.nickuc.com/nchat/");
         } else {
            context.executeMessage(targetValue);
         }
      });
   }

   public ParentSessionDao(CachedProxyCatalog target) {
      super(target, "nChat");
   }
}
