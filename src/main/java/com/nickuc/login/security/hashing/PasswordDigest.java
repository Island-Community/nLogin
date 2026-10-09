package com.nickuc.login.security.hashing;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.enums.event.UnregisterSource;
import com.nickuc.login.api.enums.event.UpdatePasswordSource;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.locale.LocaleCollection;
import java.util.UUID;

public class PasswordDigest extends LocaleCollection {
   public PasswordDigest(InternalLoginOption target) {
      super(target);
      this.getPasswordHashCommand();
   }

   @Override
   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (!(target instanceof VerifiedServerAdapter)) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin unregister" + (output.length > 0 ? " " : "") + String.join(" ", output)
         );
      } else {
         VerifiedServerAdapter context = (VerifiedServerAdapter)target;
         if (output.length == 0) {
            String reference = String.format("/%s <%s>", input, CachedSettingsGateway.computeMessage(LoudProxyState.OPEN_LOUDPROXYSTATE, context));
            CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.LINKED_LOUDPROXYSTATE, reference);
         } else {
            LimboCoordinator data = this.indirectSessionHandler.loadLimboRegistry().loadLimboCoordinator(context);
            SpawnLookup value = data.loadSpawnLookup();
            if (value.fetchState() && (!value.fetchStateAndState() || value.retrieveState())) {
               IndirectPasswordResolver result = this.indirectSessionHandler.findIndirectPasswordResolver();
               String request = output[0];
               if (!result.checkState(value, request)) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_AUTHENTICATED_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else {
                  String response = context.getName();
                  UUID source = context.getUniqueId();
                  if (this.indirectSessionHandler.verifyState(EventEnum.UNREGISTER, context, source, response, UnregisterSource.BY_PLAYER)) {
                     synchronized (value.object) {
                        if (!value.fetchState()) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                           CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                           return;
                        }

                        if (!result.canState(value)) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                           CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                           return;
                        }

                        PasswordHashContainer.dispatchMessage("The " + response + " player has unregistered his account. ~ " + context.resolveMessage());

                        try {
                           this.indirectSessionHandler.verifyState(EventEnum.PASSWORD_UPDATE_EVENT, context, source, response, null, UpdatePasswordSource.BY_PLAYER);
                        } finally {
                           context.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.PRIVATE_LOUDPROXYSTATE, context));
                        }
                     }
                  }
               }
            } else {
               CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.INCOMING_LOUDPROXYSTATE);
               CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
            }
         }
      }
   }
}
