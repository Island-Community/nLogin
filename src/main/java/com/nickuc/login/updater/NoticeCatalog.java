package com.nickuc.login.updater;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.platform.session.SessionHandler;
import java.util.Collections;

public enum NoticeCatalog implements SessionHandler {
   NOTICE_CATALOG("update.stable"),
   ACTIVE_NOTICECATALOG("update.latest"),
   PENDING_NOTICECATALOG("notification.setup.select-language", true),
   CURRENT_NOTICECATALOG("notification.setup.permission-required", true),
   PRIMARY_NOTICECATALOG("notification.setup.welcome-message", true),
   MAIN_NOTICECATALOG("notification.setup.configure-version-channel", true),
   LOCAL_NOTICECATALOG("notification.setup.configure-auto-or-manual-updates", true),
   REMOTE_NOTICECATALOG("notification.setup.configure-password-algorithm", true),
   CACHED_NOTICECATALOG("notification.setup.enable-dialogs", true),
   STORED_NOTICECATALOG("notification.setup.configure-dialogs", true),
   VERIFIED_NOTICECATALOG("notification.setup.configure-bungeeguard", true),
   AUTHENTICATED_NOTICECATALOG("notification.setup.start-premium-configuration", true),
   SHARED_NOTICECATALOG("notification.setup.configure-username-appendix", true),
   PRIVATE_NOTICECATALOG("notification.setup.configure-username-appendix-legacy-versions", true),
   INTERNAL_NOTICECATALOG("notification.setup.enable-restrict-premium-nicknames", true),
   UPSTREAM_NOTICECATALOG("notification.setup.enable-challenge-premium-nicknames", true),
   INCOMING_NOTICECATALOG("notification.setup.enable-premium-question", true),
   OUTGOING_NOTICECATALOG("notification.setup.skip-premium-register", true),
   SECONDARY_NOTICECATALOG("notification.setup.skip-bedrock-register", true),
   DIRECT_NOTICECATALOG("notification.setup.nantibot-recommendation", true),
   LINKED_NOTICECATALOG("notification.setup.nchat-recommendation", true),
   ROOT_NOTICECATALOG("notification.setup.recommend-spawn-set", true),
   TOP_NOTICECATALOG("notification.setup.finish-configuration", true),
   FAST_NOTICECATALOG("notification.update-available-confirmation", true),
   SAFE_NOTICECATALOG("notification.update-available-restart", true),
   SECURE_NOTICECATALOG("notification.premium-fail", true),
   OPEN_NOTICECATALOG("notification.premium-expired", true),
   READY_NOTICECATALOG("notification.premium-recommendation", true);

   public final BusyLoginProcessor busyLoginProcessor;
   public final boolean enabled;

   @Override
   public Object getObject() {
      return this.enabled
         ? Collections.singletonList("§cThis message list (with key \"" + this.busyLoginProcessor.fetchNames()[0] + "\") was not found.")
         : "§cThis message (with key \"" + this.busyLoginProcessor.fetchNames()[0] + "\") was not found.";
   }

   NoticeCatalog(String output, boolean context) {
      this.busyLoginProcessor = BusyLoginProcessor.handleBusyLoginProcessor(output);
      this.enabled = context;
   }

   @Override
   public int fetchCount() {
      return this.ordinal();
   }

   NoticeCatalog(String output) {
      this(output, false);
   }

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      throw new UnsupportedOperationException();
   }
}
