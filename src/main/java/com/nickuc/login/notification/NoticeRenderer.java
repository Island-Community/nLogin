package com.nickuc.login.notification;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.PrivateLoginCheckpoint;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.session.LocalSessionTable;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;

public class NoticeRenderer extends LoginSource {
   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (!(target instanceof VerifiedServerAdapter)) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.TOP_LOUDPROXYSTATE);
      } else {
         VerifiedServerAdapter output = (VerifiedServerAdapter)target;
         LimboCoordinator context = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
         if (!context.d(LenientMessageKind.PRIMARY_LENIENTMESSAGEKIND)) {
            SecondaryAccountHandler data = context.getSecondaryAccountHandler();
            if (input.length < 4) {
               data.executeMessage("§cThis action could not be performed: insufficient arguments.");
            } else {
               String value = input[1];
               if (value.equals("notification")) {
                  int result = PrivateLoginCheckpoint.handleInteger(input[2], -1);
                  int request = PrivateLoginCheckpoint.handleInteger(input[3], -1);
                  synchronized (context.object) {
                     LocalSessionTable source = (LocalSessionTable)context.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
                     if (source == null) {
                        return;
                     }

                     PrivateLoginOption entry = PrivateLoginOption.processPrivateLoginOption(request);
                     if (entry == null) {
                        return;
                     }

                     LoudPlayerContract record = source.getLoudPlayerContract();
                     if (record.fetchCachedProxyCatalog().findCount() != result) {
                        return;
                     }

                     record.dispatchPasswordStore(this.passwordStore, output, context, data, entry);
                  }
               }
            }
         }
      }
   }

   public NoticeRenderer(PasswordStore target) {
      super(target, "click", null, false, true);
   }
}
