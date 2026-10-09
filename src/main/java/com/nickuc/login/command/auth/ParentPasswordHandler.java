package com.nickuc.login.command.auth;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.api.enums.event.ChangePasswordSource;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.enums.event.UpdatePasswordSource;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ParentPasswordHandler extends LoginSource {
   private static float factor = Float.intBitsToFloat(1097859072);
   private static float activeFactor = Float.intBitsToFloat(1077936128);

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 3) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " <player> <new password>"
         );
      } else {
         LiveLoginCheckpoint output = new LiveLoginCheckpoint();
         IndirectPasswordResolver context = this.passwordStore.findIndirectPasswordResolver();
         SpawnLookup data = context.loadSpawnLookup(target, super.internalLoginOption, input, input[1]);
         if (data != null) {
            if (!data.retrieveState()) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
            } else {
               String value = input[2];
               int result = value.length();
               if (result <= SpawnState.ACTIVE_OUTGOING_SPAWNSTATE.r()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ACTIVE_REMOTE_LOUDPROXYSTATE);
               } else if (result >= SpawnState.ACTIVE_SECONDARY_SPAWNSTATE.r()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ACTIVE_LOCAL_LOUDPROXYSTATE);
               } else if (context.checkState(data, value)) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ACTIVE_MAIN_LOUDPROXYSTATE);
               } else {
                  String request = data.retrieveMessage();
                  VerifiedServerAdapter response = this.passwordStore.b().resolveVerifiedServerAdapter(request);
                  UUID source = response != null ? response.getUniqueId() : data.getUniqueId();
                  if (this.passwordStore.verifyState(EventEnum.CHANGE_PASSWORD, response, source, request, ChangePasswordSource.BY_ADMIN)) {
                     synchronized (data.object) {
                        if (!context.validateState(data, value)) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                           CachedSettingsGateway.processOutgoingSenderAdapter(response, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                           return;
                        }

                        PasswordHashContainer.dispatchMessage("The " + request + " player had his password changed by " + target.getName() + ".");
                        this.passwordStore.verifyState(EventEnum.PASSWORD_UPDATE_EVENT, response, source, request, value, UpdatePasswordSource.BY_ADMIN);
                        if (response != null) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(response, LoudProxyState.AUTHENTICATED_LOUDPROXYSTATE);
                        }

                        CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§a✔ You have successfully changed §f" + request + "'s §apassword");
                        CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
                        CachedSettingsGateway.handleOutgoingSenderAdapter(
                           target, "§e⚑ This operation took §f" + output.loadMessage(TimeUnit.MILLISECONDS, 2) + "ms§e."
                        );
                     }
                  }
               }
            }
         }
      }
   }

   public ParentPasswordHandler(PasswordStore target) {
      super(target, "changepass", "nlogin.command.nlogin.changepass", true, false, "changepassword");
   }
}
