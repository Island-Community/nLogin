package com.nickuc.login.listener.bukkit;

import com.nickuc.login.api.types.Location;
import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import com.nickuc.login.spawn.LoginResolver;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;

public class CachedLoginListener implements DirectPacketAdapter<Location> {
   public static CachedLoginListener cachedLoginListener = new CachedLoginListener();

   @Override
   public Class<?> loadClass() {
      return Location.class;
   }

   public Location buildLocation(@Nonnull JSONObject target) {
      String input = target.getString("encoded");
      org.bukkit.Location output = VerifiedLoginGuard.resolveLocation(input);
      return new LoginResolver(output.getWorld().getName(), output.getX(), output.getY(), output.getZ(), output.getYaw(), output.getPitch());
   }

   public JSONObject createJSONObject(@Nonnull Location target) {
      JSONObject input = new JSONObject();
      org.bukkit.Location output = new org.bukkit.Location(
         Bukkit.getServer().getWorld(target.getWorldName()), target.getX(), target.getY(), target.getZ(), target.getYaw(), target.getPitch()
      );
      input.put("encoded", VerifiedLoginGuard.createMessage(output));
      return input;
   }
}
