package com.nickuc.login.security.hashing;

import com.nickuc.login.auth.password.PasswordService;
import com.nickuc.login.model.NoticeKind;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.protocol.DialogHandler;
import com.nickuc.login.protocol.LenientCommandHandler;
import io.netty.channel.Channel;

public class PasswordHasher implements LenientCommandHandler {
   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      if (Pbkdf2Linker.retrieveNoticeKind() == NoticeKind.ACTIVE_NOTICEKIND) {
         if (!target.getServerVersion().isOlderThan(ServerVersion.V_1_21_6)) {
            Channel input = (Channel)target.getChannel();
            input.attr(DialogHandler.attributeKey).set(new DialogHandler(null, (byte)0, null));
         }
      }
   }

   public PasswordHasher(PasswordService target) {
      this.passwordService = target;
   }
}
