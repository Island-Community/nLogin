package com.nickuc.login.session;

import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.limbo.LimboStore;
import com.nickuc.login.limbo.SpawnSession;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SettingsLinker;
import com.nickuc.login.storage.spawn.SpawnState;
import com.nickuc.login.tasks.limbo.DelayedPlayerLimboClearTask;
import com.nickuc.login.tasks.limbo.PlayerLimboClearTask;
import com.nickuc.login.tasks.limbo.PlayerLimboRestoreTask;
import java.util.concurrent.TimeUnit;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public class BusyLimboStore extends SettingsLinker {
   private final BukkitPlatform BukkitPlatform;
   private final SpawnSession spawnSession;

   public BusyLimboStore(BukkitPlatform target) {
      super(target.fetchPasswordStore());
      this.BukkitPlatform = target;
      this.spawnSession = new SpawnSession(target);
   }

   @Override
   public void saveVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, boolean output) {
      LimboStore context = (LimboStore)input.d(LenientMessageKind.LOCAL_LENIENTMESSAGEKIND);
      if (context == null) {
         target.buildCompletableFuture("§4[nLogin] Severe internal error detected. Please report to an admin.");
         PasswordHashContainer.updateMessage("Limbo was not defined in the " + target.getName() + " player session!");
      } else {
         try {
            this.spawnSession.validateState(input, context, output);
         } catch (Exception result) {
            target.buildCompletableFuture("§4[nLogin] Severe internal error detected. Please report to an admin.");
            PasswordHashContainer.handleMessage("Severe error during limbo process when saving player stats. (" + target.getName() + ")", result);
            return;
         }

         Runnable data = () -> {
            byte contextValue = 0;

            try {
               contextValue = this.spawnSession.validateState(input, context);
            } catch (Exception valueValue) {
               PasswordHashContainer.handleMessage("Severe error during limbo process when cleaning player stats. (" + target.getName() + ")", valueValue);
            }

            if (contextValue == 0) {
               target.buildCompletableFuture("§4[nLogin] Severe internal error detected. Please report to an admin.");
            }
         };
         int value = SpawnState.LINKED_SPAWNSTATE.r() * 50;
         if (value <= 0) {
            new PlayerLimboClearTask(data).updateTask();
         } else {
            target.getLinkedSessionHandler().loadStrictCommandHandler(new DelayedPlayerLimboClearTask(data), value, TimeUnit.MILLISECONDS);
         }
      }
   }

   @Override
   public void executeVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input) {
      input.loadObject(LenientMessageKind.SAFE_LENIENTMESSAGEKIND);
      LimboStore output = (LimboStore)input.d(LenientMessageKind.LOCAL_LENIENTMESSAGEKIND);
      if (output == null) {
         target.<Player>findObject().updateInventory();
      } else {
         Server context = (Server)this.BukkitPlatform.b().c();
         if (context.isPrimaryThread() && !BungeeWriter.fetchState()) {
            this.spawnSession.performLimboCoordinator(input, output, false);
         } else {
            target.getLinkedSessionHandler()
               .buildStrictCommandHandler(new PlayerLimboRestoreTask(() -> this.spawnSession.performLimboCoordinator(input, output, false)));
         }
      }
   }

   @Override
   public boolean isState(LimboCoordinator target, String input, boolean output) {
      if (this.BukkitPlatform.getServer().isPrimaryThread()) {
         throw new IllegalStateException(input + " cannot be performed on the primary thread!");
      } else {
         return super.isState(target, input, output);
      }
   }

   public SpawnSession loadSpawnSession() {
      return this.spawnSession;
   }
}
