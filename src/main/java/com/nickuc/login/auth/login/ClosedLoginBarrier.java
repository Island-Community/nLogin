package com.nickuc.login.auth.login;

import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.spawn.LoginLocator;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;


public class ClosedLoginBarrier implements TightSenderAdapter {
   private final String name;
   private final boolean enabled;
   @Nullable
   private final String activeName;
   private final String pendingName;
   private final boolean activeEnabled;
   private final String currentName;
   private final List<LoginLocator> entries;

   public ClosedLoginBarrier(String target, String input, String output, @Nullable String context, LoginLocator... data) {
      this(target, input, output, context, true, true, data);
   }

   @Override
   public boolean equals(Object target) {
      if (target == this) {
         return true;
      }

      if (!(target instanceof ClosedLoginBarrier)) {
         return false;
      }

      ClosedLoginBarrier input = (ClosedLoginBarrier)target;
      if (!input.validateState(this)) {
         return false;
      }

      if (this.fetchState() != input.fetchState()) {
         return false;
      }

      if (this.getState() != input.getState()) {
         return false;
      }

      String output = this.retrieveMessage();
      String context = input.retrieveMessage();
      if (output == null ? context == null : output.equals(context)) {
         String data = this.fetchMessage();
         String value = input.fetchMessage();
         if (data == null ? value == null : data.equals(value)) {
            String result = this.getVersion();
            String request = input.getVersion();
            if (result == null ? request == null : result.equals(request)) {
               String response = this.getMessage();
               String source = input.getMessage();
               if (response == null ? source == null : response.equals(source)) {
                  List entry = this.loadCollection();
                  List record = input.loadCollection();
                  return entry == null ? record == null : entry.equals(record);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public String retrieveMessage() {
      return this.name;
   }

   @Override
   public boolean fetchState() {
      return this.activeEnabled;
   }

   @Override
   public String fetchMessage() {
      return this.pendingName;
   }

   @Override
   public boolean getState() {
      return this.enabled;
   }

   public ClosedLoginBarrier(String target, String input, String output, @Nullable String context, boolean data, boolean value, LoginLocator... result) {
      this.name = target.replace("{}", ".");
      this.pendingName = input.replace("{}", ".");
      this.currentName = output;
      this.activeName = context;
      this.activeEnabled = data;
      this.enabled = value;
      this.entries = Arrays.asList(result);
   }

   @Override
   public int hashCode() {
      byte target = 59;
      int input = 1;
      input = input * 59 + (this.fetchState() ? 79 : 97);
      input = input * 59 + (this.getState() ? 79 : 97);
      String output = this.retrieveMessage();
      input = input * 59 + (output == null ? 43 : output.hashCode());
      String context = this.fetchMessage();
      input = input * 59 + (context == null ? 43 : context.hashCode());
      String data = this.getVersion();
      input = input * 59 + (data == null ? 43 : data.hashCode());
      String value = this.getMessage();
      input = input * 59 + (value == null ? 43 : value.hashCode());
      List result = this.loadCollection();
      return input * 59 + (result == null ? 43 : result.hashCode());
   }

   public boolean validateState(Object target) {
      return target instanceof ClosedLoginBarrier;
   }

   @Override
   public String getVersion() {
      return this.currentName;
   }

   @Override
   public List<LoginLocator> loadCollection() {
      return this.entries;
   }

   @Nullable
   @Override
   public String getMessage() {
      return this.activeName;
   }
}
