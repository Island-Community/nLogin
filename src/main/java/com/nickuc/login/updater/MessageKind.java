package com.nickuc.login.updater;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.notification.NoticeSender;
import com.nickuc.login.platform.session.SessionHandler;
import java.util.Arrays;
import javax.annotation.Nullable;

public enum MessageKind implements SessionHandler {
   MESSAGE_KIND("confirm-read"),
   ACTIVE_MESSAGEKIND("yes-btn"),
   PENDING_MESSAGEKIND("no-btn"),
   CURRENT_MESSAGEKIND("new-account"),
   PRIMARY_MESSAGEKIND("transfer-data"),
   MAIN_MESSAGEKIND("start-setup", true),
   LOCAL_MESSAGEKIND("stable", true),
   REMOTE_MESSAGEKIND("latest", true),
   CACHED_MESSAGEKIND("automatic", true),
   STORED_MESSAGEKIND("manual", true),
   VERIFIED_MESSAGEKIND("safer", true),
   AUTHENTICATED_MESSAGEKIND("faster", true),
   SHARED_MESSAGEKIND("pre-login", true),
   PRIVATE_MESSAGEKIND("after-login", true),
   INTERNAL_MESSAGEKIND("install", true),
   UPSTREAM_MESSAGEKIND("skip", true);

   public final BusyLoginProcessor busyLoginProcessor;
   public final boolean enabled;

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      throw new UnsupportedOperationException();
   }

   @Nullable
   public static NoticeSender createNoticeSender(String instance, PasswordHashLoader target) {
      String input = LocalLocaleFlow.loadMessage(target.b(instance + ".text"));
      if (input == null) {
         return null;
      }

      String output = LocalLocaleFlow.loadMessage(target.b(instance + ".hover"));
      return new NoticeSender(input, output);
   }

   MessageKind(String... output) {
      this.busyLoginProcessor = BusyLoginProcessor.handleBusyLoginProcessor(
         Arrays.stream(output).map(instance -> "notification.button." + instance).toArray(String[]::new)
      );
      this.enabled = false;
   }

   MessageKind(String output, boolean context) {
      this.busyLoginProcessor = BusyLoginProcessor.handleBusyLoginProcessor("notification.button." + output);
      this.enabled = context;
   }

   @Override
   public Object getObject() {
      return new NoticeSender("§c" + this.name(), null);
   }

   @Override
   public int fetchCount() {
      return this.ordinal();
   }

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }
}
