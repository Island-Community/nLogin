package com.nickuc.login.protocol;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.nickuc.login.storage.settings.SettingsRepository;

public class SharedPacketInterceptor implements QuickCommandHandler {
   public SharedPacketInterceptor(SettingsRepository target) {
      this.settingsRepository = target;
   }

   @Override
   public void savePacketSendEvent(PacketSendEvent target) {
      if (!SettingsRepository.isState(this.settingsRepository)) {
         PacketWrapper input = new PacketWrapper(target);
         int output = input.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_2) ? input.readContainerId() : input.readByte();
         SettingsRepository.handleSettingsRepository(this.settingsRepository, target, output);
      }
   }
}
