package com.nickuc.login.tasks;

import com.nickuc.login.platform.account.ParentAccountHandler;
import com.nickuc.login.platform.command.StrictCommandHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.spawn.PremiumOption;
import com.nickuc.login.storage.password.PasswordHashStore;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.tasks.LoginMainQueueTask;
import java.util.concurrent.TimeUnit;

public class LoginJob implements Runnable {
   private final VerifiedServerAdapter verifiedServerAdapter;
   private final ParentAccountHandler parentAccountHandler;
   private final PasswordStore passwordStore;
   private final String name;
   private final StrictCommandHandler strictCommandHandler;
   private final boolean enabled;

   public LoginJob(PasswordStore target, VerifiedServerAdapter input, boolean output) {
      StringBuilder context = new StringBuilder();
      byte data = 1;

      for (PremiumOption response : PremiumOption.values()) {
         if (!response.retrieveState()) {
            if (data == 0) {
               context.append("  ");
            } else {
               data = 0;
            }

            context.append("§7").append(response.name).append(": %s");
         }
      }

      this.name = context.toString();
      this.passwordStore = target;
      this.verifiedServerAdapter = input;
      this.enabled = output;
      this.parentAccountHandler = target.b();
      this.strictCommandHandler = target.processLinkedSessionHandler(true).processStrictCommandHandler(this, 1000L, 50L, TimeUnit.MILLISECONDS);
   }

   public void updateTask() {
      if (!this.passwordStore.resolveState()) {
         this.strictCommandHandler.performTask();
      } else if (this.verifiedServerAdapter.loadState() && PasswordHashStore.getSet().contains(this.verifiedServerAdapter.getName())) {
         int target = LoginMainQueueTask.retrieveCount();
         if (target > 0) {
            this.verifiedServerAdapter.saveMessage("", "§eLogin process running for " + target + " " + (target == 1 ? "player" : "players") + "...", 0, 40, 20);
         }

         PremiumOption[] input = PremiumOption.values();
         String[] output = new String[input.length];

         for (int context = 0; context < input.length; context++) {
            String data = this.enabled ? input[context].loadMessage(TimeUnit.MILLISECONDS, 2) : input[context].resolveMessage(TimeUnit.MILLISECONDS, 2);
            output[context] = data + "ms";
         }

         this.verifiedServerAdapter.handleMessage(String.format(this.name, output));
      } else {
         PasswordHashStore.getSet().remove(this.verifiedServerAdapter.getName());
         this.strictCommandHandler.performTask();
      }
   }
}
