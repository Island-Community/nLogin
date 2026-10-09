package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import java.io.File;

public abstract class SettingsGateway extends PasswordHashRepository {
   private final String name;

   public abstract void saveFile(File target);

   public SettingsGateway(PasswordStore target, MessageOption input, String output, boolean context) {
      super(target, input, context);
      this.name = output;
   }

   @Override
   public void handleOutgoingSenderAdapter(OutgoingSenderAdapter target) {
      File input = new File(this.retrieveFile(), this.name);
      if (input.isDirectory()) {
         File[] output = input.listFiles();
         if (output == null) {
            if (target != null) {
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cUnable to retrieve player data files.");
            }
         } else {
            this.timestamp = output.length;

            for (File result : output) {
               try {
                  this.saveFile(result);
               } catch (Exception record) {
                  PasswordHashContainer.processMessage("[" + this.messageOption.getName() + "] File " + result.getName() + " could not be converted.", record);
               } finally {
                  this.pendingTimestamp++;
               }
            }

            this.sendOutgoingSenderAdapter(target);
         }
      }
   }
}
