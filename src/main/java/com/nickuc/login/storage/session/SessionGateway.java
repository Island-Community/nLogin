package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import java.util.List;


public class SessionGateway implements LoudPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return output.isState(LenientMessageKind.AUTHENTICATED_LENIENTMESSAGEKIND);
   }

   public SessionGateway(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      List data = output.loadObject(LenientMessageKind.AUTHENTICATED_LENIENTMESSAGEKIND);
      if (data != null) {
         data.forEach(inputValue -> target.resolveRootMessageHandler().hasState(input, inputValue));
      }

      LoudPlayerContract.super.handlePasswordStore(target, input, output);
      return new BusyLoginBarrier[0];
   }
}
