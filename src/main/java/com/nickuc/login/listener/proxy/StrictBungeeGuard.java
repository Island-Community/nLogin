package com.nickuc.login.listener.proxy;

import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.listener.RootServerAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.bungee.BungeePlatform;
import com.nickuc.login.storage.login.LoginCollection;

import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

public class StrictBungeeGuard implements RootServerAdapter {
   private final BungeePlatform BungeePlatform;

   public StrictBungeeGuard(BungeePlatform target) {
      this.BungeePlatform = target;
   }

   @EventHandler
   public void handlePluginMessageEvent(PluginMessageEvent target) {
      if (!target.isCancelled()) {
         try {
            String input = target.getTag();
            switch (input) {
               case "nlogin:main":
                  target.setCancelled(true);
                  Connection source = target.getSender();
                  if (!(source instanceof ServerConnection)) {
                     if (source instanceof ProxiedPlayer) {
                        ProxiedPlayer record = (ProxiedPlayer)source;
                        record.disconnect(TextComponent.fromLegacyText("§cAttempted to send a message on a protected nLogin channel."));
                        PasswordHashContainer.performMessage(record.getName() + " attempted to send a message on a protected nLogin channel.");
                     }

                     return;
                  }

                  VerifiedServerAdapter entry = this.BungeePlatform.b().processVerifiedServerAdapter(target.getReceiver());
                  String result = ((ServerConnection)source).getInfo().getName();
                  byte[] request = target.getData();
                  this.BungeePlatform.getPasswordHashAdapter().sendVerifiedServerAdapter(entry, result, request);
                  break;
               case "nlogin:addon":
                  LoginCollection data = this.BungeePlatform.fetchPasswordStore().loadInternalAccountHandler().getLoginCollection();
                  if (data == null) {
                     return;
                  }

                  Connection value = target.getSender();
                  if (!(value instanceof ProxiedPlayer)) {
                     return;
                  }

                  if (data.fetchSettingsProcessor().canState(this.BungeePlatform.b().processVerifiedServerAdapter(value), target.getData())) {
                     target.setCancelled(true);
                  }
            }
         } catch (Throwable response) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + target.getSender() + ")", response);
            target.setCancelled(true);
            target.getSender().disconnect(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
         }
      }
   }
}
