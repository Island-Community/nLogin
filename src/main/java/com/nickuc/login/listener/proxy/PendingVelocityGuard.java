package com.nickuc.login.listener.proxy;

import com.nickuc.login.api.event.internal.LockableEvent;
import com.nickuc.login.api.event.internal.LockableNewActionEvent;
import com.nickuc.login.api.event.velocity.auth.request.LoginRequestEvent;
import com.nickuc.login.auth.mojang.SecondaryMojangProcessor;
import com.nickuc.login.velocity.VelocityPlatform;
import com.nickuc.login.storage.spawn.SpawnArchive;
import com.velocitypowered.api.proxy.Player;

public class PendingVelocityGuard extends SecondaryMojangProcessor {
   public void lockableEvent(LockableEvent target, byte input, byte output) {
      VelocityPlatform context = this.spawnArchive.passwordStore.findObject();
      if (input == 0) {
         LoginRequestEvent data = (LoginRequestEvent)target;
         Player value = data.getPlayer();
         switch (output) {
            case 1:
               context.callEvent(data);
               break;
            case 2:
               this.spawnArchive
                  .passwordStore
                  .findSettingsLinker()
                  .retrievePacketCoordinator()
                  .saveVerifiedServerAdapter(this.spawnArchive.passwordStore.b().processVerifiedServerAdapter(value), data.isCancelled());
               break;
            default:
               throw new IllegalArgumentException("Unknown action " + input);
         }
      }
   }

   public PendingVelocityGuard(SpawnArchive target) {
      this.spawnArchive = target;
   }

   public void lockableNewAction(LockableNewActionEvent<?> target) {
      VelocityPlatform input = this.spawnArchive.passwordStore.findObject();
      input.callEvent(target);
   }
}
