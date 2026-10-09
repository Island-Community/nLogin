package com.nickuc.login.listener.proxy;

import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.platform.sender.StrictSenderAdapter;
import com.nickuc.login.protocol.ChannelBridge;
import com.nickuc.login.proxy.DiscordForwarder;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

public class VelocityListener extends ChannelBridge<DiscordForwarder> {
   @Override
   public void processObject(Object target) {
      this.dispatchObject(target, null);
   }

   public VelocityListener(DiscordForwarder target) {
      super(target);
   }

   @Override
   public void dispatchObject(Object target, Object input) {
      if (input != null && !(input instanceof StrictSenderAdapter)) {
         throw new IllegalArgumentException("Invalid listener! " + input.getClass().getCanonicalName());
      }

      Object output;
      if (target instanceof String) {
         String context = (String)target;
         String[] data = context.split(":");
         output = data.length == 2 ? MinecraftChannelIdentifier.create(data[0], data[1]) : new LegacyChannelIdentifier(context);
      } else {
         if (!(target instanceof ChannelIdentifier)) {
            throw new IllegalArgumentException("Invalid argument for channel! " + target + " " + target.getClass().getCanonicalName());
         }

         output = (ChannelIdentifier)target;
      }

      this.indirectSessionHandler.findProxyServer().getChannelRegistrar().register(new ChannelIdentifier[]{(ChannelIdentifier)output});
      if (input != null) {
         this.indirectSessionHandler.a((OutgoingAccountHandler)input, new OutgoingAccountHandler[0]);
      }
   }

   @Override
   public void updateObject(Object target) {
      Object input;
      if (target instanceof String) {
         String output = (String)target;
         String[] context = output.split(":");
         input = context.length == 2 ? MinecraftChannelIdentifier.create(context[0], context[1]) : new LegacyChannelIdentifier(output);
      } else {
         if (!(target instanceof ChannelIdentifier)) {
            throw new IllegalArgumentException("Invalid argument for channel! " + target + " " + target.getClass().getCanonicalName());
         }

         input = (ChannelIdentifier)target;
      }

      this.indirectSessionHandler.findProxyServer().getChannelRegistrar().unregister(new ChannelIdentifier[]{(ChannelIdentifier)input});
   }
}
