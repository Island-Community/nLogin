package com.nickuc.login.listener.bukkit;

import com.nickuc.login.config.PasswordHashContainer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.Entity;

public class SettingsGuard {
   private boolean enabled = true;

   public boolean canState(Entity target) {
      if (this.enabled) {
         try {
            return CitizensAPI.getNPCRegistry().isNPC(target);
         } catch (Throwable output) {
            this.enabled = false;
            if (output.getCause() instanceof ClassNotFoundException) {
               PasswordHashContainer.performMessage("Unable to verify that the player is an NPC. This is a Citizens error: %s", output.getMessage());
            } else {
               PasswordHashContainer.processMessage("Unable to verify that the player is an NPC. Most likely this is a Citizens error.", output);
            }
         }
      }

      return false;
   }
}
