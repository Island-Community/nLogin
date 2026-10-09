package com.nickuc.login.account;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.model.ProxyState;
import com.nickuc.login.platform.listener.InternalListenerContract;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.Collections;


public enum QuickPremiumOption implements InternalListenerContract {
   QUICK_PREMIUM_OPTION(BusyLoginProcessor.handleBusyLoginProcessor("premium-nickname"), ProxyState.PROXY_STATE.name()),
   ACTIVE_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("premium-uuid"), ProxyState.PROXY_STATE.name()),
   PENDING_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("premium-question"), true),
   CURRENT_QUICKPREMIUMOPTION(BusyLoginProcessor.createBusyLoginProcessor(true, "username-appender.enable"), false),
   PRIMARY_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("username-appender.premium.username-appendix"), ""),
   MAIN_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("username-appender.premium.position"), "suffix"),
   LOCAL_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("username-appender.premium.domains"), Collections.emptyList()),
   REMOTE_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("username-appender.offline.username-appendix"), "+"),
   CACHED_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("username-appender.offline.position"), "suffix"),
   STORED_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("username-appender.offline.domains"), Collections.emptyList()),
   VERIFIED_QUICKPREMIUMOPTION(BusyLoginProcessor.createBusyLoginProcessor(true, "autologin.bedrock.enable"), true),
   AUTHENTICATED_QUICKPREMIUMOPTION(BusyLoginProcessor.createBusyLoginProcessor(true, "autologin.bedrock.skip-register"), true),
   SHARED_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("autologin.bedrock.use-database-uuid"), false),
   PRIVATE_QUICKPREMIUMOPTION(BusyLoginProcessor.createBusyLoginProcessor(true, "autologin.premium.enable"), true),
   INTERNAL_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("autologin.premium.allow-if-offline"), false),
   UPSTREAM_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("autologin.premium.skip-register"), true),
   INCOMING_QUICKPREMIUMOPTION(BusyLoginProcessor.createBusyLoginProcessor(true, "autologin.session.enable"), true),
   OUTGOING_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("autologin.session.duration"), 1),
   SECONDARY_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("autologin.session.bypass.nicknames"), Collections.emptyList()),
   DIRECT_QUICKPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("autologin.session.bypass.ips"), Collections.emptyList());

   public static final SecureLoginGate secureLoginGate = new SecureLoginGate("premium-config", values().length);
   public final BusyLoginProcessor busyLoginProcessor;
   private final Object object;

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      return secureLoginGate;
   }

   @Override
   public int fetchCount() {
      return this.ordinal();
   }

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }

   @Override
   public Object getObject() {
      return this.object;
   }

   public boolean getState() {
      return InternalListenerContract.super.checkState(this.loadSecureLoginGate());
   }

   QuickPremiumOption(BusyLoginProcessor output, Object context) {
      this.busyLoginProcessor = output;
      this.object = context;
   }

   @Override
   public boolean retrieveState() {
      return (!this.busyLoginProcessor.fetchState() || PasswordStore.resolvePasswordStore().a().getCount() == 9) && this.getState();
   }
}
