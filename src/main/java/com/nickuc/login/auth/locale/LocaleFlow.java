package com.nickuc.login.auth.locale;

import com.nickuc.login.model.LoginOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocaleFlow {
   private String name;
   private String activeName;
   private static double ratio = Double.longBitsToDouble(4652007308841189376L);
   private String pendingName;
   private String currentName;
   private final long timestamp;
   private final Locale locale;
   private String primaryName;
   private String mainName;
   private String localName;
   private String remoteName;

   public String getMessage() {
      this.performTask();
      return this.name;
   }

   public static String computeMessage(long instance, long input) {
      return loadMessage(instance, input, false);
   }

   public String loadMessage() {
      this.performTask();
      return this.primaryName;
   }

   public String fetchMessage() {
      this.performTask();
      return this.remoteName;
   }

   public LocaleFlow() {
      this(Locale.getDefault());
   }

   public String findMessage() {
      this.performTask();
      return this.currentName;
   }

   public String getMessageForMessage() {
      this.performTask();
      return this.localName;
   }

   public static String createMessage(long instance) {
      return computeMessage(instance, System.currentTimeMillis());
   }

   public static String loadMessage(long instance, long input, boolean context) {
      long data = Math.max(instance, input) - Math.min(instance, input);
      StringBuilder result = new StringBuilder();

      for (LoginOption entry : LoginOption.values()) {
         if (!context || entry != LoginOption.CURRENT_LOGINOPTION || data < 60000L) {
            long record = (Long)LoginOption.resolveFunction(entry).apply(data);
            if (record > 0L) {
               if (result.length() > 0) {
                  result.append(" ");
               }

               result.append(record).append(LoginOption.computeMarker(entry));
            }
         }
      }

      if (result.length() == 0) {
         double element = data % 1000L / ratio;
         return element + "s";
      } else {
         return result.toString();
      }
   }

   public String loadMessageForMessage() {
      this.performTask();
      return this.mainName;
   }

   public LocaleFlow(Locale target) {
      this(System.currentTimeMillis(), target);
   }

   private String buildMessage(String target) {
      return new SimpleDateFormat(target, this.locale).format(new Date(this.timestamp));
   }

   public LocaleFlow(long target, Locale output) {
      this.timestamp = target;
      this.locale = output;
   }

   private void performTask() {
      if (this.mainName == null) {
         this.mainName = this.buildMessage("ss");
         this.localName = this.buildMessage("mm");
         this.remoteName = this.buildMessage("HH");
         this.pendingName = this.buildMessage("dd");
         this.activeName = this.buildMessage("MM");
         this.currentName = this.buildMessage("yyyy");
         this.primaryName = this.buildMessage("EEEEE");
         this.name = this.buildMessage("MMMMM");
      }
   }

   public String resolveMessage() {
      this.performTask();
      return this.activeName;
   }

   public LocaleFlow(long target) {
      this(target, Locale.getDefault());
   }

   public String retrieveMessage() {
      this.performTask();
      return this.pendingName;
   }
}
