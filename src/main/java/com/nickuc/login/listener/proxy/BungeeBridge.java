package com.nickuc.login.listener.proxy;

import com.nickuc.login.api.event.bungee.auth.request.LoginRequestEvent;
import com.nickuc.login.api.event.internal.LockableEvent;
import com.nickuc.login.api.event.internal.LockableNewActionEvent;
import com.nickuc.login.auth.mojang.SecondaryMojangProcessor;
import com.nickuc.login.proxy.SpawnGateway;
import com.nickuc.login.bungee.BungeePlatform;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeBridge extends SecondaryMojangProcessor {
   public BungeeBridge(SpawnGateway target) {
      this.spawnGateway = target;
   }

   public void lockableNewAction(LockableNewActionEvent<?> target) {
      BungeePlatform input = this.spawnGateway.passwordStore.findObject();
      input.callEvent(target);
   }

   public void lockableEvent(LockableEvent target, byte input, byte output) {
      BungeePlatform context = this.spawnGateway.passwordStore.findObject();
      if (input == 0) {
         LoginRequestEvent data = (LoginRequestEvent)target;
         ProxiedPlayer value = data.getPlayer();
         switch (output) {
            case 1:
               context.callEvent(data);
               break;
            case 2:
               this.spawnGateway
                  .passwordStore
                  .findSettingsLinker()
                  .retrievePacketCoordinator()
                  .saveVerifiedServerAdapter(this.spawnGateway.passwordStore.b().processVerifiedServerAdapter(value), data.isCancelled());
               break;
            default:
               throw new IllegalArgumentException("Unknown action " + input);
         }
      }
   }
}
