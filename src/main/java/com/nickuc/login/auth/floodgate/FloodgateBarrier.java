package com.nickuc.login.auth.floodgate;


import org.geysermc.floodgate.api.player.FloodgatePlayer;

public class FloodgateBarrier {
   public final FloodgatePlayer floodgatePlayer;

   public FloodgateBarrier(FloodgatePlayer target) {
      this.floodgatePlayer = target;
   }
}
