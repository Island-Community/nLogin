package com.nickuc.login.bukkit;

import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.listener.bukkit.DiscordGuard;
import com.nickuc.login.loader.MemClassLoader;
import com.nickuc.login.loader.platform.BukkitLoader;
import com.nickuc.login.platform.listener.ListenerContract;
import com.nickuc.login.platform.server.ServerAdapter;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.session.LimboSupervisor;
import com.nickuc.login.session.MojangCoordinator;
import com.nickuc.login.spawn.VerifiedNoticeKind;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.proxy.ProxyRepository;
import com.nickuc.login.storage.settings.SettingsStore;
import java.util.Collections;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class BukkitPlatform extends DiscordGuard implements ListenerContract {
   private boolean enabled;
   private LimboSupervisor limboSupervisor;

   public BukkitPlatform(BukkitLoader target) {
      super(target, "nLogin", null);
   }

   @Override
   public boolean callEvent(Object target) {
      this.getServer().getPluginManager().callEvent((Event)target);
      return !(target instanceof Cancellable) || !((Cancellable)target).isCancelled();
   }

   @Override
   public void performTask() {
      if (this.enabled) {
         this.limboSupervisor = new LimboSupervisor(this);
      }

      ServerAdapter target = this.enabled ? new ProxyRepository(this) : new SettingsStore(this);
      this.a(new PasswordStore(target, this, this.enabled));
      super.performTask();
   }

   public boolean getState() {
      return this.enabled;
   }

   @Override
   public void dispatchTask() {
      try {
         MemClassLoader target = this.getParentDiscordNotifier().loadMemClassLoader();
         ClassLoader input = target.getParentLoader();
         (input.getParent() != null ? input.getParent() : ClassLoader.getSystemClassLoader()).loadClass("net.kyori.adventure.Adventure");
         target.configureFilter(Collections.singleton("net.kyori"));
      } catch (ClassNotFoundException output) {
      }

      this.enabled = (boolean)(MojangCoordinator.activeEnabled || !BungeeWriter.resolveState() && !BungeeWriter.retrieveState() ? 0 : 1);
      if (this.enabled) {
         this.b(0);
      }

      super.dispatchTask();
   }

   public LimboSupervisor resolveLimboSupervisor() {
      return this.limboSupervisor;
   }

   public PasswordStore fetchPasswordStore() {
      return (PasswordStore)super.b();
   }

   @Override
   public Class<?> getPlayerClass() {
      return Player.class;
   }

   @Override
   public TightSenderAdapter[] findValues() {
      return VerifiedNoticeKind.values();
   }
}
