package com.nickuc.login.storage.password;

import java.util.Properties;


public class PasswordRepository {
   private final String name;
   private final String activeName;
   private final String pendingName;
   private final Properties properties;
   private final String currentName;
   private final int count;

   public String loadMessage() {
      return this.currentName;
   }

   public int findCount() {
      return this.count;
   }

   public String getMessage() {
      return this.pendingName;
   }

   public static PasswordRepository processPasswordRepository(String instance, String target, String input, String output, Properties context, int data) {
      String[] value = instance.split(":");

      int result;
      try {
         result = value.length > 1 ? Integer.parseInt(value[1]) : data;
      } catch (NumberFormatException response) {
         throw new IllegalArgumentException("Invalid port! " + value[1]);
      }

      return computePasswordRepository(value.length > 0 ? value[0] : instance, result, target, input, output, context);
   }

   public String resolveMessage() {
      return this.name;
   }

   public Properties retrieveProperties() {
      return this.properties;
   }

   public static PasswordRepository computePasswordRepository(String instance, int target, String input, String output, String context, Properties data) {
      return new PasswordRepository(instance, target, input, output, context, data);
   }

   private PasswordRepository(String target, int input, String output, String context, String data, Properties value) {
      if (target == null || target.isEmpty()) {
         throw new IllegalArgumentException("Host cannot be null or empty!");
      }

      if (input <= 0 || input > 65535) {
         throw new IllegalArgumentException("Invalid port! " + input);
      }

      if (output == null || output.isEmpty()) {
         throw new IllegalArgumentException("Database cannot be null or empty!");
      }

      if (context == null) {
         throw new IllegalArgumentException("Username cannot be null!");
      }

      if (data == null) {
         throw new IllegalArgumentException("Password cannot be null!");
      }

      this.activeName = target;
      this.count = input;
      this.pendingName = output;
      this.currentName = context;
      this.name = data;
      this.properties = value;
   }

   public String fetchMessage() {
      return this.activeName;
   }
}
