package com.nickuc.login.listener.bukkit;

import com.nickuc.login.loader.platform.BukkitLoader;
import com.nickuc.login.protocol.ChannelBridge;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class MessageListener extends ChannelBridge<DiscordGuard> {
   @Override
   public void dispatchObject(Object target, Object input) {
      if (input == null) {
         throw new IllegalArgumentException("Listener cannot be null!");
      }

      if (!(input instanceof PluginMessageListener)) {
         throw new IllegalArgumentException("Invalid listener! " + input.getClass().getCanonicalName());
      }

      if (!(target instanceof String)) {
         throw new IllegalArgumentException("Channel must be a string!");
      }

      String output = (String)target;
      BukkitLoader context = this.indirectSessionHandler.retrieveBukkitLoader();
      Messenger data = context.getServer().getMessenger();
      if (!data.isOutgoingChannelRegistered(context, output)) {
         data.registerOutgoingPluginChannel(context, output);
      }

      if (!data.isIncomingChannelRegistered(context, output)) {
         data.registerIncomingPluginChannel(context, output, (PluginMessageListener)input);
      }
   }

   @Override
   public void updateObject(Object target) {
      if (!(target instanceof String)) {
         throw new IllegalArgumentException("Channel must be a string!");
      }

      String input = (String)target;
      BukkitLoader output = this.indirectSessionHandler.retrieveBukkitLoader();
      Messenger context = output.getServer().getMessenger();
      context.unregisterIncomingPluginChannel(output, input);
      context.unregisterOutgoingPluginChannel(output, input);
   }

   @Override
   public void processObject(Object target) {
      throw new UnsupportedOperationException();
   }

   public MessageListener(DiscordGuard target) {
      super(target);
   }
}
