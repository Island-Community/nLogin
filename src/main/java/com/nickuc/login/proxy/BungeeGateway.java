package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.StoredLoginHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.command.StrictCommandHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeGateway implements LinkedSessionHandler {
   private final StoredLoginHandler storedLoginHandler = new StoredLoginHandler();
   private final Plugin plugin;

   public LocalBungeeBridge createLocalBungeeBridge(Consumer<StrictCommandHandler> target, long input, TimeUnit context) {
      return new LocalBungeeBridge(this.storedLoginHandler, target).computeLocalBungeeBridge(this.plugin, input, context);
   }

   public LocalBungeeBridge createLocalBungeeBridge(Runnable target, long input, TimeUnit context) {
      return new LocalBungeeBridge(this.storedLoginHandler, target).computeLocalBungeeBridge(this.plugin, input, context);
   }

   public LocalBungeeBridge processLocalBungeeBridge(Consumer<StrictCommandHandler> target) {
      return new LocalBungeeBridge(this.storedLoginHandler, target).computeLocalBungeeBridge(this.plugin);
   }

   public BungeeGateway(Plugin target) {
      this.plugin = target;
   }

   @Override
   public void dispatchTask() {
      this.plugin.getProxy().getScheduler().cancel(this.plugin);
   }

   public LocalBungeeBridge handleLocalBungeeBridge(Runnable target) {
      return new LocalBungeeBridge(this.storedLoginHandler, target).computeLocalBungeeBridge(this.plugin);
   }

   public LocalBungeeBridge loadLocalBungeeBridge(Runnable target, long input, long context, TimeUnit value) {
      return new LocalBungeeBridge(this.storedLoginHandler, target).handleLocalBungeeBridge(this.plugin, input, context, value);
   }

   @Override
   public StoredLoginHandler loadStoredLoginHandler() {
      return this.storedLoginHandler;
   }

   public LocalBungeeBridge computeLocalBungeeBridge(Consumer<StrictCommandHandler> target, long input, long context, TimeUnit value) {
      return new LocalBungeeBridge(this.storedLoginHandler, target).handleLocalBungeeBridge(this.plugin, input, context, value);
   }
}
