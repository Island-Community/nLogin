package com.nickuc.login.listener.bukkit;

import com.nickuc.login.bukkit.BukkitPlatform;


public class LocalLoginListener {
   public final SettingsListener settingsListener;
   public final PendingMessageListener pendingMessageListener = new PendingMessageListener(this);
   private final BukkitPlatform BukkitPlatform;

   public LocalLoginListener(BukkitPlatform target) {
      this.settingsListener = new SettingsListener(this);
      this.BukkitPlatform = target;
   }
}
