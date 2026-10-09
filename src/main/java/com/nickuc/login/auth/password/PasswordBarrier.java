package com.nickuc.login.auth.password;

import com.nickuc.login.account.MessageOption;
import com.nickuc.login.config.SettingsDefinition;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;

public class PasswordBarrier extends SettingsDefinition {
   public PasswordBarrier(PasswordStore target) {
      super(
         target,
         MessageOption.SHARED_MESSAGEOPTION,
         "passwords.yml",
         target.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE
      );
   }

   @Override
   public void processMessage(String target, String input) {
      UUID output = DeadLoginFlow.buildUniqueId(target);
      OfflinePlayer context = ((Server)this.passwordStore.b().c()).getOfflinePlayer(output);
      this.updateMessage(context.getName(), input, null, output);
   }
}
