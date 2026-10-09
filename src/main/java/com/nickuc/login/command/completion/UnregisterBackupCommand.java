package com.nickuc.login.command.completion;

import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordHashVerifier;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.ArrayList;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.TabCompleteEvent;

public class UnregisterBackupCommand implements PacketAdapter {
   private final PasswordStore passwordStore;

   public UnregisterBackupCommand(PasswordStore target) {
      this.passwordStore = target;
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void updateTabCompleteEvent(TabCompleteEvent target) {
      CommandSender input = target.getSender();
      if (input instanceof Player) {
         if (!target.getCompletions().isEmpty()) {
            if (target.getBuffer().startsWith("/")) {
               VerifiedServerAdapter output = this.passwordStore.b().processVerifiedServerAdapter(input);
               int context = !output.i("nlogin.command.nlogin") && !output.i("nlogin.admin") ? 0 : 1;
               ArrayList data = new ArrayList(target.getCompletions());
               data.removeIf(outputValue -> {
                  if (outputValue.trim().isEmpty()) {
                     return false;
                  }

                  if (outputValue.charAt(0) != '/') {
                     return false;
                  }

                  String[] contextValue = outputValue.split(" ");
                  String dataValue = contextValue[0].toLowerCase(Locale.ENGLISH);
                  if (!context && dataValue.equals("/nlogin")) {
                     return true;
                  }

                  if (this.passwordStore.loadLimboRegistry().canState(output)) {
                     return false;
                  }

                  IndirectPasswordHashVerifier value = this.passwordStore.findIndirectPasswordHashVerifier();
                  return value == null || value.validateState(dataValue);
               });
               target.setCompletions(data);
            }
         }
      }
   }
}
