package com.nickuc.login.storage.session;

import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import java.util.concurrent.TimeUnit;

public class ActiveSessionRepository {
   private final PasswordStore passwordStore;

   public LoudPlayerContract computeLoudPlayerContract(VerifiedServerAdapter target, LimboCoordinator input) {
      return this.processLoudPlayerContract(target, input, false);
   }

   public ActiveSessionRepository(PasswordStore target) {
      this.passwordStore = target;
   }

   private LoudPlayerContract processLoudPlayerContract(VerifiedServerAdapter target, LimboCoordinator input, boolean output) {
      boolean context = target.i("nlogin.admin");
      input.loadObject(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND);
      boolean data = input.d(LenientMessageKind.PRIMARY_LENIENTMESSAGEKIND);
      LocalSessionTable value = new LocalSessionTable(this.passwordStore, data, output, context);
      LoudPlayerContract result = value.buildLoudPlayerContract(target, input);
      if (result == null) {
         return null;
      }

      input.updateLenientMessageKind(LenientMessageKind.PRIVATE_LENIENTMESSAGEKIND, value);
      if (!output && !(result instanceof UpstreamAccountHandler)) {
         input.updateLenientMessageKind(LenientMessageKind.ACTIVE_LOCAL_LENIENTMESSAGEKIND, result);
      } else {
         result.savePasswordStore(this.passwordStore, target, input, true);
         if (result instanceof UpstreamAccountHandler) {
            if (!output) {
               input.updateLenientMessageKind(LenientMessageKind.SAFE_LENIENTMESSAGEKIND, true);
            }

            UpstreamAccountHandler request = (UpstreamAccountHandler)result;
            if (request.at()) {
               input.updateLenientMessageKind(LenientMessageKind.SECURE_LENIENTMESSAGEKIND, 0);
            }

            if (request.loadState()) {
               this.passwordStore.processLinkedSessionHandler(true).loadStrictCommandHandler(outputValue -> {
                  if (target.loadState() && request.a(input)) {
                     CachedSettingsGateway.updateVerifiedServerAdapter(target, SharedLoginOption.SHARED_SHAREDLOGINOPTION);
                  } else {
                     outputValue.performTask();
                  }
               }, 20L, 20L, TimeUnit.SECONDS);
            }
         }
      }

      return result;
   }

   public boolean isState(VerifiedServerAdapter target, LimboCoordinator input) {
      return this.processLoudPlayerContract(target, input, true) != null;
   }
}
