package com.nickuc.login.listener.bukkit;

import com.nickuc.login.auth.login.StoredLoginHandler;
import com.nickuc.login.loader.platform.BukkitLoader;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.command.StrictCommandHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

public class PendingLoginFilter implements LinkedSessionHandler {
   private final StoredLoginHandler storedLoginHandler = new StoredLoginHandler();
   private final boolean enabled;
   @Nullable
   private final Player player;
   private final BukkitLoader bukkitLoader;

   @Override
   public StrictCommandHandler buildStrictCommandHandler(Runnable target) {
      return new IncomingLoginGuard(this.player, this.enabled, this.storedLoginHandler, target).resolveIncomingLoginGuard(this.bukkitLoader);
   }

   @Override
   public StoredLoginHandler loadStoredLoginHandler() {
      return this.storedLoginHandler;
   }

   @Override
   public StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target, long input, TimeUnit context) {
      return new IncomingLoginGuard(this.player, this.enabled, this.storedLoginHandler, target).buildIncomingLoginGuard(this.bukkitLoader, input, context);
   }

   @Override
   public StrictCommandHandler processStrictCommandHandler(Runnable target, long input, long context, TimeUnit value) {
      return new IncomingLoginGuard(this.player, this.enabled, this.storedLoginHandler, target).computeIncomingLoginGuard(this.bukkitLoader, input, context, value);
   }

   @Override
   public StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target, long input, long context, TimeUnit value) {
      return new IncomingLoginGuard(this.player, this.enabled, this.storedLoginHandler, target).computeIncomingLoginGuard(this.bukkitLoader, input, context, value);
   }

   @Override
   public StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target) {
      return new IncomingLoginGuard(this.player, this.enabled, this.storedLoginHandler, target).resolveIncomingLoginGuard(this.bukkitLoader);
   }

   private PendingLoginFilter(BukkitLoader target, @Nullable Player input, boolean output) {
      this.bukkitLoader = target;
      this.player = input;
      this.enabled = output;
   }

   public PendingLoginFilter(BukkitLoader target, boolean input) {
      this(target, null, input);
   }

   @Override
   public void dispatchTask() {
      IncomingLoginGuard.sendJavaPlugin(this.bukkitLoader);
   }

   @Override
   public StrictCommandHandler loadStrictCommandHandler(Runnable target, long input, TimeUnit context) {
      return new IncomingLoginGuard(this.player, this.enabled, this.storedLoginHandler, target).buildIncomingLoginGuard(this.bukkitLoader, input, context);
   }

   public PendingLoginFilter(BukkitLoader target, @Nullable Player input) {
      this(target, input, false);
   }
}
