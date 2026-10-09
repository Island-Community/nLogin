package com.nickuc.login.storage.login;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public abstract class LoginSource {
   private final boolean enabled;
   public final InternalLoginOption internalLoginOption = InternalLoginOption.INTERNAL_LOGIN_OPTION;
   public PasswordStore passwordStore;
   private final List<String> entries;
   private final String name;
   public LenientPremiumOption lenientPremiumOption;
   private final String activeName;
   private final boolean activeEnabled;

   public String resolveMessage() {
      return this.name;
   }

   public void handleOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      this.lenientPremiumOption = target instanceof VerifiedServerAdapter
         ? this.passwordStore.loadLimboRegistry().loadLimboCoordinator((VerifiedServerAdapter)target).fetchLenientPremiumOption()
         : CachedSettingsGateway.loadLenientPremiumOption();
      this.processOutgoingSenderAdapter(target, input);
   }

   public LoginSource(PasswordStore target, String input, String output, boolean context, boolean data, String... value) {
      this.passwordStore = target;
      this.activeName = input;
      this.name = output;
      this.activeEnabled = context;
      this.enabled = data;
      this.entries = value.length == 0 ? Collections.emptyList() : Arrays.asList(value);
   }

   public InternalLoginOption resolveInternalLoginOption() {
      return this.internalLoginOption;
   }

   public String findMessage() {
      return this.activeName;
   }

   public PasswordStore retrievePasswordStore() {
      return this.passwordStore;
   }

   public List<String> retrieveCollection() {
      return this.entries;
   }

   public LenientPremiumOption fetchLenientPremiumOption() {
      return this.lenientPremiumOption;
   }

   public boolean getState() {
      return this.enabled;
   }

   public boolean fetchState() {
      return this.activeEnabled;
   }

   public abstract void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input);

   public List<String> computeCollection(OutgoingSenderAdapter target, String input, String[] output) {
      return null;
   }

   public boolean loadState() {
      return this.lenientPremiumOption == LenientPremiumOption.LENIENT_PREMIUM_OPTION
         || this.lenientPremiumOption == LenientPremiumOption.INCOMING_LENIENTPREMIUMOPTION;
   }
}
