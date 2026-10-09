package com.nickuc.login.account;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.platform.listener.InternalListenerContract;


public enum IndirectNoticeCatalog implements InternalListenerContract {
   INDIRECT_NOTICE_CATALOG("database.remote.hostname"),
   ACTIVE_INDIRECTNOTICECATALOG("database.remote.database"),
   PENDING_INDIRECTNOTICECATALOG("database.remote.username"),
   CURRENT_INDIRECTNOTICECATALOG("database.remote.password"),
   PRIMARY_INDIRECTNOTICECATALOG("database.pool-settings.maximum-pool-size"),
   MAIN_INDIRECTNOTICECATALOG("database.pool-settings.minimum-idle"),
   LOCAL_INDIRECTNOTICECATALOG("database.pool-settings.maximum-lifetime"),
   REMOTE_INDIRECTNOTICECATALOG("database.pool-settings.connection-timeout"),
   CACHED_INDIRECTNOTICECATALOG("premium.challenge-if-premium-uuid"),
   STORED_INDIRECTNOTICECATALOG("premium.legacy.restrict-premium-nicknames"),
   VERIFIED_INDIRECTNOTICECATALOG("premium.legacy.challenge-if-premium-nickname");

   private final BusyLoginProcessor busyLoginProcessor;

   @Override
   public int fetchCount() {
      return this.ordinal();
   }

   IndirectNoticeCatalog(String... output) {
      this.busyLoginProcessor = BusyLoginProcessor.handleBusyLoginProcessor(output);
   }

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      throw new UnsupportedOperationException();
   }

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }

   @Override
   public Object getObject() {
      throw new UnsupportedOperationException();
   }

   IndirectNoticeCatalog(BusyLoginProcessor output) {
      this.busyLoginProcessor = output;
   }

   @Override
   public String toString() {
      return "nLoginV2Converter.ExtraSettings." + this.name() + "(key=" + this.busyLoginProcessor + ")";
   }
}
