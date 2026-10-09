package com.nickuc.login.storage.packet;

import com.nickuc.login.model.LenientMessageKind;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.LenientCommandHandler;
import com.nickuc.login.session.LimboCoordinator;


public class PacketArchive implements LenientCommandHandler {
   private final PasswordStore passwordStore;

   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      Object input = target.getPlayer();
      if (input != null) {
         VerifiedServerAdapter output = this.passwordStore.b().processVerifiedServerAdapter(input);
         LimboCoordinator context = this.passwordStore.loadLimboRegistry().buildLimboCoordinator(output);
         if (context != null) {
            context.updateLenientMessageKind(LenientMessageKind.REMOTE_LENIENTMESSAGEKIND, System.currentTimeMillis());
         }
      }
   }

   public PacketArchive(PasswordStore target) {
      this.passwordStore = target;
   }
}
