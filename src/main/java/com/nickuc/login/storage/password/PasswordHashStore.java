package com.nickuc.login.storage.password;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.tasks.LoginJob;
import java.util.HashSet;
import java.util.Set;

public class PasswordHashStore extends LoginSource {
   private static final Set<String> players = new HashSet<>();

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (!(target instanceof VerifiedServerAdapter)) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.TOP_LOUDPROXYSTATE);
      } else {
         VerifiedServerAdapter output = (VerifiedServerAdapter)target;
         if (players.remove(output.getName())) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§cDisabling monitoring mode...");
         } else {
            players.add(output.getName());
            new LoginJob(this.passwordStore, output, input.length > 1 && input[1].equalsIgnoreCase("last"));
            output.handleMessage("§aEnabling monitoring mode...");
         }
      }
   }

   public PasswordHashStore(PasswordStore target) {
      super(target, "monitor", "nlogin.admin", false, true);
   }
}
