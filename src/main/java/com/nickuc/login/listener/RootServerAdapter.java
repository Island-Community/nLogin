package com.nickuc.login.listener;

import com.nickuc.login.config.PasswordHashDefinition;
import com.nickuc.login.loader.platform.BungeeLoader;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import net.md_5.bungee.api.plugin.Listener;

public interface RootServerAdapter extends OutgoingAccountHandler, Listener {
   @Override
   default void handleObject(Object target) {
      PasswordHashDefinition input = (PasswordHashDefinition)target;
      BungeeLoader output = input.retrieveBungeeLoader();
      output.getProxy().getPluginManager().registerListener(output, this);
   }
}
