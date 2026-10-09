package com.nickuc.login.storage.settings;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import java.sql.ResultSet;

public abstract class InternalSettingsGateway extends PasswordHashRepository {
   private final String name;
   public String activeName;

   public InternalSettingsGateway(PasswordStore target, MessageOption input, String output, boolean context) {
      super(target, input, context);
      this.name = output;
   }

   public abstract void dispatchPasswordHashLoader(PasswordHashLoader target);

   @Override
   public void handleOutgoingSenderAdapter(OutgoingSenderAdapter target) {
      PasswordHashLoader input = new PasswordHashLoader(this.name, this.retrieveFile());
      this.dispatchPasswordHashLoader(input);
      if (this.activeName != null) {
         this.processTask();
      } else {
         this.performPasswordHashLoader(input);
      }

      this.sendOutgoingSenderAdapter(target);
   }

   public abstract void performPasswordHashLoader(PasswordHashLoader target);

   private void processTask() {
      if (this.activeName == null) {
         throw new IllegalArgumentException("Table name cannot be null!");
      }

      if (this.activeName.isEmpty()) {
         throw new IllegalArgumentException("Table name cannot be empty!");
      }

      this.processMessage(this.activeName);
      PrimaryLoginHandler target = this.sharedListenerContract.buildPrimaryLoginHandler("SELECT * FROM `" + this.activeName + "`");

      try {
         ResultSet input = target.resolveObject();

         while (input.next() && this.passwordStore.resolveState()) {
            try {
               this.dispatchSet(input);
            } finally {
               this.pendingTimestamp++;
            }
         }
      } catch (Throwable response) {
         if (target != null) {
            try {
               target.close();
            } catch (Throwable result) {
               response.addSuppressed(result);
            }
         }

         throw response;
      }

      if (target != null) {
         target.close();
      }
   }

   public abstract void dispatchSet(ResultSet target);
}
