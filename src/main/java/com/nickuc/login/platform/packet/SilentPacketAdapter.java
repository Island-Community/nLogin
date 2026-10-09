package com.nickuc.login.platform.packet;

import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.premium.OpenPasswordHashProvider;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.password.PasswordStore;

public interface SilentPacketAdapter extends UpstreamAccountHandler {
   default OpenPasswordHashProvider loadOpenPasswordHashProvider(LimboCoordinator target) {
      return target.resolveObject(LenientMessageKind.INTERNAL_LENIENTMESSAGEKIND, instance -> OpenPasswordHashProvider.loadOpenPasswordHashProvider());
   }

   @Override
   default boolean at() {
      return false;
   }

   @Override
   default boolean canState(PasswordStore target) {
      return target.fetchLocalSettingsRepository().loadState();
   }
}
