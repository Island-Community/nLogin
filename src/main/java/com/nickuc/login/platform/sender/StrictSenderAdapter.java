package com.nickuc.login.platform.sender;

import com.nickuc.login.loader.platform.VelocityLoader;
import com.nickuc.login.proxy.DiscordForwarder;

public interface StrictSenderAdapter extends OutgoingAccountHandler {
   @Override
   default void handleObject(Object target) {
      DiscordForwarder input = (DiscordForwarder)target;
      VelocityLoader output = input.fetchVelocityLoader();
      output.getServer().getEventManager().register(output, this);
   }
}
