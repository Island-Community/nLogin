package com.nickuc.login.storage.spawn;

import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SettingsLinker;
import com.nickuc.login.session.LimboCoordinator;

public class SpawnStore extends SettingsLinker {
   private final SecondarySenderAdapter secondarySenderAdapter;

   public SpawnStore(PasswordStore target) {
      super(target);
      this.secondarySenderAdapter = target.findObject();
   }

   @Override
   public void executeVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input) {
      this.secondarySenderAdapter.getPasswordHashAdapter().updateVerifiedServerAdapter(target, 1, "action", 1);
   }

   @Override
   public void saveVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, boolean output) {
      this.secondarySenderAdapter.getPasswordHashAdapter().updateVerifiedServerAdapter(target, 1, "action", 0, "use-register-spawn", output);
   }
}
