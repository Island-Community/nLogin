package com.nickuc.login.listener.proxy;

import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import io.netty.channel.Channel;
import java.lang.reflect.Field;
import javax.annotation.Nullable;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.netty.ChannelWrapper;

public class StrictBungeeListener {
   private static final Field field = LoginCheckpoint.handleField(ChannelWrapper.class, Channel.class, 0);
   private static final Field activeField = LoginCheckpoint.handleField(InitialHandler.class, ChannelWrapper.class, 0);

   @Nullable
   public static Channel buildChannel(Object instance, PendingConnection target) {
      try {
         InitialHandler input = (InitialHandler)target;
         ChannelWrapper output = (ChannelWrapper)activeField.get(input);
         return (Channel)field.get(output);
      } catch (Exception context) {
         PasswordHashContainer.handleMessage("Unable to get the channel from " + instance.getClass().getCanonicalName() + " event", context);
         return null;
      }
   }
}
