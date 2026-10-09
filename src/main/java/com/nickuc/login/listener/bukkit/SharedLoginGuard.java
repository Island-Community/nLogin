package com.nickuc.login.listener.bukkit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class SharedLoginGuard {
   @CheckReturnValue
   @Nullable
   public Location handleLocation(DataInputStream target) {
      String input = target.readUTF();
      double output = target.readDouble();
      double data = target.readDouble();
      double result = target.readDouble();
      float response = target.readFloat();
      float source = target.readFloat();
      World entry = Bukkit.getServer().getWorld(input);
      return entry != null ? new Location(entry, output, data, result, response, source) : null;
   }

   public void executeLocation(Location target, DataOutputStream input) {
      World output = target.getWorld();
      if (output == null) {
         throw new IllegalArgumentException("World is null!");
      }

      input.writeUTF(output.getName());
      input.writeDouble(target.getX());
      input.writeDouble(target.getY());
      input.writeDouble(target.getZ());
      input.writeFloat(target.getYaw());
      input.writeFloat(target.getPitch());
   }
}
