package com.nickuc.login.protocol;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.login.LoginDao;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.kyori.adventure.text.Component;

public class MessageHandler implements LenientCommandHandler, QuickCommandHandler {
   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      Object input = target.getPlayer();
      if (input != null) {
         VerifiedServerAdapter output = LoginDao.processPasswordStore(this.loginDao).b().processVerifiedServerAdapter(input);
         LimboCoordinator context = LoginDao.processPasswordStore(this.loginDao).loadLimboRegistry().buildLimboCoordinator(output);
         if (context == null) {
            target.setCancelled(true);
         } else {
            WrapperPlayClientChatMessage data = new WrapperPlayClientChatMessage(target);
            String value = data.getMessage();
            if (!(value = value.trim()).isEmpty()) {
               if (value.charAt(0) == '/') {
                  String result = LoginDao.processPasswordStore(this.loginDao).getFloodgateResolver().handleMessage(output, value);
                  if (result == null) {
                     target.setCancelled(true);
                     return;
                  }

                  data.setMessage(result);
                  target.markForReEncode(true);
                  if (value.length() < 2) {
                     return;
                  }

                  String[] request = value.substring(1).split(" ");
                  if (request.length < 1) {
                     return;
                  }

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
               } else if (LoginDao.processPasswordStore(this.loginDao).getFloodgateResolver().verifyState(output, value)) {
                  target.setCancelled(true);
               }
            }
         }
      }
   }

   private MessageHandler(LoginDao target) {
      this.loginDao = target;
   }

   @Override
   public void savePacketSendEvent(PacketSendEvent target) {
      if (SpawnState.PENDING_VERIFIED_SPAWNSTATE.ar()) {
         Object input = target.getPlayer();
         if (input != null) {
            VerifiedServerAdapter output = LoginDao.processPasswordStore(this.loginDao).b().processVerifiedServerAdapter(input);
            LimboCoordinator context = LoginDao.processPasswordStore(this.loginDao).loadLimboRegistry().buildLimboCoordinator(output);
            if (context != null) {
               if (!context.loadTightPlatformCatalog().isState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)) {
                  WrapperPlayServerChatMessage data = new WrapperPlayServerChatMessage(target);
                  ChatMessage value = data.getMessage();
                  if (value.getType() != ChatTypes.GAME_INFO) {
                     Component result = value.getChatContent();
                     List request = context.resolveObject(LenientMessageKind.AUTHENTICATED_LENIENTMESSAGEKIND, instance -> new ArrayList());
                     if (request.size() < 15) {
                        request.add(result);
                     }

                     target.setCancelled(true);
                  }
               }
            }
         }
      }
   }
}
