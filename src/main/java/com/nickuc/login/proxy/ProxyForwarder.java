package com.nickuc.login.proxy;

import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.BusyLimboStore;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.PacketCoordinator;
import com.nickuc.login.session.ParentLimboTracker;
import javax.annotation.Nullable;

public class ProxyForwarder extends BusyLimboStore {
   public ProxyForwarder(BukkitPlatform target) {
      super(target);
   }

   @Override
   public void saveSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output) {
      throw new UnsupportedOperationException("Unsupported method in proxy mode!");
   }

   @Override
   public void sendSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output, String context, boolean data, boolean value) {
      throw new UnsupportedOperationException("Unsupported method in proxy mode!");
   }

   @Override
   public ParentLimboTracker resolveParentLimboTracker() {
      throw new UnsupportedOperationException("Unsupported method in proxy mode!");
   }

   @Override
   public void saveSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, boolean output, boolean context) {
      throw new UnsupportedOperationException("Unsupported method in proxy mode!");
   }

   @Override
   public PacketCoordinator retrievePacketCoordinator() {
      throw new UnsupportedOperationException("Unsupported method in proxy mode!");
   }

   @Override
   public void executeSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output) {
      throw new UnsupportedOperationException("Unsupported method in proxy mode!");
   }

   @Override
   public void processSpawnLookup(
      SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output, String context, @Nullable String data, boolean value, boolean result
   ) {
      throw new UnsupportedOperationException("Unsupported method in proxy mode!");
   }
}
