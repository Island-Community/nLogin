package com.nickuc.login.platform.proxy;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.command.VerifiedLocaleAction;
import com.nickuc.login.command.completion.PrimaryCompletionAdminCommand;
import com.nickuc.login.command.completion.UnregisterBackupCommand;
import com.nickuc.login.listener.bukkit.CurrentMessageListener;
import com.nickuc.login.listener.bukkit.LoginFilter;
import com.nickuc.login.listener.bukkit.MessageGuard;
import com.nickuc.login.listener.bukkit.ParentLoginFilter;
import com.nickuc.login.listener.bukkit.ParentLoginGuard;
import com.nickuc.login.listener.bukkit.PrimaryLoginListener;
import com.nickuc.login.listener.bukkit.ReadyLoginFilter;
import com.nickuc.login.listener.bukkit.RemoteLoginListener;
import com.nickuc.login.listener.bukkit.SecondaryLoginGuard;
import com.nickuc.login.listener.bukkit.SharedLoginListener;
import com.nickuc.login.listener.bukkit.SpawnListener;
import com.nickuc.login.premium.SettingsLinker;
import com.nickuc.login.security.hashing.PasswordHashProvider;
import com.nickuc.login.session.BusyLimboStore;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.settings.StrictSettingsGateway;
import com.nickuc.login.storage.spawn.VerifiedSpawnDao;
import javax.annotation.Nullable;

public enum StoredProxyCatalog {
   STORED_PROXY_CATALOG(true, LoginFilter.class, PasswordHashProvider.class),
   ACTIVE_STOREDPROXYCATALOG(true, ParentLoginGuard.class, PasswordHashProvider.class),
   PENDING_STOREDPROXYCATALOG(false, VerifiedLocaleAction.class, BukkitPlatform.class, LimboRegistry.class, PasswordHashProvider.class, boolean.class),
   CURRENT_STOREDPROXYCATALOG(false, MessageGuard.class, BukkitPlatform.class),
   PRIMARY_STOREDPROXYCATALOG(false, CurrentMessageListener.class, BukkitPlatform.class),
   MAIN_STOREDPROXYCATALOG(false, StrictSettingsGateway.class, PasswordStore.class),
   LOCAL_STOREDPROXYCATALOG(false, ReadyLoginFilter.class, BukkitPlatform.class, PasswordHashProvider.class, boolean.class),
   REMOTE_STOREDPROXYCATALOG("org.bukkit.event.player.PlayerInteractAtEntityEvent", ParentLoginFilter.class, PasswordHashProvider.class),
   CACHED_STOREDPROXYCATALOG("org.bukkit.event.player.PlayerSwapHandItemsEvent", RemoteLoginListener.class, PasswordHashProvider.class),
   STORED_STOREDPROXYCATALOG(false, SpawnListener.class, BukkitPlatform.class, BusyLimboStore.class),
   VERIFIED_STOREDPROXYCATALOG(false, VerifiedSpawnDao.class, PasswordStore.class, BusyLimboStore.class),
   AUTHENTICATED_STOREDPROXYCATALOG("org.bukkit.event.entity.EntityAirChangeEvent", SecondaryLoginGuard.class, PasswordHashProvider.class),
   SHARED_STOREDPROXYCATALOG("org.bukkit.event.player.PlayerCommandSendEvent", PrimaryCompletionAdminCommand.class, BukkitPlatform.class),
   PRIVATE_STOREDPROXYCATALOG("org.bukkit.event.server.TabCompleteEvent", UnregisterBackupCommand.class, PasswordStore.class),
   INTERNAL_STOREDPROXYCATALOG(false, SharedLoginListener.class, BukkitPlatform.class),
   UPSTREAM_STOREDPROXYCATALOG(false, PrimaryLoginListener.class, BukkitPlatform.class);

   private final Class<?> value;
   private final String name;
   private final boolean enabled;
   private final Class<?>[] values;
   private boolean activeEnabled;

