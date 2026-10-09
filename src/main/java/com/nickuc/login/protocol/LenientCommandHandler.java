package com.nickuc.login.protocol;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;

public interface LenientCommandHandler {
   default void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
   }
}
