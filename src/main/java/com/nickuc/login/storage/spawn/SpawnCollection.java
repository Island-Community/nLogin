package com.nickuc.login.storage.spawn;

import com.nickuc.login.api.enums.SpawnType;
import com.nickuc.login.api.nLoginAPI.nLoginInternal;
import com.nickuc.login.api.types.Identity;
import com.nickuc.login.api.types.Location;
import com.nickuc.login.auth.login.BusyLoginHandler;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.listener.bukkit.FastLoginGuard;
import com.nickuc.login.listener.bukkit.VerifiedLoginGuard;
import com.nickuc.login.mail.PasswordHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.spawn.IndirectLoginKind;
import com.nickuc.login.spawn.LoginResolver;
import com.nickuc.login.tasks.SynchronizeWithServerThreadTask;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class SpawnCollection extends PasswordHandler {
   private final FastLoginGuard fastLoginGuard = new FastLoginGuard(this);

   public SpawnCollection(PasswordStore target) {
      super(target);
   }

   @Nonnull
   public nLoginInternal internal() {
      return this.fastLoginGuard;
   }

   public void requestLogin(@Nonnull Identity target, @Nonnull Object input) {
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
         Runnable data = () -> this.passwordStore
            .findSettingsLinker()
            .retrievePacketCoordinator()
            .handleVerifiedServerAdapter(context, this.passwordStore.loadLimboRegistry().loadLimboCoordinator(context));
         if (Bukkit.getServer().isPrimaryThread() && !BungeeWriter.fetchState()) {
            data.run();
         } else {
            context.getLinkedSessionHandler().buildStrictCommandHandler(new SynchronizeWithServerThreadTask(data));
         }
      }
   }

   public Optional<Location> getSpawnLocation(@Nonnull SpawnType target) {
      if (target == null) {
         throw new IllegalArgumentException("Spawn type cannot be null!");
      }

      IndirectLoginKind input = IndirectLoginKind.computeIndirectLoginKind(target);
      org.bukkit.Location output = VerifiedLoginGuard.resolveLocation(this.passwordStore.a().loadPrimaryPasswordHashVerifier().loadMessage(input.fetchMessage()));
      if (output == null) {
         return Optional.empty();
      }

      LoginResolver context = new LoginResolver(output.getWorld().getName(), output.getX(), output.getY(), output.getZ(), output.getYaw(), output.getPitch());
      return Optional.of(context);
   }
}
