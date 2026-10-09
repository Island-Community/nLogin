package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.platform.packet.QuickPacketAdapter;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;


public class FastSessionSource implements UpstreamAccountHandler, QuickPacketAdapter {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public boolean at() {
      return false;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      return target.getParentDiscordNotifier().getState();
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      input.executeTask();
      return new BusyLoginBarrier[0];
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public boolean loadState() {
      return false;
   }

   public FastSessionSource(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }

   @Override
   public boolean canState(PasswordStore target) {
      return false;
   }
}
