package com.nickuc.login.premium;

import com.nickuc.login.auth.login.CachedLoginBarrier;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.loader.LoaderBootstrap;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.security.hashing.PrimaryPasswordHashVerifier;
import com.nickuc.login.updater.AuthenticatedNoticeKind;
import java.io.File;
import java.util.Locale;


public class ParentSettingsLookup {
   private final File dataFile;
   private PrimaryPasswordHashVerifier primaryPasswordHashVerifier;
   private final CachedLoginBarrier cachedLoginBarrier;

   public ParentSettingsLookup(IndirectSessionHandler<?> target) {
      try {
         this.dataFile = MessageProcessor.handleFile(LoaderBootstrap.class);
      } catch (Exception source) {
         throw new RuntimeException("Cannot retrieve JAR file (path unavailable?)", source);
      }

      this.cachedLoginBarrier = new CachedLoginBarrier(target);
      File input = target.fetchFile();
      if (!input.exists() && !input.mkdirs()) {
         PasswordHashContainer.updateMessage("Unable to create the nCore folder for database.");
      } else {
         File output = new File(input, "data");
         if (!output.exists() && !output.mkdirs()) {
            PasswordHashContainer.updateMessage("Unable to create the nCore database folder.");
         } else {
            String context = target.retrieveMessage();
            File data = new File(output, context.toLowerCase(Locale.ENGLISH) + ".data");
            this.primaryPasswordHashVerifier = new PrimaryPasswordHashVerifier(context, data);
            byte value = 0;
            if (this.primaryPasswordHashVerifier.hasState("version_channel")) {
               this.primaryPasswordHashVerifier
                  .computePrimaryPasswordHashVerifier("version-channel", this.primaryPasswordHashVerifier.createPayload("version_channel"));
               this.primaryPasswordHashVerifier.createPrimaryPasswordHashVerifier("version_channel");
               value = 1;
            }

            if (this.primaryPasswordHashVerifier.hasState("license_id")) {
               this.primaryPasswordHashVerifier.computePrimaryPasswordHashVerifier("license-id", this.primaryPasswordHashVerifier.createPayload("license_id"));
               this.primaryPasswordHashVerifier.createPrimaryPasswordHashVerifier("license_id");
               value = 1;
            }

            String result = this.primaryPasswordHashVerifier.loadMessage("version-channel");
            if (result != null) {
               switch (result) {
                  case "stable":
                     this.primaryPasswordHashVerifier
                        .handlePrimaryPasswordHashVerifier("version-channel", (byte)AuthenticatedNoticeKind.AUTHENTICATED_NOTICE_KIND.ordinal());
                     value = 1;
                     break;
                  case "development":
                     this.primaryPasswordHashVerifier
                        .handlePrimaryPasswordHashVerifier("version-channel", (byte)AuthenticatedNoticeKind.ACTIVE_AUTHENTICATEDNOTICEKIND.ordinal());
                     value = 1;
               }
            }

            if (value != 0) {
               this.primaryPasswordHashVerifier.sendTask();
            }
         }
      }
   }

   public File retrieveFile() {
      return this.dataFile;
   }

   public PrimaryPasswordHashVerifier loadPrimaryPasswordHashVerifier() {
      return this.primaryPasswordHashVerifier;
   }

   public CachedLoginBarrier findCachedLoginBarrier() {
      return this.cachedLoginBarrier;
   }
}
