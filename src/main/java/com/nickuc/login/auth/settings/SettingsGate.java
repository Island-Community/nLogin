package com.nickuc.login.auth.settings;

import com.nickuc.login.config.PasswordHashContainer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

public final class SettingsGate extends AbstractFilter {
   private boolean enabled;
   private static final Object[] values = new Object[0];

   private Result processFilter(String target, String input, Object[] output) {
      return input != null && !this.enabled && PasswordHashContainer.retrieveSet().stream().anyMatch(context -> {
         try {
            return context.filter(target, input, output == null ? values : output);
         } catch (Throwable value) {
            PasswordHashContainer.handleMessage("Unable to filter logging.", value);
            this.enabled = true;
            return false;
         }
      }) ? Result.DENY : Result.NEUTRAL;
   }

   public Result filter(Logger target, Level input, Marker output, String context, Object... data) {
      return this.processFilter(target.getName(), context, data);
   }

   public Result filter(LogEvent target) {
      if (target == null) {
         return Result.NEUTRAL;
      }

      Message input = target.getMessage();
      return input == null ? Result.NEUTRAL : this.loadFilter(target.getLoggerName(), input, input.getParameters());
   }

   public static void as() {
      Logger instance = (Logger)LogManager.getRootLogger();
      instance.addFilter(new SettingsGate());
   }

   public Result filter(Logger target, Level input, Marker output, Object context, Throwable data) {
      return context == null ? Result.NEUTRAL : this.processFilter(target.getName(), context.toString(), values);
   }

   public Result filter(Logger target, Level input, Marker output, Message context, Throwable data) {
      return this.loadFilter(target.getName(), context, values);
   }

   private Result loadFilter(String target, Message input, Object[] output) {
      return input != null ? this.processFilter(target, input.getFormattedMessage(), output) : Result.NEUTRAL;
   }
}
