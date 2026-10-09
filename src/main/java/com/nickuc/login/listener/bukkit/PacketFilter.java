package com.nickuc.login.listener.bukkit;

import com.nickuc.login.bukkit.PacketLink;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientLoginStart;
import com.nickuc.login.protocol.LenientCommandHandler;
import io.netty.channel.Channel;
import java.net.InetAddress;

public class PacketFilter implements LenientCommandHandler {
   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      WrapperLoginClientLoginStart input = new WrapperLoginClientLoginStart(target);
      String output = input.getUsername();
      Channel context = (Channel)target.getChannel();
      InetAddress data = target.getSocketAddress().getAddress();
      PacketLink value = new PacketLink(target.getUser(), null, output, null, false, null, context, null);
      context.attr(PacketLink.attributeKey).set(value);
      PacketLink.updateMessage(output, output, data, value);
   }
}
