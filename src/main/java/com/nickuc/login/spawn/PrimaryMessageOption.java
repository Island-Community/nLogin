package com.nickuc.login.spawn;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.listener.InternalListenerContract;
import com.nickuc.login.premium.SettingsLookup;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import java.io.File;
import java.util.Collections;
import java.util.List;

public enum PrimaryMessageOption implements InternalListenerContract {
   PRIMARY_MESSAGE_OPTION(
      BusyLoginProcessor.handleBusyLoginProcessor("backend.auth-servers", "authentication-servers", "Config.authentication-servers"),
      Collections.singletonList("lobby")
   ),
   ACTIVE_PRIMARYMESSAGEOPTION(BusyLoginProcessor.handleBusyLoginProcessor("backend.check-ack-message", "check-ack-message"), true),
   PENDING_PRIMARYMESSAGEOPTION(BusyLoginProcessor.handleBusyLoginProcessor("redirect.use-fast-redirect"), true),
   CURRENT_PRIMARYMESSAGEOPTION(
      BusyLoginProcessor.handleBusyLoginProcessor("redirect.override-first-server", "override-redirect", "force-redirect", "Config.force-redirect"), true
   ),
   PRIMARY_PRIMARYMESSAGEOPTION(BusyLoginProcessor.handleBusyLoginProcessor("redirect.connect-delay", "redirect.after-auth.connect-delay"), 500),
   MAIN_PRIMARYMESSAGEOPTION(BusyLoginProcessor.handleBusyLoginProcessor("redirect.retry-delay", "redirect.after-auth.retry-delay"), 5000),
   LOCAL_PRIMARYMESSAGEOPTION(
      BusyLoginProcessor.handleBusyLoginProcessor("redirect.last-server.enable", "redirect.last-server.enabled", "redirect.redirect-to-last-server"), false
   ),
   REMOTE_PRIMARYMESSAGEOPTION(BusyLoginProcessor.handleBusyLoginProcessor("redirect.last-server.ignored"), Collections.emptyList()),
   CACHED_PRIMARYMESSAGEOPTION(
      BusyLoginProcessor.handleBusyLoginProcessor(
         "redirect.after-auth.enable", "redirect.after-auth.enabled", "server-redirect.enabled", "Config.server-redirect.enabled"
      ),
      false
   ),
   STORED_PRIMARYMESSAGEOPTION(
      BusyLoginProcessor.handleBusyLoginProcessor("redirect.after-auth.servers", "server-redirect.redirect-servers", "Config.server-redirect.redirect-servers"),
      Collections.singletonList("lobby")
   );

   private static final SecureLoginGate secureLoginGate = new SecureLoginGate("proxy-config", values().length);
   private final BusyLoginProcessor busyLoginProcessor;
   private final Object object;

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      return secureLoginGate;
   }

   public static void dispatchIndirectSessionHandler(IndirectSessionHandler<?> instance, boolean target) {
      PasswordHashLoader input = new PasswordHashLoader("config.yml", new File(instance.resolveFile(), "proxy"));
      SettingsLookup.loadLoudNoticeCatalog(instance, input, "proxy/proxy_%s.yml", target);
      VerifiedPasswordHashHasher.performValues(values(), secureLoginGate, input);
      List output = STORED_PRIMARYMESSAGEOPTION.b(new Object[0]);
      output.removeIf(instanceValue -> instanceValue == null || instanceValue.trim().isEmpty());
      if (!output.isEmpty()) {
         VerifiedPasswordHashHasher.executeInternalListenerContract(STORED_PRIMARYMESSAGEOPTION, secureLoginGate, output);
      } else {
         VerifiedPasswordHashHasher.executeInternalListenerContract(CACHED_PRIMARYMESSAGEOPTION, secureLoginGate, false);
      }
   }

   @Override
   public int fetchCount() {
      return this.ordinal();
   }

   PrimaryMessageOption(BusyLoginProcessor output, Object context) {
      this.object = context;
      this.busyLoginProcessor = output;
   }

   @Override
   public Object getObject() {
      return this.object;
   }

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }
}
