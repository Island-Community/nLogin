package com.nickuc.login.listener.bukkit;

import com.nickuc.login.api.event.bukkit.auth.request.LoginRequestEvent;
import com.nickuc.login.api.event.internal.LockableEvent;
import com.nickuc.login.api.event.internal.LockableNewActionEvent;
import com.nickuc.login.auth.mojang.SecondaryMojangProcessor;
import com.nickuc.login.proxy.StrictSpawnGateway;
import org.bukkit.entity.Player;

public class ProxyGuard extends SecondaryMojangProcessor {
   public void lockableEvent(LockableEvent target, byte input, byte output) {
      if (input == 0) {
         LoginRequestEvent context = (LoginRequestEvent)target;
         Player data = context.getPlayer();
         if (!data.isOnline()) {
            return;
         }

         switch (output) {
            case 1:
               StrictSpawnGateway.buildNLoginBukkit(this.strictSpawnGateway).callEvent(context);
            case 2:
               break;
            default:
               throw new IllegalArgumentException("Unknown action " + input);
         }
      }
   }

   public ProxyGuard(StrictSpawnGateway target) {
      this.strictSpawnGateway = target;
   }

   public void lockableNewAction(LockableNewActionEvent<?> target) {
   }
}
