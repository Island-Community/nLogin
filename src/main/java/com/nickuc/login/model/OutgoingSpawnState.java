package com.nickuc.login.model;

import com.nickuc.login.storage.spawn.SpawnState;


public enum OutgoingSpawnState {
   OUTGOING_SPAWN_STATE(SpawnState.PRIMARY_SPAWNSTATE),
   ACTIVE_OUTGOINGSPAWNSTATE(SpawnState.MAIN_SPAWNSTATE),
   PENDING_OUTGOINGSPAWNSTATE(SpawnState.LOCAL_SPAWNSTATE),
   CURRENT_OUTGOINGSPAWNSTATE(SpawnState.REMOTE_SPAWNSTATE),
   PRIMARY_OUTGOINGSPAWNSTATE(SpawnState.CACHED_SPAWNSTATE),
   MAIN_OUTGOINGSPAWNSTATE(SpawnState.STORED_SPAWNSTATE),
   LOCAL_OUTGOINGSPAWNSTATE(SpawnState.VERIFIED_SPAWNSTATE),
   REMOTE_OUTGOINGSPAWNSTATE(SpawnState.AUTHENTICATED_SPAWNSTATE),
   CACHED_OUTGOINGSPAWNSTATE(SpawnState.SHARED_SPAWNSTATE),
   STORED_OUTGOINGSPAWNSTATE(SpawnState.PRIVATE_SPAWNSTATE),
   VERIFIED_OUTGOINGSPAWNSTATE(SpawnState.INTERNAL_SPAWNSTATE),
   AUTHENTICATED_OUTGOINGSPAWNSTATE(SpawnState.UPSTREAM_SPAWNSTATE);

   private final SpawnState spawnState;

   OutgoingSpawnState(SpawnState output) {
      this.spawnState = output;
   }

   public static String buildMessage(String instance, OutgoingSpawnState... target) {
      if (instance == null) {
         throw new IllegalArgumentException("Input cannot be null!");
      }

      String[] input = new String[target.length];

      for (int output = 0; output < input.length; output++) {
         input[output] = target[output].getName();
      }

      return String.format(instance, input);
   }

   public String getName() {
      return this.spawnState.a(new Object[0]);
   }
}