   @Nullable
   private OutgoingAccountHandler createOutgoingAccountHandler(Object... target) {
      try {
         if (this.name != null && !ChildLoginCheckpoint.validateState(this.name)) {
            return null;
         } else if (target.length != this.values.length) {
            throw new IllegalArgumentException("Invalid values length for listener " + this + "! " + target.length + " != " + this.values.length);
         } else {
            Object input = this.value.getConstructor(this.values).newInstance(target);
            if (!(input instanceof OutgoingAccountHandler)) {
               throw new UnsupportedOperationException("Listener " + this + " does not implements a SharedListener!");
            } else {
               return (OutgoingAccountHandler)input;
            }
         }
      } catch (Exception output) {
         throw new RuntimeException("Failed to create instance for listener " + this + ".", output);
      }
   }

   public boolean fetchState() {
      return this.activeEnabled;
   }

   StoredProxyCatalog(String output, Class<?> context, Class<?>... data) {
      this.name = output;
      this.enabled = true;
      this.value = context;
      this.values = data;
   }

   StoredProxyCatalog(boolean output, Class<?> context, Class<?>... data) {
      this.name = null;
      this.enabled = output;
      this.value = context;
      this.values = data;
   }

   public static void saveNLoginBukkit(BukkitPlatform instance, boolean target) {
      PasswordStore input = instance.fetchPasswordStore();

      for (StoredProxyCatalog value : values()) {
         if (value.enabled) {
            Object[] result = new Object[value.values.length];

            for (int request = 0; request < result.length; request++) {
               Class response = value.values[request];
               if (LimboRegistry.class.isAssignableFrom(response)) {
                  result[request] = input.loadLimboRegistry();
               } else if (InternalAccountHandler.class.isAssignableFrom(response)) {
                  result[request] = input.loadInternalAccountHandler();
               } else if (SettingsLinker.class.isAssignableFrom(response)) {
                  result[request] = input.findSettingsLinker();
               } else if (PasswordStore.class.isAssignableFrom(response)) {
                  result[request] = input;
               } else if (BukkitPlatform.class.isAssignableFrom(response)) {
                  result[request] = instance;
               } else {
                  if (!PasswordHashProvider.class.isAssignableFrom(response)) {
                     throw new UnsupportedOperationException("Unsupported constructor class for listener " + value + "! " + response);
                  }

                  result[request] = input.getFloodgateResolver();
               }
            }

            value.updateNLoginBukkit(instance, result);
         }
      }

      (ChildLoginCheckpoint.validateState("io.papermc.paper.event.player.AsyncChatEvent") ? PRIMARY_STOREDPROXYCATALOG : CURRENT_STOREDPROXYCATALOG)
         .updateNLoginBukkit(instance, instance);
      if (ChildLoginCheckpoint.validateState("io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent")) {
         VERIFIED_STOREDPROXYCATALOG.updateNLoginBukkit(instance, input, input.findSettingsLinker());
      } else if (ChildLoginCheckpoint.validateState("org.spigotmc.event.player.PlayerSpawnLocationEvent")) {
         STORED_STOREDPROXYCATALOG.updateNLoginBukkit(instance, instance, input.findSettingsLinker());
      }

      if (!ReadyLoginFilter.enabled) {
         LOCAL_STOREDPROXYCATALOG.updateNLoginBukkit(instance, instance, input.getFloodgateResolver(), target);
      }

      PENDING_STOREDPROXYCATALOG.updateNLoginBukkit(instance, instance, input.loadLimboRegistry(), input.getFloodgateResolver(), target);
      if (!target && ChildLoginCheckpoint.validateState("io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent")) {
         MAIN_STOREDPROXYCATALOG.updateNLoginBukkit(instance, instance.fetchPasswordStore());
      }

      (PrimaryLoginListener.enabled ? UPSTREAM_STOREDPROXYCATALOG : INTERNAL_STOREDPROXYCATALOG).updateNLoginBukkit(instance, instance);
   }

   public void updateNLoginBukkit(BukkitPlatform target, Object... input) {
      OutgoingAccountHandler output = this.createOutgoingAccountHandler(input);
      if (output != null) {
         target.a(output, new OutgoingAccountHandler[0]);
         this.activeEnabled = true;
      }
   }
}
