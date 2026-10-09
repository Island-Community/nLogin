package com.nickuc.login.listener.bukkit;

import com.nickuc.login.auth.login.StoredLoginHandler;
import com.nickuc.login.loader.platform.BukkitLoader;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.command.StrictCommandHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class CachedLoginFilter implements LinkedSessionHandler {
   private final BukkitLoader bukkitLoader;
   private final StoredLoginHandler storedLoginHandler = new StoredLoginHandler();
   private final boolean enabled;

   @Override
   public StrictCommandHandler processStrictCommandHandler(Runnable target, long input, long context, TimeUnit value) {
      return new PrimaryLoginGuard(this.enabled, this.storedLoginHandler, target).computePrimaryLoginGuard(this.bukkitLoader, input, context, value);
   }

   public CachedLoginFilter(BukkitLoader target, boolean input) {
      this.bukkitLoader = target;
      this.enabled = input;
   }

   @Override
   public void dispatchTask() {
      PrimaryLoginGuard.sendJavaPlugin(this.bukkitLoader);
   }

   @Override
   public StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target) {
      return new PrimaryLoginGuard(this.enabled, this.storedLoginHandler, target).resolvePrimaryLoginGuard(this.bukkitLoader);
   }

   @Override
   public StrictCommandHandler loadStrictCommandHandler(Runnable target, long input, TimeUnit context) {
      return new PrimaryLoginGuard(this.enabled, this.storedLoginHandler, target).buildPrimaryLoginGuard(this.bukkitLoader, input, context);
   }

   @Override
   public StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target, long input, long context, TimeUnit value) {
      return new PrimaryLoginGuard(this.enabled, this.storedLoginHandler, target).computePrimaryLoginGuard(this.bukkitLoader, input, context, value);
   }

   @Override
   public StrictCommandHandler loadStrictCommandHandler(Consumer<StrictCommandHandler> target, long input, TimeUnit context) {
      return new PrimaryLoginGuard(this.enabled, this.storedLoginHandler, target).buildPrimaryLoginGuard(this.bukkitLoader, input, context);
   }

   @Override
   public StrictCommandHandler buildStrictCommandHandler(Runnable target) {
      return new PrimaryLoginGuard(this.enabled, this.storedLoginHandler, target).resolvePrimaryLoginGuard(this.bukkitLoader);
   }

   @Override
   public StoredLoginHandler loadStoredLoginHandler() {
      return this.storedLoginHandler;
   }
}
