package com.nickuc.login.listener.bukkit;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.nickuc.login.protocol.LenientCommandHandler;
import org.bukkit.entity.Player;

public class PendingMessageListener implements LenientCommandHandler {
   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      WrapperPlayClientPluginMessage input = new WrapperPlayClientPluginMessage(target);
      String output = input.getChannelName();
      if (output.equals("nlogin:main")) {
         Player context = (Player)target.getPlayer();
         if (context != null) {
            target.setCancelled(true);
            LocalLoginListener.createNLoginBukkit(this.localLoginListener)
               .resolveLimboSupervisor()
               .sendVerifiedServerAdapter(LocalLoginListener.createNLoginBukkit(this.localLoginListener).b().processVerifiedServerAdapter(context), input.getData());
         }
      }
   }

   public PendingMessageListener(LocalLoginListener target) {
      this.localLoginListener = target;
   }
}
