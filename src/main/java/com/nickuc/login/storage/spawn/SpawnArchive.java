package com.nickuc.login.storage.spawn;

import com.nickuc.login.api.enums.SpawnType;
import com.nickuc.login.api.nLoginAPI.nLoginInternal;
import com.nickuc.login.api.types.Identity;
import com.nickuc.login.api.types.Location;
import com.nickuc.login.auth.login.BusyLoginHandler;
import com.nickuc.login.listener.proxy.PendingVelocityGuard;
import com.nickuc.login.mail.PasswordHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import java.util.Optional;
import javax.annotation.Nonnull;

public class SpawnArchive extends PasswordHandler {
   private final PendingVelocityGuard pendingVelocityGuard = new PendingVelocityGuard(this);

   @Nonnull
   public nLoginInternal internal() {
      return this.pendingVelocityGuard;
   }

   public SpawnArchive(PasswordStore target) {
      super(target);
   }

   public Optional<Location> getSpawnLocation(@Nonnull SpawnType target) {
      throw new UnsupportedOperationException("Unsupported operation!");
   }

   public void requestLogin(@Nonnull Identity target, @Nonnull Object input) {
      if (input == null) {
         throw new IllegalArgumentException("Plugin owner cannot be null!");
      }

      if (target == null) {
         throw new IllegalArgumentException("Identity cannot be null!");
      }

      if (!(target instanceof BusyLoginHandler)) {
         throw new IllegalArgumentException(
            "Identity is not an instance of " + BusyLoginHandler.class.getCanonicalName() + " class! " + target.getClass().getCanonicalName()
         );
      }

      String output = this.createMessage(target);
      VerifiedServerAdapter context = this.passwordStore.b().resolveVerifiedServerAdapter(output);
      if (context != null) {
         this.passwordStore
            .findSettingsLinker()
            .retrievePacketCoordinator()
            .handleVerifiedServerAdapter(context, this.passwordStore.loadLimboRegistry().loadLimboCoordinator(context));
      }
   }
}
