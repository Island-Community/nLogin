package com.nickuc.login.command;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.account.SpawnOption;
import com.nickuc.login.auth.account.LocalAccountGate;
import com.nickuc.login.auth.account.SharedAccountGate;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.locale.LocaleCollection;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.Locale;
import java.util.stream.Collectors;

public class LocaleHandler extends LocaleCollection {
   public LocaleHandler(InternalLoginOption target) {
      super(target);
      this.getPasswordHashCommand();
   }

   @Override
   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (!(target instanceof VerifiedServerAdapter)) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin register" + (output.length > 0 ? " " : "") + String.join(" ", output)
         );
      } else {
         VerifiedServerAdapter context = (VerifiedServerAdapter)target;
         LimboCoordinator data = this.indirectSessionHandler.loadLimboRegistry().loadLimboCoordinator(context);
         SpawnLookup value = data.loadSpawnLookup();
         if (value == null) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.DIRECT_LOUDPROXYSTATE);
            CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
         } else if (value.retrieveState()) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_CURRENT_LOUDPROXYSTATE);
            CachedSettingsGateway.updateVerifiedServerAdapter(context, SharedLoginOption.LOCAL_SHAREDLOGINOPTION);
            CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
         } else if (data.loadTightPlatformCatalog().isState(TightPlatformCatalog.PRIMARY_TIGHTPLATFORMCATALOG)) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.LIVE_LOUDPROXYSTATE);
            CachedSettingsGateway.updateVerifiedServerAdapter(context, SharedLoginOption.MAIN_SHAREDLOGINOPTION);
            CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
         } else if (output.length == 0) {
            SecondaryAccountHandler source = data.getSecondaryAccountHandler();
            CachedSettingsGateway.handleOutgoingSenderAdapter(
               context, LoudProxyState.ACTIVE_LOUDPROXYSTATE, inputValue -> source.dispatchMessage(inputValue, input.toLowerCase(Locale.ENGLISH) + " ")
            );
         } else {
            SpawnOption result = value.loadSpawnOption();
            if (result != SpawnOption.SPAWN_OPTION && result != SpawnOption.PENDING_SPAWNOPTION) {
               String request = context.resolveMessage();
               if (SpawnState.ACTIVE_VERIFIED_SPAWNSTATE.ar() && !SpawnState.ACTIVE_INCOMING_SPAWNSTATE.a(new Object[0]).contains(request)) {
                  LocalAccountGate response = this.indirectSessionHandler.findIndirectPasswordResolver().createLocalAccountGate(request);
                  if (response == null) {
                     CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                     CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                     return;
                  }

                  if (response.canState(SpawnState.ACTIVE_AUTHENTICATED_SPAWNSTATE.r())) {
                     CachedSettingsGateway.executeOutgoingSenderAdapter(
                        context,
                        LoudProxyState.SECONDARY_LOUDPROXYSTATE,
                        response.loadCollection().stream().map(SharedAccountGate::getName).collect(Collectors.joining(", "))
                     );
                     CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                     return;
                  }
               }
            }

            if (output.length == 1) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_VERIFIED_LOUDPROXYSTATE);
               CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
            } else {
               String entry = output[0];
               int record = entry.length();
               if (record <= SpawnState.ACTIVE_OUTGOING_SPAWNSTATE.r()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_REMOTE_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else if (record >= SpawnState.ACTIVE_SECONDARY_SPAWNSTATE.r()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_LOCAL_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else if (SpawnState.ACTIVE_DIRECT_SPAWNSTATE.ar() && !Pbkdf2Linker.findPattern().matcher(entry).matches()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_CACHED_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else if (!entry.equals(output[1])) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.ACTIVE_STORED_LOUDPROXYSTATE);
                  CachedSettingsGateway.updateVerifiedServerAdapter(context, SharedLoginOption.REMOTE_SHAREDLOGINOPTION);
                  CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
               } else {
                  this.indirectSessionHandler.findSettingsLinker().processSpawnLookup(value, context, data, entry, null, true, true);
               }
            }
         }
      }
   }
}
