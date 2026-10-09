package com.nickuc.login.discord;

import com.nickuc.login.auth.bedrock.BedrockCheckpoint;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.listener.bukkit.MessageListener;
import com.nickuc.login.listener.bukkit.SettingsFilter;
import com.nickuc.login.listener.bukkit.SettingsGuard;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.connection.StrictConnectionContract;
import com.nickuc.login.premium.ReadyBedrockResolver;
import com.nickuc.login.storage.login.LoginCollection;
import com.nickuc.login.storage.password.PasswordStore;

import org.bukkit.plugin.PluginManager;

public class DiscordListener implements InternalAccountHandler, StrictConnectionContract {
   private LowDiscordBridge lowDiscordBridge;
   private LoginCollection loginCollection;
   private final BukkitPlatform BukkitPlatform;
   private SecondaryConnectionContract secondaryConnectionContract;
   private SettingsGuard settingsGuard;

   @Override
   public void processTask() {
      if (this.loginCollection != null) {
         MessageListener target = this.BukkitPlatform.findMessageListener();
         target.updateObject("nlogin:addon");
      }

      this.secondaryConnectionContract = null;
   }

   @Override
   public LoginCollection getLoginCollection() {
      return this.loginCollection;
   }

   @Override
   public SettingsGuard findSettingsGuard() {
      return this.settingsGuard;
   }

   @Override
   public SecondaryConnectionContract retrieveSecondaryConnectionContract() {
      return this.secondaryConnectionContract;
   }

   @Override
   public void executeTask() {
      PasswordStore target = this.BukkitPlatform.fetchPasswordStore();
      PluginManager input = this.BukkitPlatform.getServer().getPluginManager();
      if (input.getPlugin("Citizens") != null) {
         this.settingsGuard = new SettingsGuard();
      }

      if (input.getPlugin("DiscordSRV") != null) {
         this.lowDiscordBridge = new LowDiscordBridge();
      }

      if (input.getPlugin("floodgate") != null) {
         this.secondaryConnectionContract = new ReadyBedrockResolver(target);
      } else if (input.getPlugin("Geyser-Spigot") != null) {
         this.secondaryConnectionContract = new BedrockCheckpoint();
      } else {
         this.secondaryConnectionContract = null;
      }

      if (this.loginCollection == null) {
         this.loginCollection = new LoginCollection(target);
         SettingsFilter output = new SettingsFilter(this.BukkitPlatform, this.loginCollection);
         MessageListener context = this.BukkitPlatform.findMessageListener();
         context.dispatchObject("nlogin:addon", output);
      }
   }

   @Override
   public void processPasswordStore(PasswordStore target, boolean input) {
      this.executeTask();
   }

   public DiscordListener(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }

   public LowDiscordBridge fetchLowDiscordBridge() {
      return this.lowDiscordBridge;
   }
}
