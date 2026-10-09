package com.nickuc.login.notification;

import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.storage.spawn.SpawnState;


public class SettingsPublisher {
   private String name;
   private int count;
   private int activeCount = 0;
   private String activeName;
   private int pendingCount;

   public void sendVerifiedServerAdapter(VerifiedServerAdapter target, Object... input) {
      if (this.activeName != null && this.name != null && this.count > 0) {
         if (target != null && SpawnState.PENDING_MAIN_SPAWNSTATE.ar()) {
            String output = target.getName();
            target.saveMessage(
               OpenLocaleBarrier.handleMessage(this.activeName.replace("@player", output), input),
               OpenLocaleBarrier.handleMessage(this.name.replace("@player", output), input),
               this.activeCount,
               this.count,
               this.pendingCount
            );
         }
      }
   }

   public boolean resolveState() {
      return this.activeName != null && this.name != null;
   }

   public SettingsPublisher() {
      this.count = 30;
      this.pendingCount = 6;
   }

   public static SettingsPublisher buildSettingsPublisher(String instance, PasswordHashLoader target) {
      if (target.canState(instance + ".title") && target.canState(instance + ".subtitle")) {
         SettingsPublisher input = new SettingsPublisher();
         input.activeName = LocalLocaleFlow.loadMessage(target.a(instance + ".title", ""));
         input.name = LocalLocaleFlow.loadMessage(target.a(instance + ".subtitle", ""));
         if (target.canState(instance + ".delays.fadeIn")) {
            input.activeCount = target.a(instance + ".delays.fadeIn");
         }

         if (target.canState(instance + ".delays.stay")) {
            input.count = target.a(instance + ".delays.stay");
         }

         if (target.canState(instance + ".delays.fadeOut")) {
            input.pendingCount = target.a(instance + ".delays.fadeOut");
         }

         return input;
      } else {
         return SharedLoginOption.loadSettingsPublisher();
      }
   }

   @Override
   public String toString() {
      return "Messages.Title.SavedTitle(title="
         + this.activeName
         + ", subtitle="
         + this.name
         + ", start="
         + this.activeCount
         + ", duration="
         + this.count
         + ", end="
         + this.pendingCount
         + ")";
   }
}
