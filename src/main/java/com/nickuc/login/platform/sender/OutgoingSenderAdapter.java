package com.nickuc.login.platform.sender;

import com.nickuc.login.model.SecondarySpawnState;

public interface OutgoingSenderAdapter extends ChildListenerContract {
   boolean hasState(String target);

   String getName();

   void dispatchMessage(String target);

   default void processMessage(String target, Object... input) {
      this.dispatchMessage(String.format(target, input));
   }

   default SecondarySpawnState findSecondarySpawnState() {
      return this instanceof VerifiedServerAdapter ? SecondarySpawnState.SECONDARY_SPAWN_STATE : SecondarySpawnState.ACTIVE_SECONDARYSPAWNSTATE;
   }
}
