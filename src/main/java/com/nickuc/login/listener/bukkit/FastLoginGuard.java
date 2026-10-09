package com.nickuc.login.listener.bukkit;

import com.nickuc.login.api.event.bukkit.auth.request.LoginRequestEvent;
import com.nickuc.login.api.event.internal.LockableEvent;
import com.nickuc.login.api.event.internal.LockableNewActionEvent;
import com.nickuc.login.auth.mojang.SecondaryMojangProcessor;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.storage.spawn.SpawnCollection;
import org.bukkit.entity.Player;

public class FastLoginGuard extends SecondaryMojangProcessor {
   public void lockableNewAction(LockableNewActionEvent<?> target) {
      BukkitPlatform input = this.spawnCollection.passwordStore.findObject();
      input.callEvent(target);
   }

   public void lockableEvent(LockableEvent target, byte input, byte output) {
      BukkitPlatform context = this.spawnCollection.passwordStore.findObject();
      if (input == 0) {
         LoginRequestEvent data = (LoginRequestEvent)target;
         Player value = data.getPlayer();
         if (!value.isOnline()) {
            return;
         }

         switch (output) {
            case 1:
               context.callEvent(data);
               break;
            case 2:
               this.spawnCollection
                  .passwordStore
                  .findSettingsLinker()
                  .retrievePacketCoordinator()
                  .saveVerifiedServerAdapter(this.spawnCollection.passwordStore.b().processVerifiedServerAdapter(value), data.isCancelled());
               break;
            default:
               throw new IllegalArgumentException("Unknown action " + input);
         }
      }
   }

   public FastLoginGuard(SpawnCollection target) {
      this.spawnCollection = target;
   }
}
