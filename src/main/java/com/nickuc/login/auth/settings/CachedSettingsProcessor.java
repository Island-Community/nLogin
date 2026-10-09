package com.nickuc.login.auth.settings;

import com.nickuc.login.command.ForceSetupCommand;
import com.nickuc.login.command.LinkSpawnCommand;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.premium.IndirectPasswordHashVerifier;

public final class CachedSettingsProcessor {
   public static void executeSilentProxyState(SilentProxyState instance, IndirectPasswordHashVerifier target) {
      PasswordHashContainer.dispatchMessage("Starting logger filter...");
      switch (instance) {
         case SILENT_PROXY_STATE:
            PasswordHashContainer.saveConnectionContract(new ForceSetupCommand(target, null));
            break;
         case ACTIVE_SILENTPROXYSTATE:
         case PENDING_SILENTPROXYSTATE:
            PasswordHashContainer.saveConnectionContract(new LinkSpawnCommand(target, null));
      }
   }
}
