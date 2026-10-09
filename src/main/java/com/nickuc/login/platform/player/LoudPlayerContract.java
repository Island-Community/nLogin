package com.nickuc.login.platform.player;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.api.enums.LoginType;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.session.LocalSessionTable;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.session.SessionGateway;
import java.util.concurrent.TimeUnit;

public interface LoudPlayerContract {
   default void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      this.handlePasswordStore(target, input, output);
   }

   default int buildCount(boolean target) {
      return target ? 1500 : 0;
   }

   default void savePasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, boolean context) {
      SecondaryAccountHandler data = output.getSecondaryAccountHandler();
      this.processPasswordStore(target, input, output, data);
      int value = this.buildCount(context);
      if (value > 0) {
         target.processLinkedSessionHandler(true).loadStrictCommandHandler(() -> {
            if (input.loadState()) {
               if (this.hasState(output)) {
                  data.dispatchCount(this.fetchCachedProxyCatalog().findCount(), this.loadValues(target, input, output, data));
               }
            }
         }, value, TimeUnit.MILLISECONDS);
      } else {
         data.dispatchCount(this.fetchCachedProxyCatalog().findCount(), this.loadValues(target, input, output, data));
      }
   }

   CachedProxyCatalog fetchCachedProxyCatalog();

   default void handlePasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      LocalSessionTable context = (LocalSessionTable)output.loadObject(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
      if (context == null) {
         throw new IllegalStateException("Notification session not loaded for " + input.getName() + "!");
      }

      LoudPlayerContract data = context.buildLoudPlayerContract(input, output);
      if (data == null) {
         output.loadObject(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
         if (!(this instanceof SessionGateway) && (!(this instanceof UpstreamAccountHandler) || !((UpstreamAccountHandler)this).canState(target))) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.ACTIVE_OPEN_LOUDPROXYSTATE);
         }
      }

      if (this instanceof UpstreamAccountHandler && !(data instanceof UpstreamAccountHandler)) {
         UpstreamAccountHandler value = (UpstreamAccountHandler)this;
         if (value.at()) {
            output.loadObject(LenientMessageKind.SECURE_LENIENTMESSAGEKIND);
            output.loadObject(LenientMessageKind.OPEN_LENIENTMESSAGEKIND);
            input.handleMessage("");
         }

         if (!value.canState(target)) {
            input.executeTask();
            if (data != null) {
               output.updateLenientMessageKind(LenientMessageKind.ACTIVE_LOCAL_LENIENTMESSAGEKIND, data);
            }

            target.findSettingsLinker()
               .resolveParentLimboTracker()
               .saveSpawnLookup(
                  output.loadSpawnLookup(),
                  input,
                  output,
                  (LoginType)output.loadObject(LenientMessageKind.SECONDARY_LENIENTMESSAGEKIND),
                  (String)output.loadObject(LenientMessageKind.LINKED_LENIENTMESSAGEKIND)
               );
            return;
         }

         output.executeTightPlatformCatalog(TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG, TightPlatformCatalog.PENDING_TIGHTPLATFORMCATALOG);
         target.findSettingsLinker().retrievePacketCoordinator().sendVerifiedServerAdapter(input, output);
      }

      if (data != null) {
         data.sendPasswordStore(target, input, output);
      }
   }

   default void processPasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
   }

   boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output);

   default void sendPasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      this.savePasswordStore(target, input, output, false);
   }

   default boolean hasState(LimboCoordinator target) {
      LocalSessionTable input = (LocalSessionTable)target.loadObject(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
      return input != null && input.getLoudPlayerContract().fetchCachedProxyCatalog() == this.fetchCachedProxyCatalog();
   }

   BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context);
}
