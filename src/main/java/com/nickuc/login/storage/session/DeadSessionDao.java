package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.login.PrivateLoginCheckpoint;
import com.nickuc.login.auth.login.SafeLoginBarrier;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.LinkedPasswordHashVerifier;
import com.nickuc.login.session.LimboCoordinator;


public class DeadSessionDao implements UpstreamAccountHandler, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      LinkedPasswordHashVerifier context = target.a().loadLinkedPasswordHashVerifier();
      return this.canState(context, output.loadState() ? "pt" : "en");
   }

   private boolean canState(LinkedPasswordHashVerifier target, String input) {
      String output = target.handleObject("notify." + input);
      if (output != null && !output.isEmpty()) {
         int context = PrivateLoginCheckpoint.handleInteger(target.handleObject("notify." + input + ".chance"), -1);
         return context == -1 || SecureLoginHandler.retrieveRandom().nextInt(100) <= context;
      } else {
         return false;
      }
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      LinkedPasswordHashVerifier data = target.a().loadLinkedPasswordHashVerifier();
      boolean value = output.loadState();
      String result = data.buildObject("notify." + (value ? "pt" : "en"), "");
      String request = LocalLocaleFlow.loadMessage(SafeLoginBarrier.computeMessage(result).replace("\\n", "\n"));
      String response = data.handleObject("notify." + (value ? "pt" : "en") + ".link");
      if (response != null && !response.isEmpty()) {
         context.executeMessage(request, SafeLoginBarrier.computeMessage(response));
      } else {
         context.executeMessage(request);
      }

      return BusyLoginBarrier.resolveValues(input, PrivateLoginOption.PRIVATE_LOGIN_OPTION);
   }

   @Override
   public boolean canState(PasswordStore target) {
      return false;
   }

   @Override
   public boolean at() {
      return false;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   public DeadSessionDao(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }
}
