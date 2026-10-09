package com.nickuc.login.protocol;

import com.nickuc.login.config.PasswordHashContainer;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.player.User;

public class PacketHandler extends PacketListenerAbstract {
   public void onPacketSend(PacketSendEvent target) {
      if (!target.isCancelled()) {
         try {
            QuickCommandHandler input = (QuickCommandHandler)RootMessageHandler.loadTable(this.rootMessageHandler).get(target.getPacketType());
            if (input != null) {
               input.savePacketSendEvent(target);
            }
         } catch (Throwable data) {
            target.setCancelled(true);
            ChannelHelper.close(target.getChannel());
            User output = target.getUser();
            String context = "server = " + target.getServerVersion() + (output != null ? ", client = " + output.getClientVersion() : "");
            PasswordHashContainer.handleMessage("Unable to handle outgoing packet  " + target.getPacketType() + " (" + context + ")", data);
         }
      }
   }

   public void onPacketReceive(PacketReceiveEvent target) {
      if (!target.isCancelled()) {
         try {
            LenientCommandHandler input = (LenientCommandHandler)RootMessageHandler.resolveTable(this.rootMessageHandler).get(target.getPacketType());
            if (input != null) {
               input.dispatchPacketReceiveEvent(target);
            }
         } catch (Throwable data) {
            target.setCancelled(true);
            ChannelHelper.close(target.getChannel());
            User output = target.getUser();
            String context = "server = " + target.getServerVersion() + (output != null ? ", client = " + output.getClientVersion() : "");
            PasswordHashContainer.handleMessage("Unable to handle incoming packet " + target.getPacketType() + " (" + context + ")", data);
         }
      }
   }

   public PacketHandler(RootMessageHandler target) {
      this.rootMessageHandler = target;
   }
}
