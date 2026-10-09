package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.platform.packet.QuickPacketAdapter;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;


public abstract class SessionTable implements UpstreamAccountHandler, QuickPacketAdapter {
   private final LoudProxyState loudProxyState;
   private final CachedProxyCatalog cachedProxyCatalog;
   private final StrictMessageKind strictMessageKind;

   @Override
   public void handlePasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      SpawnLookup context = output.loadSpawnLookup();
      if (this.strictMessageKind.computeMessage(context) != null) {
         UpstreamAccountHandler.super.b(target, input, output);
      }
   }

   public SessionTable(CachedProxyCatalog target, StrictMessageKind input, LoudProxyState output) {
      this.cachedProxyCatalog = target;
      this.strictMessageKind = input;
      this.loudProxyState = output;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   public StrictMessageKind getStrictMessageKind() {
      return this.strictMessageKind;
   }

   @Override
   public boolean at() {
      return true;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (!this.strictMessageKind.hasState(target)) {
         return false;
      } else if (!this.strictMessageKind.getState()) {
         return false;
      } else {
         SpawnLookup context = output.loadSpawnLookup();
         if (context.fetchStateAndState() && !this.strictMessageKind.loadState()) {
            return false;
         } else if (context.getState() && !this.strictMessageKind.findState()) {
            return false;
         } else {
            return input.i("nlogin.bypass." + this.strictMessageKind.getName()) ? false : this.strictMessageKind.computeMessage(context) == null;
         }
      }
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.executeOutgoingSenderAdapter(input, this.loudProxyState);
      return new BusyLoginBarrier[0];
   }

   @Override
   public boolean canState(PasswordStore target) {
      return false;
   }
}
