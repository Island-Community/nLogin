package com.nickuc.login.storage.proxy;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.server.ServerAdapter;
import com.nickuc.login.platform.proxy.StoredProxyCatalog;
import com.nickuc.login.platform.message.TightMessageKind;
import com.nickuc.login.premium.FloodgateResolver;
import com.nickuc.login.proxy.ProxyForwarder;
import com.nickuc.login.proxy.SecondaryProxyBridge;
import com.nickuc.login.proxy.StrictSpawnGateway;
import com.nickuc.login.security.hashing.PasswordHashProvider;


public class ProxyRepository implements ServerAdapter {
   private final BukkitPlatform BukkitPlatform;

   @Override
   public void executeTask() {
   }

   public ProxyRepository(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }

   @Override
   public boolean retrieveState() {
      return TightMessageKind.canState(this.BukkitPlatform, true);
   }

   @Override
   public void performTask() {
      PasswordStore target = this.BukkitPlatform.fetchPasswordStore();
      this.BukkitPlatform
         .b()
         .fetchCollection()
         .forEach(instance -> instance.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.SHARED_LOUDPROXYSTATE, instance)));
      StoredProxyCatalog.saveNLoginBukkit(this.BukkitPlatform, true);
      this.BukkitPlatform.resolveLimboSupervisor().sendTask();
   }

   @Override
   public FloodgateResolver fetchFloodgateResolver() {
      return new PasswordHashProvider((PasswordStore)this.BukkitPlatform.b(), this.BukkitPlatform, true);
   }

   @Override
   public nLoginAPI loadNLoginAPI() {
      return new StrictSpawnGateway(this.BukkitPlatform);
   }

   @Override
   public InternalAccountHandler getInternalAccountHandler() {
      return new SecondaryProxyBridge(this.BukkitPlatform);
   }

   public ProxyForwarder resolveProxyForwarder() {
      return new ProxyForwarder(this.BukkitPlatform);
   }
}
