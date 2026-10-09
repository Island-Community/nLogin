package com.nickuc.login.storage.update;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.auth.login.CachedLoginBarrier;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.LinkedPasswordHashVerifier;
import com.nickuc.login.premium.UpdateLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.updater.AuthenticatedNoticeKind;
import com.nickuc.login.updater.NoticeCatalog;


public class PendingUpdateStore implements DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      LinkedPasswordHashVerifier data = target.a().loadLinkedPasswordHashVerifier();
      String value = data.getMessage();
      String result = target.getMessage();
      boolean request = target.a().loadLinkedPasswordHashVerifier().loadState();
      String response = !result.equals(value) ? String.format(" §f(v%s §a» §fv%s)", result, value) : "";
      AuthenticatedNoticeKind source = data.fetchAuthenticatedNoticeKind();
      if (source == null) {
         source = AuthenticatedNoticeKind.ACTIVE_AUTHENTICATEDNOTICEKIND;
      }

      String entry = CachedSettingsGateway.handleSettingsWriter(output.fetchLenientPremiumOption())
         .processMessage(source == AuthenticatedNoticeKind.AUTHENTICATED_NOTICE_KIND ? NoticeCatalog.NOTICE_CATALOG : NoticeCatalog.ACTIVE_NOTICECATALOG);
      CachedSettingsGateway.updateVerifiedServerAdapter(input, SharedLoginOption.INCOMING_SHAREDLOGINOPTION);
      CachedSettingsGateway.saveOutgoingSenderAdapter(input, request ? NoticeCatalog.SAFE_NOTICECATALOG : NoticeCatalog.FAST_NOTICECATALOG, response, entry);
      return request
         ? BusyLoginBarrier.resolveValues(input, PrivateLoginOption.PRIVATE_LOGIN_OPTION)
         : BusyLoginBarrier.resolveValues(input, PrivateLoginOption.ACTIVE_PRIVATELOGINOPTION, PrivateLoginOption.PENDING_PRIVATELOGINOPTION);
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      UpdateLookup context = target.a();
      LinkedPasswordHashVerifier data = context.loadLinkedPasswordHashVerifier();
      String value = data.getMessage();
      CachedLoginBarrier result = target.a().findCachedLoginBarrier();
      if (value == null) {
         result.dispatchState(true);
         return false;
      } else {
         return !target.a().loadPrimaryPasswordHashVerifier().hasState("autoUpdate", true)
            && (target.a().loadLinkedPasswordHashVerifier().loadState() || data.findState() && !result.getState());
      }
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      switch (data) {
         case PRIVATE_LOGIN_OPTION:
         case ACTIVE_PRIVATELOGINOPTION:
            InternalLoginOption.INTERNAL_LOGIN_OPTION.performVerifiedServerAdapter(input, output, "update", "confirm");
         case PENDING_PRIVATELOGINOPTION:
            DirectPlayerContract.super.a(target, input, output, context, data);
      }
   }

   public PendingUpdateStore(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }
}
