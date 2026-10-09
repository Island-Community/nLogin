package com.nickuc.login.premium;

import com.nickuc.login.auth.login.IncomingLoginGate;
import com.nickuc.login.auth.login.LiveLoginProcessor;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.ReadyLoginGate;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;

public class LoginLinker extends LoginSource {
   public LoginLinker(PasswordStore target) {
      super(target, "version", "nlogin.admin", false, true, "v");
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      long output = this.passwordStore.fetchLocalSettingsRepository().getTime();
      UpdateLookup data = this.passwordStore.a();
      LiveLoginProcessor value = data.retrieveLiveLoginProcessor();
      ReadyLoginGate result = data.resolveReadyLoginGate();
      IncomingLoginGate request = this.passwordStore.findIncomingLoginGate();
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
      StringBuilder switchState = new StringBuilder()
         .append(" §eRunning §f")
         .append(this.passwordStore.retrieveMessage())
         .append(" v")
         .append(this.passwordStore.getMessage())
         .append(" §b")
         .append(data.getMessage());
      this.passwordStore.a();
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, switchState.append(false ? " §c(compromised)" : "").toString());
      if (this.passwordStore.a().getCount() == 9) {
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §6⭐ Premium version");
      }

      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7Implementation version: §fv26.07");
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7Identifier: " + (value != null ? "§f" + value.retrieveMessage() : "§cUnavailable"));
      CachedSettingsGateway.handleOutgoingSenderAdapter(
         target, "  §7Session ID: " + (result != null ? "§f" + OpenLocaleBarrier.resolveMessage(result.retrieveTime()) : "§cUnavailable")
      );
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7Platform: §f" + request.retrieveSilentProxyState().getName() + " " + request.getMessage());
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7Registered Players: §f" + OpenLocaleBarrier.resolveMessage(output));
      CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
   }
}
