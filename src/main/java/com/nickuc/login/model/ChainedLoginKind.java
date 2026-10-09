package com.nickuc.login.model;

import com.nickuc.login.auth.login.LocalLoginBarrier;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import java.io.File;
import javax.annotation.Nullable;


public enum ChainedLoginKind {
   CHAINED_LOGIN_KIND("https://repo.nickuc.com", "https://repo.nickuc.com/maven-public/", PlatformCatalog.ACTIVE_PLATFORMCATALOG),
   ACTIVE_CHAINEDLOGINKIND("https://repo.nickuc.net", "https://repo.nickuc.net/maven-public/", PlatformCatalog.ACTIVE_PLATFORMCATALOG);

   private final String name;
   private final String activeName;
   private final PlatformCatalog platformCatalog;

   public String retrieveMessage() {
      return this.name;
   }

   @Nullable
   public String resolveMessage(TightSenderAdapter target, PlatformCatalog input) {
      String output = this.createMessage(target) + "." + input.loadMessage();
      return PendingPasswordHashHasher.getPendingPasswordHashHasher().processVerifiedLoginGate(output).loadMessage();
   }

   public String resolveMessage() {
      return this.activeName;
   }

   public boolean isState(TightSenderAdapter target, File input) {
      if (input.exists()) {
         return true;
      }

      String output = this.createMessage(target);
      PendingPasswordHashHasher context = PendingPasswordHashHasher.getPendingPasswordHashHasher();
      LocalLoginBarrier data = context.loadLocalLoginBarrier(output, input);
      return data.findCount() == 200 && data.retrieveState();
   }

   public PlatformCatalog loadPlatformCatalog() {
      return this.platformCatalog;
   }

   ChainedLoginKind(String output, String context, PlatformCatalog data) {
      this.name = output;
      this.activeName = context;
      this.platformCatalog = data;
   }

   @Nullable
   public String handleMessage(TightSenderAdapter target) {
      return this.resolveMessage(target, this.platformCatalog);
   }

   public String createMessage(TightSenderAdapter target) {
      return this.activeName + target.retrieveMessage().replace('.', '/') + "/" + target.fetchMessage() + "/" + target.getVersion() + "/" + target.handleMessage(false);
   }
}
