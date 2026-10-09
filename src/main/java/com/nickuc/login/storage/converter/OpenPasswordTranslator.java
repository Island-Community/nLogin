package com.nickuc.login.storage.converter;

import com.nickuc.login.storage.platform.IncomingPlatformCatalog;


public class OpenPasswordTranslator {
   private final String name;
   private final String activeName;
   private final String pendingName;
   private final String currentName;
   private final IncomingPlatformCatalog incomingPlatformCatalog;
   private final String primaryName;
   private final int count;

   @Override
   public String toString() {
      return "LibreLogin.StorageConfig(storageType="
         + this.incomingPlatformCatalog
         + ", database="
         + this.name
         + ", host="
         + this.pendingName
         + ", user="
         + this.currentName
         + ", password="
         + this.activeName
         + ", port="
         + this.count
         + ", path="
         + this.primaryName
         + ")";
   }

   public OpenPasswordTranslator(IncomingPlatformCatalog target, String input, String output, String context, String data, int value, String result) {
      this.incomingPlatformCatalog = target;
      this.name = input;
      this.pendingName = output;
      this.currentName = context;
      this.activeName = data;
      this.count = value;
      this.primaryName = result;
   }

   public static PasswordTranslator getPasswordTranslator() {
      return new PasswordTranslator();
   }
}
