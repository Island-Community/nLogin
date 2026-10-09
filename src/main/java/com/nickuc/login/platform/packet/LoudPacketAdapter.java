package com.nickuc.login.platform.packet;

import com.nickuc.login.auth.login.CachedLoginProcessor;
import com.nickuc.login.premium.SpawnLookup;
import javax.annotation.Nullable;

public interface LoudPacketAdapter {
   boolean resolveState();

   boolean findState();

   void updateTask();

   @Nullable
   CachedLoginProcessor resolveCachedLoginProcessor();

   String loadMessage();

   void sendMessage(String target, @Nullable String input);

   SpawnLookup loadSpawnLookup();
}
