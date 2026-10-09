package com.nickuc.login.auth.settings;

import com.nickuc.login.config.PasswordHashContainer;
import java.util.logging.Filter;
import java.util.logging.LogRecord;


public final class SettingsHandler implements Filter {
   private final Filter filter;
   private boolean enabled;
   private static final Object[] values = new Object[0];

   @Override
   public boolean isLoggable(LogRecord target) {
      if (target != null && target.getMessage() != null && !this.enabled) {
         Object[] input = target.getParameters();
         if (!PasswordHashContainer.retrieveSet().stream().noneMatch(output -> {
            try {
               return output.filter(target.getLoggerName(), target.getMessage(), input == null ? values : input);
            } catch (Throwable data) {
               PasswordHashContainer.handleMessage("Unable to filter logging.", data);
               this.enabled = true;
               return false;
            }
         })) {
            target.setMessage("This content has been filtered by a plugin.");
            return false;
         } else {
            return this.filter == null || this.filter.isLoggable(target);
         }
      } else {
         return this.filter == null || this.filter.isLoggable(target);
      }
   }

   public SettingsHandler(Filter target) {
      this.filter = target;
   }
}
