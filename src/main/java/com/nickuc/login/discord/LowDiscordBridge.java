package com.nickuc.login.discord;

import com.nickuc.login.config.PasswordHashContainer;
import java.lang.reflect.Method;
import java.util.UUID;
import javax.annotation.Nullable;

public class LowDiscordBridge {
   @Nullable
   public String loadMessage(UUID target) {
      try {
         Class<?> pluginType = Class.forName("github.scarsz.discordsrv.DiscordSRV");
         Object plugin = pluginType.getMethod("getPlugin").invoke(null);
         Object linkManager = plugin.getClass().getMethod("getAccountLinkManager").invoke(plugin);
         Method lookup = linkManager.getClass().getMethod("getDiscordId", UUID.class);
         return (String) lookup.invoke(linkManager, target);
      } catch (Throwable output) {
         PasswordHashContainer.handleMessage("Unable to fetch Discord ID using DiscordSRV API.", output);
         return null;
      }
   }
}
