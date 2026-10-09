package com.nickuc.login.protocol;

import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.login.LoginDao;
import com.nickuc.login.storage.spawn.SpawnState;
import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

public class MessageAdapter implements QuickCommandHandler {
   @Override
   public void savePacketSendEvent(PacketSendEvent target) {
      if (SpawnState.PENDING_VERIFIED_SPAWNSTATE.ar()) {
         Object input = target.getPlayer();
         if (input != null) {
            VerifiedServerAdapter output = LoginDao.processPasswordStore(this.loginDao).b().processVerifiedServerAdapter(input);
            LimboCoordinator context = LoginDao.processPasswordStore(this.loginDao).loadLimboRegistry().buildLimboCoordinator(output);
            if (context != null) {
               if (!context.loadTightPlatformCatalog().isState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)) {
                  WrapperPlayServerSystemChatMessage data;
                  try {
                     data = new WrapperPlayServerSystemChatMessage(target);
                  } catch (Throwable request) {
                     if (!PasswordHashContainer.findState() && request instanceof IllegalArgumentException) {
                        PasswordHashContainer.performMessage("Unable to decode chat message for " + output.getName() + ": " + request.getMessage());
                     } else {
                        PasswordHashContainer.handleMessage(
                           "Unable to decode chat message for " + output.getName() + ", probably this is a bug! Skipping...", request
                        );
                     }

                     return;
                  }

                  if (!data.isOverlay()) {
                     Component value = data.getMessage();
                     if (value instanceof TranslatableComponent) {
                        TranslatableComponent result = (TranslatableComponent)value;
                        if ("multiplayer.message_not_delivered".equals(result.key())) {
                           target.setCancelled(true);
                           return;
                        }
                     }

                     List response = context.resolveObject(LenientMessageKind.AUTHENTICATED_LENIENTMESSAGEKIND, instance -> new ArrayList());
                     if (response.size() < 15) {
                        response.add(value);
                     }

                     target.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   private MessageAdapter(LoginDao target) {
      this.loginDao = target;
   }
}
