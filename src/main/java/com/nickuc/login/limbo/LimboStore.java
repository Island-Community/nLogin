package com.nickuc.login.limbo;

import com.nickuc.login.model.PrivateNoticeKind;
import java.io.File;
import javax.annotation.Nullable;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LimboStore {
   public int count;
   public boolean enabled;
   public float factor;
   public boolean activeEnabled;
   public final Player player;
   public float activeFactor;
   public volatile PrivateNoticeKind privateNoticeKind = PrivateNoticeKind.PRIVATE_NOTICE_KIND;
   public final File dataFile;
   public double ratio;
   @Nullable
   public Location location;
   public Location activeLocation;
   public boolean pendingEnabled;
   public boolean currentEnabled;
   public boolean primaryEnabled;
   public boolean mainEnabled;
   public GameMode gameMode;
   public int activeCount;

   public void performLocation(Location target) {
      this.activeLocation = target;
   }

   public void saveTask() {
      this.pendingEnabled = true;
   }

   @Override
   public String toString() {
      return "PlayerLimbo(state=" + this.privateNoticeKind + ")";
   }

   public LimboStore(Player target, File input) {
      this.player = target;
      this.dataFile = input;
   }

   public void updateLocation(@Nullable Location target) {
      this.location = target;
   }
}
