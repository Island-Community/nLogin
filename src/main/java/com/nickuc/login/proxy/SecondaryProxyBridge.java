package com.nickuc.login.proxy;

import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.listener.bukkit.SettingsGuard;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.connection.StrictConnectionContract;
import com.nickuc.login.storage.login.LoginCollection;
import com.nickuc.login.storage.password.PasswordStore;

import org.bukkit.plugin.PluginManager;

public class SecondaryProxyBridge implements InternalAccountHandler, StrictConnectionContract {
   private SettingsGuard settingsGuard;
   private final BukkitPlatform BukkitPlatform;

   @Override
   public void processTask() {
   }

   @Override
   public void processPasswordStore(PasswordStore target, boolean input) {
      this.executeTask();
   }

   @Override
   public void executeTask() {
      PluginManager target = this.BukkitPlatform.getServer().getPluginManager();
      if (target.getPlugin("Citizens") != null) {
         this.settingsGuard = new SettingsGuard();
      }
   }

   @Override
   public LoginCollection getLoginCollection() {
      throw new UnsupportedOperationException("Unsupported method in proxy mode!");
   }

   @Override
   public SecondaryConnectionContract retrieveSecondaryConnectionContract() {
      throw new UnsupportedOperationException("Unsupported method in proxy mode!");
   }

   public SecondaryProxyBridge(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }

   @Override
   public SettingsGuard findSettingsGuard() {
      return this.settingsGuard;
   }
}
