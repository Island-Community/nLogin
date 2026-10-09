package com.nickuc.login.command.auth;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.api.enums.event.ChangePasswordSource;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.enums.event.UpdatePasswordSource;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.DiscordVerifier;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.session.LocalSessionTable;
import com.nickuc.login.storage.locale.LocaleCollection;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.UUID;

public class LowPasswordHandler extends LocaleCollection {
   private static float factor = Float.intBitsToFloat(1097859072);
   private static float activeFactor = Float.intBitsToFloat(1077936128);

   public LowPasswordHandler(InternalLoginOption target) {
      super(target);
      this.getPasswordHashCommand();
   }

   @Override
   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (!(target instanceof VerifiedServerAdapter)) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin changepassword" + (output.length > 0 ? " " : "") + String.join(" ", output)
         );
      } else {
         VerifiedServerAdapter context = (VerifiedServerAdapter)target;
         LimboCoordinator data = this.indirectSessionHandler.loadLimboRegistry().loadLimboCoordinator(context);
         LocalSessionTable value = (LocalSessionTable)data.d(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
         TightPlatformCatalog result = data.loadTightPlatformCatalog();
         if (!result.canState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)
            && (
               result.isState(TightPlatformCatalog.LOCAL_TIGHTPLATFORMCATALOG)
                  || value == null
                  || value.fetchCachedProxyCatalog() == CachedProxyCatalog.CACHED_CACHEDPROXYCATALOG
            )) {
            if (output.length != 2) {
               String subject = String.format(
                  "/%s <%s> <%s>",
                  input,
                  CachedSettingsGateway.computeMessage(LoudProxyState.OPEN_LOUDPROXYSTATE, context),
                  CachedSettingsGateway.computeMessage(LoudProxyState.READY_LOUDPROXYSTATE, context)
               );
               CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.LINKED_LOUDPROXYSTATE, subject);
            } else {
               SpawnLookup request = data.loadSpawnLookup();
               if (!request.retrieveState()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else {
                  String response = output[0];
                  String source = output[1];
                  int entry = source.length();
                  if (entry <= SpawnState.ACTIVE_OUTGOING_SPAWNSTATE.r()) {
                     CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_REMOTE_LOUDPROXYSTATE);
                     CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                  } else if (entry >= SpawnState.ACTIVE_SECONDARY_SPAWNSTATE.r()) {
                     CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_LOCAL_LOUDPROXYSTATE);
                     CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                  } else if (SpawnState.ACTIVE_DIRECT_SPAWNSTATE.ar() && !Pbkdf2Linker.findPattern().matcher(source).matches()) {
                     CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_CACHED_LOUDPROXYSTATE);
                     CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                  } else if (response.equals(source)) {
                     CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_MAIN_LOUDPROXYSTATE);
                     CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                  } else {
                     IndirectPasswordResolver record = this.indirectSessionHandler.findIndirectPasswordResolver();
                     if (!record.checkState(request, response)) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_AUTHENTICATED_LOUDPROXYSTATE);
                        CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                     } else {
                        UUID item = context.getUniqueId();
                        String element = context.getName();
                        if (this.indirectSessionHandler.verifyState(EventEnum.CHANGE_PASSWORD, context, item, element, ChangePasswordSource.BY_PLAYER)) {
                           synchronized (request.object) {
                              if (!request.retrieveState()) {
                                 CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                                 CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                                 return;
                              }

                              if (request.resolveState()) {
                                 request.executeTask();
                              }

                              if (!record.validateState(request, source)) {
                                 CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                                 CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                                 return;
                              }

                              PasswordHashContainer.dispatchMessage("The " + element + " player changed its account password ~ " + context.resolveMessage());
                              if (value != null && value.fetchCachedProxyCatalog() == CachedProxyCatalog.CACHED_CACHEDPROXYCATALOG) {
                                 value.getLoudPlayerContract().handlePasswordStore(this.indirectSessionHandler, context, data);
                              }

                              this.indirectSessionHandler
                                 .verifyState(EventEnum.PASSWORD_UPDATE_EVENT, context, item, element, source, UpdatePasswordSource.BY_PLAYER);
                              CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
                              CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.AUTHENTICATED_LOUDPROXYSTATE);
                              DiscordVerifier payload = this.indirectSessionHandler.resolveDiscordVerifier();
                              if (payload.retrieveFastDiscordNotifier() != null && request.fetchQuickDiscordHandler().getMessage() != null) {
                                 payload.retrieveFastDiscordNotifier().saveSpawnLookup(request, context, source);
                              }

                              if (payload.getMailNotifier() != null && request.fetchQuickDiscordHandler().loadMessage() != null) {
                                 payload.getMailNotifier().saveSpawnLookup(request, context, source);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
