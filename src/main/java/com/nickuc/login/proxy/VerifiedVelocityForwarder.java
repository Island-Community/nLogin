package com.nickuc.login.proxy;

import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.auth.login.LoginCheckpoint;
import com.velocitypowered.api.proxy.InboundConnection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.kyori.adventure.text.Component;

public class VerifiedVelocityForwarder {
   private static final Class<?> value;
   private static final Method method;
   private static final Method activeMethod;
   private static final Method pendingMethod;
   private static final Class<?> activeValue;
   private static final Class<?> pendingValue;

   static {
      String instance = "com.velocitypowered.proxy.";
      String target = "Unable to find the class ";
      if ((pendingValue = ChildLoginCheckpoint.loadClass("com.velocitypowered.proxy.connection.client.LoginInboundConnection")) == null) {
         throw new IllegalArgumentException("Unable to find the class connection.client.LoginInboundConnection");
      }

      if ((activeValue = ChildLoginCheckpoint.loadClass("com.velocitypowered.proxy.connection.client.InitialInboundConnection")) == null) {
         throw new IllegalArgumentException("Unable to find the class connection.client.InitialInboundConnection");
      }

      if ((value = ChildLoginCheckpoint.loadClass("com.velocitypowered.proxy.connection.client.ConnectedPlayer")) == null) {
         throw new IllegalArgumentException("Unable to find the class connection.client.ConnectedPlayer");
      }

      if ((activeMethod = LoginCheckpoint.handleMethod(pendingValue, "disconnect", Component.class)) == null) {
         throw new IllegalArgumentException("Unable to find the disconnect(Component) method in " + pendingValue);
      }

      if ((method = LoginCheckpoint.handleMethod(activeValue, "disconnect", Component.class)) == null) {
         throw new IllegalArgumentException("Unable to find the disconnect(Component) method in " + activeValue);
      }

      if ((pendingMethod = LoginCheckpoint.handleMethod(value, "disconnect", Component.class)) == null) {
         throw new IllegalArgumentException("Unable to find the disconnect(Component) method in " + value);
      }
   }

   public static void updateInboundConnection(InboundConnection instance, Component target) {
      Method input;
      if (instance.getClass().isAssignableFrom(pendingValue)) {
         input = activeMethod;
      } else if (instance.getClass().isAssignableFrom(activeValue)) {
         input = method;
      } else {
         if (!instance.getClass().isAssignableFrom(value)) {
            throw new IllegalArgumentException(
               "The provided connection is not a LoginInboundConnection or InitialInboundConnection! " + instance.getClass().getCanonicalName()
            );
         }

         input = pendingMethod;
      }

      try {
         input.invoke(instance, target);
      } catch (IllegalAccessException | InvocationTargetException context) {
         throw new RuntimeException("Unable to disconnect " + instance, context);
      }
   }
}
