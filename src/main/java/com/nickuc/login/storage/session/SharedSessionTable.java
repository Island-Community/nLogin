package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.platform.packet.QuickPacketAdapter;
import com.nickuc.login.platform.listener.RootListenerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;


public abstract class SharedSessionTable implements UpstreamAccountHandler, QuickPacketAdapter {
   private final CachedProxyCatalog cachedProxyCatalog;
   private final LoudProxyState loudProxyState;
   private final StrictMessageKind strictMessageKind;

   @Override
   public boolean at() {
      return true;
   }

   @Override
   public boolean canState(PasswordStore target) {
      return false;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }

   public SharedSessionTable(CachedProxyCatalog target, StrictMessageKind input, LoudProxyState output) {
      this.cachedProxyCatalog = target;
      this.strictMessageKind = input;
      this.loudProxyState = output;
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      SpawnLookup data = output.loadSpawnLookup();
      RootListenerContract value = this.strictMessageKind.processRootListenerContract(target);
      String result = this.strictMessageKind.computeMessage(data);
      target.verifyState(EventEnum.TWO_FACTOR_REQUEST, TwoFactorType.convert(this.strictMessageKind), input, result);
      output.updateLenientMessageKind(LenientMessageKind.OPEN_LENIENTMESSAGEKIND, SpawnState.ACTIVE_PENDING_SPAWNSTATE.r() * 5);
      value.performSpawnLookup(data, input);
      String request = CachedSettingsGateway.computeMessage(this.loudProxyState, input).replace("@address", input.resolveMessage());
      context.executeMessage(request);
      return new BusyLoginBarrier[0];
   }

   public StrictMessageKind getStrictMessageKind() {
      return this.strictMessageKind;
   }

   @Override
   public boolean canState(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (!this.strictMessageKind.hasState(target)) {
         return false;
      }

      RootListenerContract context = this.strictMessageKind.processRootListenerContract(target);
      if (!context.findState()) {
         return false;
      }

      SpawnLookup data = output.loadSpawnLookup();
      if (!input.i("nlogin.force." + this.strictMessageKind.getName())) {
         String value = data.findMessage();
         if (value == null || value.equals(input.resolveMessage())) {
            return false;
         }
      }

      if (Boolean.TRUE.equals(output.loadObject(LenientMessageKind.ACTIVE_PENDING_LENIENTMESSAGEKIND))) {
         return false;
      }

      String result = this.strictMessageKind.computeMessage(data);
      return result != null && this.strictMessageKind.validateState(data);
   }
}
