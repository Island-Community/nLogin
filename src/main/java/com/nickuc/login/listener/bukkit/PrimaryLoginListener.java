package com.nickuc.login.listener.bukkit;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.nickuc.login.auth.login.LenientLoginFlow;
import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.listener.PacketAdapter;
import java.net.InetAddress;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class PrimaryLoginListener implements PacketAdapter {
   private final BukkitPlatform BukkitPlatform;
   public static final boolean enabled = (boolean)(LoginCheckpoint.handleMethod(AsyncPlayerPreLoginEvent.class, "getPlayerProfile") != null ? 1 : 0);

   public PrimaryLoginListener(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void handleAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent target) {
      if (target.getLoginResult() == Result.ALLOWED) {
         InetAddress input = target.getAddress();

         InetAddress output;
         try {
            output = target.getRawAddress();
         } catch (NoSuchMethodError request) {
            output = null;
         }

         PacketLink context = PacketLink.processPacketLink(target.getName(), input, output);
         if (context != null) {
            UUID data = context.uniqueId;
            LenientLoginFlow value = context.lenientLoginFlow;
            if (data != null || value != null) {
               PlayerProfile result = target.getPlayerProfile();
               if (data != null) {
                  result.setId(data);
               }

               if (value != null) {
                  result.setProperty(new ProfileProperty("textures", value.activeName, value.name));
               }
            }
         }
      }
   }
}
