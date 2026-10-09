package com.nickuc.login.storage.converter;

import com.nickuc.login.storage.platform.IncomingPlatformCatalog;


public class PasswordTranslator {
   private String name;
   private int count;
   private IncomingPlatformCatalog incomingPlatformCatalog;
   private String activeName;
   private String pendingName;
   private String currentName;
   private String primaryName;

   public PasswordTranslator resolvePasswordTranslator(String target) {
      this.name = target;
      return this;
   }

   public OpenPasswordTranslator fetchOpenPasswordTranslator() {
      return new OpenPasswordTranslator(
         this.incomingPlatformCatalog, this.name, this.activeName, this.pendingName, this.primaryName, this.count, this.currentName
      );
   }

   public PasswordTranslator processPasswordTranslator(String target) {
      this.pendingName = target;
      return this;
   }

   public PasswordTranslator createPasswordTranslator(String target) {
      this.primaryName = target;
      return this;
   }

   public PasswordTranslator loadPasswordTranslator(String target) {
      this.currentName = target;
      return this;
   }

   @Override
   public String toString() {
      return "LibreLogin.StorageConfig.StorageConfigBuilder(storageType="
         + this.incomingPlatformCatalog
         + ", database="
         + this.name
         + ", host="
         + this.activeName
         + ", user="
         + this.pendingName
         + ", password="
         + this.primaryName
         + ", port="
         + this.count
         + ", path="
         + this.currentName
         + ")";
   }

   public PasswordTranslator resolvePasswordTranslatorForPasswordTranslator(String target) {
      this.activeName = target;
      return this;
   }

   public PasswordTranslator buildPasswordTranslator(int target) {
      this.count = target;
      return this;
   }

   public PasswordTranslator processPasswordTranslator(IncomingPlatformCatalog target) {
      this.incomingPlatformCatalog = target;
      return this;
   }
}
