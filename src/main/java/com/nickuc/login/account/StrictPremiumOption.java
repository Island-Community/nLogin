package com.nickuc.login.account;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.platform.listener.InternalListenerContract;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public enum StrictPremiumOption implements InternalListenerContract {
   STRICT_PREMIUM_OPTION(BusyLoginProcessor.handleBusyLoginProcessor("enable", "enabled"), false),
   ACTIVE_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("linking.invite-url", "options.invite-url"), "missing `linking.invite-url` option"),
   PENDING_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("linking.recommend", "options.recommend-linking"), true),
   CURRENT_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("linking.enable-2fa"), true),
   PRIMARY_STRICTPREMIUMOPTION(
      BusyLoginProcessor.handleBusyLoginProcessor("linking.required.enable", "options.link-required.enable", "options.link-required.enabled"), false
   ),
   MAIN_STRICTPREMIUMOPTION(
      BusyLoginProcessor.handleBusyLoginProcessor("linking.required.enable-for-premium", "options.link-required.required-for-premium"), false
   ),
   LOCAL_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("linking.required.enable-for-bedrock"), false),
   REMOTE_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("account.limit", "options.account.limit", "options.account-limit"), 1),
   CACHED_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("account.block-recent", "options.account.block-recent"), -1),
   STORED_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("account.force-password-update", "options.force-password-update"), false),
   VERIFIED_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("enable", "enabled"), false),
   AUTHENTICATED_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("linking.recommend", "options.recommend-linking"), true),
   SHARED_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("linking.enable-2fa"), true),
   PRIVATE_STRICTPREMIUMOPTION(
      BusyLoginProcessor.handleBusyLoginProcessor("linking.required.enable", "options.link-required.enable", "options.link-required.enabled"), false
   ),
   INTERNAL_STRICTPREMIUMOPTION(
      BusyLoginProcessor.handleBusyLoginProcessor("linking.required.enable-for-premium", "options.link-required.required-for-premium"), false
   ),
   UPSTREAM_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("linking.required.enable-for-bedrock"), false),
   INCOMING_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("account.limit", "options.account-limit"), 1),
   OUTGOING_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("account.force-password-update", "options.force-password-update"), false),
   SECONDARY_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("account.allowed-domains", "options.allowed-domains"), Collections.emptyList()),
   DIRECT_STRICTPREMIUMOPTION(BusyLoginProcessor.handleBusyLoginProcessor("account.denied-domains", "options.denied-domains"), Collections.emptyList());

   private static final SecureLoginGate secureLoginGate = new SecureLoginGate("2fa", values().length);
   private final BusyLoginProcessor busyLoginProcessor;
   private final Object object;

   public static void savePasswordStore(PasswordStore instance) {
      for (StrictPremiumOption context : values()) {
         StrictMessageKind data = computeStrictMessageKind(context);
         PasswordHashLoader value = data.processPasswordHashLoader(instance);
         if (value == null) {
            throw new IllegalStateException(data + " config cannot be null!");
         }

         VerifiedPasswordHashHasher.executeInternalListenerContract(context, context.loadSecureLoginGate(), value, true);
      }

      List result = SECONDARY_STRICTPREMIUMOPTION.b(new Object[0]);
      result.removeIf(instanceValue -> instanceValue == null || instanceValue.trim().isEmpty());
      result.replaceAll(instanceValue -> instanceValue.trim().toLowerCase(Locale.ENGLISH));
      VerifiedPasswordHashHasher.processInternalListenerContract(SECONDARY_STRICTPREMIUMOPTION, result);
      List request = DIRECT_STRICTPREMIUMOPTION.b(new Object[0]);
      request.removeIf(instanceValue -> instanceValue == null || instanceValue.trim().isEmpty());
      request.replaceAll(instanceValue -> instanceValue.trim().toLowerCase(Locale.ENGLISH));
      VerifiedPasswordHashHasher.processInternalListenerContract(DIRECT_STRICTPREMIUMOPTION, request);
   }

   StrictPremiumOption(BusyLoginProcessor output, Object context) {
      this.busyLoginProcessor = output;
      this.object = context;
   }

   @Override
   public boolean retrieveState() {
      return InternalListenerContract.super.retrieveState() && PasswordStore.resolvePasswordStore().a().getCount() == 9;
   }

   public static StrictMessageKind computeStrictMessageKind(StrictPremiumOption instance) {
      String target = instance.name().toLowerCase(Locale.ENGLISH);
      if (target.startsWith("email")) {
         return StrictMessageKind.ACTIVE_STRICTMESSAGEKIND;
      } else if (target.startsWith("discord")) {
         return StrictMessageKind.STRICT_MESSAGE_KIND;
      } else {
         throw new IllegalStateException("Unable to detect the 2FA type of this enum! " + target);
      }
   }

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      return secureLoginGate;
   }

   @Override
   public Object getObject() {
      return this.object;
   }

   @Override
   public int fetchCount() {
      return this.ordinal();
   }
}
