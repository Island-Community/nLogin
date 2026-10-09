package com.nickuc.login.protocol;

import com.nickuc.login.account.InternalLoginOption;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatCommand;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.login.LoginDao;
import java.util.Locale;


public class LocaleInterceptor implements LenientCommandHandler {
   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      Object input = target.getPlayer();
      if (input != null) {
         VerifiedServerAdapter output = LoginDao.processPasswordStore(this.loginDao).b().processVerifiedServerAdapter(target.getPlayer());
         LimboCoordinator context = LoginDao.processPasswordStore(this.loginDao).loadLimboRegistry().buildLimboCoordinator(output);
         if (context == null) {
            target.setCancelled(true);
         } else {
            WrapperPlayClientChatCommand data = new WrapperPlayClientChatCommand(target);
            String value = data.getCommand().trim();
            if (!value.isEmpty()) {
               String result = LoginDao.processPasswordStore(this.loginDao).getFloodgateResolver().handleMessage(output, '/' + value);
               if (result == null) {
                  target.setCancelled(true);
               } else {
                  if (result.charAt(0) == '/') {
                     result = result.substring(1);
                  }

                  if (data.getMessageSignData().getSaltSignature().getSignature().length == 0 && data.getMessageSignData().getSaltSignature().getSalt() == 0L) {
                     data.setCommand(result);
                     target.markForReEncode(true);
                  }

                  String[] request = value.split(" ");
                  if (request.length >= 1) {
                     InternalLoginOption response = LoginDao.processPasswordStore(this.loginDao)
                        .findIndirectPasswordHashVerifier()
                        .computeInternalLoginOption(request[0].toLowerCase(Locale.ENGLISH));
                     if (response != null) {
                        String[] source = new String[request.length - 1];
                        if (source.length > 0) {
                           System.arraycopy(request, 1, source, 0, source.length);
                        }

                        response.performVerifiedServerAdapter(output, context, request[0], source);
                     }
                  }
               }
            }
         }
      }
   }

   private LocaleInterceptor(LoginDao target) {
      this.loginDao = target;
   }
}
