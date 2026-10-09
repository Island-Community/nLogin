package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.auth.login.LoginCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.velocitypowered.api.proxy.InboundConnection;
import io.netty.channel.Channel;
import java.lang.reflect.Field;
import javax.annotation.Nullable;

public class IncomingVelocityGateway {
   private static final Class<?> value;
   private static final Field field;
   private static final Field activeField;
   private static final Field pendingField;
   private static final Field currentField;
   private static final Class<?> activeValue;

   @Nullable
   public static Channel resolveChannel(Object instance, InboundConnection target) {
      try {
         Object input;
         if (target.getClass().isAssignableFrom(activeValue)) {
            input = currentField.get(target);
         } else if (field != null) {
            target = (InboundConnection)field.get(target);
            input = pendingField.get(target);
         } else {
            input = pendingField.get(target);
         }

         return (Channel)activeField.get(input);
      } catch (Exception output) {
         PasswordHashContainer.handleMessage("Unable to get the channel from " + instance.getClass().getCanonicalName() + " event", output);
         return null;
      }
   }

   static {
      String instance = "com.velocitypowered.proxy.";
      String target = "Unable to find the class ";
      Class input;
      if ((input = ChildLoginCheckpoint.loadClass("com.velocitypowered.proxy.connection.client.InitialInboundConnection")) == null) {
         throw new IllegalArgumentException("Unable to find the class connection.client.InitialInboundConnection");
      }

      if ((value = ChildLoginCheckpoint.loadClass("com.velocitypowered.proxy.connection.MinecraftConnection")) == null) {
         throw new IllegalArgumentException("Unable to find the class connection.MinecraftConnection");
      }

      if ((activeValue = ChildLoginCheckpoint.loadClass("com.velocitypowered.proxy.connection.client.ConnectedPlayer")) == null) {
         throw new IllegalArgumentException("Unable to find the class connection.client.ConnectedPlayer");
      }

      if ((pendingField = LoginCheckpoint.handleField(input, value, 0)) == null) {
         throw new IllegalArgumentException("Unable to find the field of " + value + " in " + input);
      }

      if ((currentField = LoginCheckpoint.handleField(activeValue, value, 0)) == null) {
         throw new IllegalArgumentException("Unable to find the field of " + value + " in " + activeValue);
      }

      Class output = ChildLoginCheckpoint.loadClass("com.velocitypowered.proxy.connection.client.LoginInboundConnection");
      if (output != null) {
         field = LoginCheckpoint.loadField(output, "delegate");
         if (field == null) {
            throw new NullPointerException("initial inbound connection delegate cannot be null");
         }
      } else {
         field = null;
      }

      activeField = LoginCheckpoint.handleField(value, Channel.class, 0);
   }
}
