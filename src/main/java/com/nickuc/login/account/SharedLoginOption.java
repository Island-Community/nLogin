package com.nickuc.login.account;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.notification.SettingsPublisher;
import com.nickuc.login.platform.session.SessionHandler;
import java.util.Arrays;


public enum SharedLoginOption implements SessionHandler {
   SHARED_LOGIN_OPTION("before-login"),
   ACTIVE_SHAREDLOGINOPTION("before-register"),
   PENDING_SHAREDLOGINOPTION("after-login"),
   CURRENT_SHAREDLOGINOPTION("after-register"),
   PRIMARY_SHAREDLOGINOPTION("incorrect-password"),
   MAIN_SHAREDLOGINOPTION("already-login"),
   LOCAL_SHAREDLOGINOPTION("already-registered"),
   REMOTE_SHAREDLOGINOPTION("passwords-dont-match"),
   CACHED_SHAREDLOGINOPTION("session-login"),
   STORED_SHAREDLOGINOPTION("premium-login"),
   VERIFIED_SHAREDLOGINOPTION("bedrock-login"),
   AUTHENTICATED_SHAREDLOGINOPTION("security-warning"),
   SHARED_SHAREDLOGINOPTION("unconfirmed-message", "answer-required"),
   PRIVATE_SHAREDLOGINOPTION("setup.welcome-message-1", true),
   INTERNAL_SHAREDLOGINOPTION("setup.welcome-message-2", true),
   UPSTREAM_SHAREDLOGINOPTION("setup.welcome-message-3", true),
   INCOMING_SHAREDLOGINOPTION("update-available", true);

   private static final SettingsPublisher settingsPublisher = new SettingsPublisher();
   public final BusyLoginProcessor busyLoginProcessor;
   public final boolean enabled;

   SharedLoginOption(BusyLoginProcessor output, boolean context) {
      this.busyLoginProcessor = output;
      this.enabled = context;
   }

   @Override
   public Object getObject() {
      return settingsPublisher;
   }

   SharedLoginOption(String output, boolean context) {
      this.busyLoginProcessor = BusyLoginProcessor.handleBusyLoginProcessor("title." + output);
      this.enabled = context;
   }

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int fetchCount() {
      return this.ordinal();
   }

   SharedLoginOption(String... output) {
      this.busyLoginProcessor = BusyLoginProcessor.handleBusyLoginProcessor(Arrays.stream(output).map(instance -> "title." + instance).toArray(String[]::new));
      this.enabled = false;
   }
}
