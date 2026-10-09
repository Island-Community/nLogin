package com.nickuc.login.protocol;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.exception.InvalidHandshakeException;
import com.github.retrooper.packetevents.wrapper.handshaking.client.WrapperHandshakingClientHandshake;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import java.util.Map.Entry;

public class Sha256Handler implements LenientCommandHandler {
   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      WrapperHandshakingClientHandshake input;
      try {
         input = new WrapperHandshakingClientHandshake(target);
      } catch (InvalidHandshakeException context) {
         return;
      }

      Channel output = (Channel)target.getChannel();
      output.attr(Sha256Bridge.attributeKey).set(new Sha256Bridge(input.getProtocolVersion(), buildMessage(input.getServerAddress()), computeObject(output)));
   }

   private static Object computeObject(Channel instance) {
      for (Entry input : instance.pipeline()) {
         if (SpigotReflectionUtil.NETWORK_MANAGER_CLASS.isAssignableFrom(((ChannelHandler)input.getValue()).getClass())) {
            return input.getValue();
         }
      }

      throw new IllegalArgumentException("Unable to find NetworkManager in " + instance);
   }

   private static String buildMessage(String instance) {
      String target = instance;
      int input = target.indexOf(0);
      if (input > -1) {
         target = instance.substring(0, input);
      }

      if (!target.isEmpty() && target.charAt(target.length() - 1) == '.') {
         target = target.substring(0, target.length() - 1);
      }

      return target;
   }
}
