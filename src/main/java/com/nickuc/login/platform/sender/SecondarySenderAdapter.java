package com.nickuc.login.platform.sender;

import com.nickuc.login.api.enums.ServerConnectType;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.VerifiedProxyCatalog;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.protocol.PasswordHashAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.spawn.PrimaryMessageOption;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public interface SecondarySenderAdapter {
   default boolean canState(VerifiedServerAdapter target, LimboCoordinator input) {
      return this.isState(input, this.handleMessage(target));
   }

   @Nullable
   default ServerConnectType buildServerConnectType(VerifiedServerAdapter target, LimboCoordinator input) {
      for (ServerConnectType value : ServerConnectType.values()) {
         VerifiedProxyCatalog result = this.createVerifiedProxyCatalog(target, input, value);
         if (VerifiedProxyCatalog.canState(result)) {
            return value;
         }
      }

      return null;
   }

   @Nullable
   String handleMessage(VerifiedServerAdapter target);

   PasswordStore fetchPasswordStore();

   boolean checkState(String target);

   default boolean isState(LimboCoordinator target, String input) {
      if (input == null) {
         return false;
      } else {
         return input.equals(target.d(LenientMessageKind.ACTIVE_PRIMARY_LENIENTMESSAGEKIND))
            ? true
            : PrimaryMessageOption.PRIMARY_MESSAGE_OPTION.a(new Object[0]).stream().anyMatch(targetValue -> targetValue.equalsIgnoreCase(input));
      }
   }

   PasswordHashAdapter getPasswordHashAdapter();

   boolean verifyState(VerifiedServerAdapter target);

   LinkedSessionHandler loadLinkedSessionHandler();

   default VerifiedProxyCatalog createVerifiedProxyCatalog(VerifiedServerAdapter target, LimboCoordinator input, ServerConnectType output) {
      switch (output) {
         case WITH_LAST_SERVER:
            if (PrimaryMessageOption.LOCAL_PRIMARYMESSAGEOPTION.ar()) {
               SpawnLookup content = input.loadSpawnLookup();
               String holder = content.resolveCachedPasswordHashHasher().handleObject("last-server");
               if (holder != null
                  && !this.isState(input, holder)
                  && this.checkState(holder)
                  && !PrimaryMessageOption.REMOTE_PRIMARYMESSAGEOPTION.a(new Object[0]).contains(holder)) {
                  int subject = PrimaryMessageOption.MAIN_PRIMARYMESSAGEOPTION.r();
                  PlayerContract option = subject <= 0 ? null : dataValue -> {
                     if (!dataValue) {
                        this.loadLinkedSessionHandler().createStrictCommandHandler(() -> this.createVerifiedProxyCatalog(target, input, output), subject);
                     } else {
                        this.fetchPasswordStore().findSettingsLinker().resolveParentLimboTracker().executeVerifiedServerAdapter(target, input, false);
                     }
                  };
                  return this.createVerifiedProxyCatalog(target, holder, ServerConnectType.WITH_LAST_SERVER, option);
               }
            }
            break;
         case WITH_PLATFORM_SERVER:
            if (PrimaryMessageOption.CURRENT_PRIMARYMESSAGEOPTION.ar()) {
               String element = (String)input.d(LenientMessageKind.ACTIVE_VERIFIED_LENIENTMESSAGEKIND);
               if (element != null && !this.isState(input, element) && this.checkState(element)) {
                  int payload = PrimaryMessageOption.MAIN_PRIMARYMESSAGEOPTION.r();
                  PlayerContract reference = payload <= 0 ? null : dataValue -> {
                     if (!dataValue) {
                        this.loadLinkedSessionHandler().createStrictCommandHandler(() -> this.createVerifiedProxyCatalog(target, input, output), payload);
                     } else {
                        this.fetchPasswordStore().findSettingsLinker().resolveParentLimboTracker().executeVerifiedServerAdapter(target, input, false);
                     }
                  };
                  return this.createVerifiedProxyCatalog(target, element, ServerConnectType.WITH_PLATFORM_SERVER, reference);
               }
            }
            break;
         case WITH_CONFIGURED_SERVER:
            if (!PrimaryMessageOption.CACHED_PRIMARYMESSAGEOPTION.ar()) {
               break;
            }

            List context = PrimaryMessageOption.STORED_PRIMARYMESSAGEOPTION.a(new Object[0]);
            if (context.isEmpty()) {
               break;
            }

            String data = this.handleMessage(target);
            if (data != null && context.stream().anyMatch(data::equalsIgnoreCase)) {
               break;
            }

            Set value = (Set)input.d(LenientMessageKind.ACTIVE_STORED_LENIENTMESSAGEKIND);
            List result;
            if (value != null) {
               ArrayList request = new ArrayList(context);
               synchronized (value) {
                  request.removeIf(value::contains);
               }

               if (request.isEmpty()) {
                  result = context;
                  input.loadObject(LenientMessageKind.ACTIVE_STORED_LENIENTMESSAGEKIND);
                  value = null;
               } else {
                  result = request;
               }
            } else {
               result = context;
            }

            String setting = (String)result.get(SecureLoginHandler.retrieveRandom().nextInt(result.size()));
            if (value != null) {
               synchronized (value) {
                  value.add(setting);
               }
            }

            int response = PrimaryMessageOption.MAIN_PRIMARYMESSAGEOPTION.r();
            PlayerContract source = response <= 0 ? null : resultValue -> {
               if (!resultValue) {
                  this.loadLinkedSessionHandler().createStrictCommandHandler(() -> {
                     if (context.size() > 1 && !input.isState(LenientMessageKind.ACTIVE_STORED_LENIENTMESSAGEKIND)) {
                        HashSet valueValue = new HashSet();
                        valueValue.add(setting);
                        input.updateLenientMessageKind(LenientMessageKind.ACTIVE_STORED_LENIENTMESSAGEKIND, valueValue);
                     }

                     this.createVerifiedProxyCatalog(target, input, output);
                  }, response);
               } else {
                  this.fetchPasswordStore().findSettingsLinker().resolveParentLimboTracker().executeVerifiedServerAdapter(target, input, false);
               }
            };
            return this.createVerifiedProxyCatalog(target, setting, ServerConnectType.WITH_CONFIGURED_SERVER, source);
         default:
            throw new IllegalArgumentException("Unsupported server connect type! " + output);
      }

      return VerifiedProxyCatalog.CURRENT_VERIFIEDPROXYCATALOG;
   }

   VerifiedProxyCatalog createVerifiedProxyCatalog(VerifiedServerAdapter target, String input, ServerConnectType output, @Nullable PlayerContract<Boolean> context);
}
