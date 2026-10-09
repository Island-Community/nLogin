package com.nickuc.login.protocol;

import com.nickuc.login.auth.password.PasswordService;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.configuration.client.WrapperConfigClientSelectKnownPacks;
import com.nickuc.login.platform.player.ChainedPlayerContract;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class PendingSettingsInterceptor implements LenientCommandHandler {
   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      Channel input = (Channel)target.getChannel();
      if (input.hasAttr(DialogHandler.attributeKey)) {
         DialogHandler output = (DialogHandler)input.attr(DialogHandler.attributeKey).get();
         if (output != null && output.mask == 0) {
            WrapperConfigClientSelectKnownPacks context = new WrapperConfigClientSelectKnownPacks(target);
            User data = target.getUser();
            ChainedPlayerContract value = outputValue -> {
               if (outputValue) {
                  this.sendUser(data, context);
               }
            };
            if (this.passwordService.validateState(data, value)) {
               if (PasswordService.resolvePasswordStore(this.passwordService).loadState()) {
                  Object result = target.getPlayer();
                  if (result != null) {
                     VerifiedServerAdapter request = PasswordService.resolvePasswordStore(this.passwordService).b().processVerifiedServerAdapter(result);
                     LimboCoordinator response = PasswordService.resolvePasswordStore(this.passwordService).loadLimboRegistry().loadLimboCoordinator(request);
                     SecondarySenderAdapter source = PasswordService.resolvePasswordStore(this.passwordService).findObject();
                     String entry = source.handleMessage(request);
                     if (entry != null) {
                        source.getPasswordHashAdapter().dispatchVerifiedServerAdapter(request, response, entry);
                     }
                  }

                  target.setCancelled(true);
               }
            } else {
               input.attr(DialogHandler.attributeKey).set(null);
            }
         }
      }
   }

   private void sendUser(User target, PacketWrapper<?> input) {
      if (PasswordService.resolvePasswordStore(this.passwordService).findIncomingLoginGate().retrieveSilentProxyState().loadState()) {
         Channel output = (Channel)target.getChannel();
         input.prepareForSend(output, false, true);
         ByteBuf context = (ByteBuf)input.buffer;
         if (context == null) {
            throw new IllegalStateException("Buffer cannot be null!");
         }

         if (ChannelHelper.isOpen(output)) {
            ChannelHelper.fireChannelReadInContext(output, PacketEvents.DECODER_NAME, context);
         } else {
            context.release();
         }
      } else {
         target.receivePacketSilently(input);
      }
   }

   public PendingSettingsInterceptor(PasswordService target) {
      this.passwordService = target;
   }
}
