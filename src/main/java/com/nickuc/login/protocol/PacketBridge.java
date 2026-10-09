package com.nickuc.login.protocol;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.nickuc.login.storage.settings.SettingsRepository;

public class PacketBridge implements QuickCommandHandler {
   public PacketBridge(SettingsRepository target) {
      this.settingsRepository = target;
   }

   @Override
   public void savePacketSendEvent(PacketSendEvent target) {
      if (!SettingsRepository.isState(this.settingsRepository)) {
         PacketWrapper input = new PacketWrapper(target);
         int output = input.readContainerId();
         SettingsRepository.handleSettingsRepository(this.settingsRepository, target, output);
      }
   }
}
