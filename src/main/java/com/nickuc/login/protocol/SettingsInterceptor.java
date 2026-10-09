package com.nickuc.login.protocol;

import com.nickuc.login.auth.login.SafeLoginService;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.configuration.server.WrapperConfigServerDisconnect;
import com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerDisconnect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDisconnect;
import net.kyori.adventure.text.Component;

public class SettingsInterceptor {
   public static void executeUser(User instance, Component target) {
      switch (instance.getEncoderState()) {
         case LOGIN:
            instance.sendPacketSilently(new WrapperLoginServerDisconnect(target));
            break;
         case CONFIGURATION:
            instance.sendPacketSilently(new WrapperConfigServerDisconnect(target));
            break;
         case PLAY:
            instance.sendPacketSilently(new WrapperPlayServerDisconnect(target));
            break;
         default:
            instance.closeConnection();
            throw new IllegalStateException("Unsupported connection state! enc = " + instance.getEncoderState() + ", dec = " + instance.getDecoderState());
      }
   }

   public static void sendUser(User instance, String target) {
      executeUser(instance, Component.translatable(target));
   }

   public static void handleUser(User instance, String target) {
      executeUser(instance, SafeLoginService.resolveTextComponent(target));
   }
}
