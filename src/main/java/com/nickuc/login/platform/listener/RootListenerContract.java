package com.nickuc.login.platform.listener;

import com.nickuc.login.premium.SpawnLookup;

public interface RootListenerContract {
   void performSpawnLookup(SpawnLookup target, VerifiedServerAdapter input);

   void saveSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, String output);

   void dispatchSpawnLookup(SpawnLookup target, VerifiedServerAdapter input);

   boolean findState();

   void updateTask();

   void updateSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, String output);

   void processSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, String output);

   default boolean loadState() {
      return false;
   }
}
