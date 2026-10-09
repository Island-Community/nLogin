package com.nickuc.login.model;

import com.nickuc.login.platform.sender.TightSenderAdapter;


public enum LinkedSpawnOption {
   LINKED_SPAWN_OPTION(InternalSpawnState.TOP_INTERNALSPAWNSTATE),
   ACTIVE_LINKEDSPAWNOPTION(InternalSpawnState.FAST_INTERNALSPAWNSTATE),
   PENDING_LINKEDSPAWNOPTION(InternalSpawnState.SAFE_INTERNALSPAWNSTATE);

   private final TightSenderAdapter tightSenderAdapter;

   LinkedSpawnOption(TightSenderAdapter output) {
      this.tightSenderAdapter = output;
   }
}
