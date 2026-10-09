package com.nickuc.login.config;

import com.nickuc.login.model.NoticeKind;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.protocol.DialogHandler;
import com.nickuc.login.protocol.LenientCommandHandler;
import com.nickuc.login.protocol.QuickCommandHandler;
import com.nickuc.login.storage.spawn.SpawnState;
import io.netty.channel.Channel;

public class LowSettingsDefinition implements LenientCommandHandler, QuickCommandHandler {
   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      if (Pbkdf2Linker.retrieveNoticeKind() == NoticeKind.ACTIVE_NOTICEKIND) {
         Channel input = (Channel)target.getChannel();
         if (input.hasAttr(DialogHandler.attributeKey)) {
            DialogHandler output = (DialogHandler)input.attr(DialogHandler.attributeKey).get();
            if (output != null) {
               if (output.mask == 2) {
                  input.attr(DialogHandler.attributeKey).set(null);
                  Pbkdf2Linker.executeNoticeKind(NoticeKind.PENDING_NOTICEKIND);
                  PasswordHashContainer.performMessage(
                     "[Dialogs] The option configured for dialogs (" + Pbkdf2Linker.retrieveNoticeKind() + ") is not supported by the backend server."
                  );
                  PasswordHashContainer.performMessage(
                     "[Dialogs] How to solve: update your authentication servers to version 1.21.6 or higher, or change the option \""
                        + SpawnState.PENDING_CACHED_SPAWNSTATE.retrieveBusyLoginProcessor().fetchNames()[0]
                        + "\" to "
                        + NoticeKind.PENDING_NOTICEKIND
                        + "."
                  );
                  PasswordHashContainer.performMessage("[Dialogs] Falling back to the " + NoticeKind.PENDING_NOTICEKIND + " option...");
               }
            }
         }
      }
   }

   @Override
   public void savePacketSendEvent(PacketSendEvent target) {
      if (Pbkdf2Linker.retrieveNoticeKind() == NoticeKind.ACTIVE_NOTICEKIND) {
         Channel input = (Channel)target.getChannel();
         if (input.hasAttr(DialogHandler.attributeKey)) {
            DialogHandler output = (DialogHandler)input.attr(DialogHandler.attributeKey).get();
            if (output != null) {
               output.mask = 2;
            }
         }
      }
   }
}
