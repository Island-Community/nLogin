package com.nickuc.login.storage.login;

import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

public class LoginStore {
   public static int count = -1;

   public static void handlePasswordStore(PasswordStore instance, int target) {
      if (target <= 0) {
         throw new IllegalArgumentException("Seconds must be greater than 0!");
      }

      if (-1 != -1) {
         count = -1;
      } else {
         count = target + 1;
         instance.processLinkedSessionHandler(true).loadStrictCommandHandler(targetValue -> {
            if (!instance.resolveState()) {
               targetValue.performTask();
            } else {
               Collection input = instance.b().fetchCollection();
               if (-1 == -1) {
                  input.forEach(instanceValue -> instanceValue.saveMessage("§c§lRESTART", "§cRestart cancelled.", 0, 30, 6));
                  targetValue.performTask();
               } else {
                  if (-1 > 0) {
                     count = -2;
                  }

                  int output = -1 == 0 ? 1 : 0;
                  int context = instance.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE ? 1 : 0;

                  for (VerifiedServerAdapter value : input) {
                     if (output != 0) {
                        if (context != 0) {
                           try {
                              value.<Player>findObject().closeInventory();
                           } catch (Exception request) {
                           }
                        }

                        value.buildCompletableFuture("§cThe server is restarting...");
                     } else {
                        value.saveMessage("§c§lRESTART", "§cServer restarts in " + -1 + " " + (-1 == 1 ? "second" : "seconds"), 0, 30, 6);
                     }
                  }

                  if (output != 0) {
                     instance.b().executeTask();
                     targetValue.performTask();
                  }
               }
            }
         }, 0L, 1L, TimeUnit.SECONDS);
      }
   }
}
