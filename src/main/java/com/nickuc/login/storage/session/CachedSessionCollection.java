package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.platform.packet.QuickPacketAdapter;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;


public class CachedSessionCollection implements UpstreamAccountHandler, QuickPacketAdapter {
   private final CachedProxyCatalog cachedProxyCatalog;

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.PENDING_CURRENT_LOUDPROXYSTATE);
      return new BusyLoginBarrier[0];
   }

   @Override
   public boolean at() {
      return false;
   }

   @Override
   public boolean canState(PasswordStore target) {
      return false;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      String context = (String)output.d(LenientMessageKind.LINKED_LENIENTMESSAGEKIND);
      if (context == null) {
         return false;
      } else if (SpawnState.ACTIVE_DIRECT_SPAWNSTATE.ar() && SpawnState.ACTIVE_LINKED_SPAWNSTATE.ar()) {
         SpawnLookup data = output.loadSpawnLookup();
         return !data.retrieveState() ? false : data.resolveState() || !Pbkdf2Linker.findPattern().matcher(context).matches();
      } else {
         return false;
      }
   }

   public CachedSessionCollection(CachedProxyCatalog target) {
      this.cachedProxyCatalog = target;
   }
}
