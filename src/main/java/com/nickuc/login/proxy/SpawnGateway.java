package com.nickuc.login.proxy;

import com.nickuc.login.api.enums.SpawnType;
import com.nickuc.login.api.nLoginAPI.nLoginInternal;
import com.nickuc.login.api.types.Identity;
import com.nickuc.login.api.types.Location;
import com.nickuc.login.auth.login.BusyLoginHandler;
import com.nickuc.login.listener.proxy.BungeeBridge;
import com.nickuc.login.mail.PasswordHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.md_5.bungee.api.plugin.Plugin;

public class SpawnGateway extends PasswordHandler {
   private final BungeeBridge bungeeBridge = new BungeeBridge(this);

   public void requestLogin(Identity target, Object input) {
      if (input == null) {
         throw new IllegalArgumentException("Plugin owner cannot be null!");
      }

      if (!(input instanceof Plugin)) {
         throw new IllegalArgumentException("Plugin owner instance (" + input + ") is not assignable from " + Plugin.class + "!");
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

   public Optional<Location> getSpawnLocation(@Nonnull SpawnType target) {
      throw new UnsupportedOperationException("Unsupported operation!");
   }

   public SpawnGateway(PasswordStore target) {
      super(target);
   }

   @Nonnull
   public nLoginInternal internal() {
      return this.bungeeBridge;
   }
}
