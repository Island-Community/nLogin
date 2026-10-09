package com.nickuc.login.storage.session;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.packet.QuickPacketAdapter;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import javax.annotation.Nullable;


public class LocalSessionTable {
   private final PasswordStore passwordStore;
   private final boolean enabled;
   private final boolean activeEnabled;
   private int count = -1;
   private final boolean pendingEnabled;

   public synchronized CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.getLoudPlayerContract().fetchCachedProxyCatalog();
   }

   @Nullable
   public synchronized LoudPlayerContract buildLoudPlayerContract(VerifiedServerAdapter target, LimboCoordinator input) {
      if (this.count + 1 >= CachedProxyCatalog.size()) {
         return null;
      } else {
         int output = ++this.count;
         LoudPlayerContract context = CachedProxyCatalog.handleCachedProxyCatalog(output, this.passwordStore.fetchLocalSettingsRepository().loadState())
            .loadLoudPlayerContract();
         if (context instanceof DirectPlayerContract && !this.activeEnabled) {
            return this.buildLoudPlayerContract(target, input);
         } else if (this.enabled && !(context instanceof QuickPacketAdapter)) {
            return this.buildLoudPlayerContract(target, input);
         } else {
            int data = context instanceof UpstreamAccountHandler && ((UpstreamAccountHandler)context).canState(this.passwordStore) ? 1 : 0;
            if ((!this.pendingEnabled || data != 0) && (this.pendingEnabled || data == 0)) {
               return !context.canState(this.passwordStore, target, input) ? this.buildLoudPlayerContract(target, input) : context;
            } else {
               return this.buildLoudPlayerContract(target, input);
            }
         }
      }
   }

   public LocalSessionTable(PasswordStore target, boolean input, boolean output, boolean context) {
      this.passwordStore = target;
      this.enabled = input;
      this.pendingEnabled = output;
      this.activeEnabled = context;
   }

   public LocalSessionTable findLocalSessionTable() {
      LocalSessionTable target = new LocalSessionTable(this.passwordStore, this.enabled, this.pendingEnabled, true);
      target.count = this.count;
      return target;
   }

   public synchronized LoudPlayerContract getLoudPlayerContract() {
      if (this.count == -1) {
         throw new IllegalStateException("Not initialized yet!");
      } else {
         return CachedProxyCatalog.handleCachedProxyCatalog(this.count, this.passwordStore.fetchLocalSettingsRepository().loadState()).loadLoudPlayerContract();
      }
   }
}
