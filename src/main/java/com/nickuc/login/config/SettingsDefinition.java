package com.nickuc.login.config;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.StoredLoginFlow;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.storage.password.PasswordHashRepository;
import com.nickuc.login.storage.password.PasswordStore;
import java.io.File;
import java.util.List;

public class SettingsDefinition extends PasswordHashRepository {
   private final String name;

   private String processMessage(String target) {
      target = target.trim();
      if (target.length() >= 2 && target.charAt(0) == ' ') {
         target = target.substring(1);
      }

      char input = target.charAt(0);
      char output = target.charAt(target.length() - 1);
      if (target.length() >= 3 && (input == '\'' && output == '\'' || input == '"' && output == '"')) {
         target = target.substring(1, target.length() - 1);
      }

      return target;
   }

   public SettingsDefinition(PasswordStore target, MessageOption input, String output, boolean context) {
      super(target, input, context);
      this.name = output;
   }

   public void processMessage(String target, String input) {
      this.updateMessage(target, input, null, null);
   }

   public SettingsDefinition(PasswordStore target, MessageOption input) {
      this(target, input, "config.yml");
   }

   @Override
   public void handleOutgoingSenderAdapter(OutgoingSenderAdapter target) {
      File input = new File(this.retrieveFile(), this.name);
      List output = StoredLoginFlow.resolveCollection(input);
      this.timestamp = output.size();

      for (String data : output) {
         Object value = null;

         try {
            String[] result = data.split(":");
            if (result.length >= 2) {
               this.processMessage(result[0], this.processMessage(result[1]));
            }
         } catch (Exception entry) {
            PasswordHashContainer.processMessage(
               "[" + this.messageOption.getName() + "] " + (value == null ? "Unknown" : value + "'s") + " data could not be converted.", entry
            );
         } finally {
            this.pendingTimestamp++;
         }
      }

      output.clear();
      this.sendOutgoingSenderAdapter(target);
   }

   public SettingsDefinition(PasswordStore target, MessageOption input, String output) {
      this(target, input, output, true);
   }
}
