package com.nickuc.login.notification;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.session.LocalSessionTable;
import com.nickuc.login.storage.locale.LocaleCollection;
import java.util.UUID;

public class SafeNoticeRenderer extends LocaleCollection {
   public SafeNoticeRenderer(InternalLoginOption target) {
      super(target);
   }

   @Override
   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()) {
         if (!QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
            if (!(target instanceof VerifiedServerAdapter)) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(
                  target,
                  LoudProxyState.LINKED_LOUDPROXYSTATE,
                  "/"
                     + InternalLoginOption.LOCAL_INTERNALLOGINOPTION.fetchPasswordHashCommand().loadMessage()
                     + (output.length > 0 ? " " : "")
                     + String.join(" ", output)
               );
            } else {
               VerifiedServerAdapter context = (VerifiedServerAdapter)target;
               LimboRegistry data = this.indirectSessionHandler.loadLimboRegistry();
               if (!data.canState(context)) {
                  LimboCoordinator content = data.loadLimboCoordinator(context);
                  LocalSessionTable payload = (LocalSessionTable)content.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
                  if (payload != null && payload.fetchCachedProxyCatalog() == CachedProxyCatalog.ACTIVE_CACHEDPROXYCATALOG) {
                     InternalLoginOption.INTERNAL_LOGIN_OPTION
                        .performVerifiedServerAdapter(
                           context,
                           content,
                           "click",
                           "notification",
                           Integer.toString(CachedProxyCatalog.ACTIVE_CACHEDPROXYCATALOG.findCount()),
                           Integer.toString(PrivateLoginOption.PENDING_PRIVATELOGINOPTION.findCount())
                        );
                  }
               } else if (output.length == 0) {
                  String element = String.format("/%s <%s>", input, CachedSettingsGateway.computeMessage(LoudProxyState.OPEN_LOUDPROXYSTATE, context));
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.LINKED_LOUDPROXYSTATE, element);
               } else {
                  LimboCoordinator value = data.loadLimboCoordinator(context);
                  SpawnLookup result = value.loadSpawnLookup();
                  synchronized (result.object) {
                     if (!result.fetchState()) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                        CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                        return;
                     }

                     UUID response = result.getMojangId();
                     if (response != null) {
                        if (response.equals(result.getUniqueId())) {
                           value.getSecondaryAccountHandler()
                              .executeMessage(value.loadState() ? "§cVocê não pode virar uma conta offline." : "§cYou cannot become an offline account.");
                           CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                           return;
                        }

                        if (result.fetchStateAndState()) {
                           IndirectPasswordResolver source = this.indirectSessionHandler.findIndirectPasswordResolver();
                           String entry = output[0];
                           if (!source.checkState(result, entry)) {
                              CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_AUTHENTICATED_LOUDPROXYSTATE);
                              CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                              return;
                           }

                           result.processUniqueId(null);
                           if (!this.indirectSessionHandler.findIndirectPasswordResolver().isState(result)) {
                              result.processUniqueId(response);
                              CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                              CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                              return;
                           }

                           context.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.INTERNAL_LOUDPROXYSTATE, context));
                           return;
                        }
                     }
                  }

                  InternalLoginOption.INTERNAL_LOGIN_OPTION
                     .performVerifiedServerAdapter(
                        context,
                        value,
                        "click",
                        "notification",
                        Integer.toString(CachedProxyCatalog.ACTIVE_CACHEDPROXYCATALOG.findCount()),
                        Integer.toString(PrivateLoginOption.PENDING_PRIVATELOGINOPTION.findCount())
                     );
               }
            }
         }
      }
   }
}
