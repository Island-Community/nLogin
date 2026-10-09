package com.nickuc.login.protocol;

import com.github.retrooper.packetevents.event.PacketSendEvent;

public interface QuickCommandHandler {
   default void savePacketSendEvent(PacketSendEvent target) {
   }
}
