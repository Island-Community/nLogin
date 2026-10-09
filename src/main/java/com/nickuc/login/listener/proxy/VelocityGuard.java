package com.nickuc.login.listener.proxy;

import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.sender.StrictSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.velocity.VelocityPlatform;
import com.nickuc.login.storage.login.LoginCollection;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent.ForwardResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.ChannelMessageSource;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;


public class VelocityGuard implements StrictSenderAdapter {
   public static final ChannelIdentifier channelIdentifier = MinecraftChannelIdentifier.create("nlogin", "addon");
   private final VelocityPlatform VelocityPlatform;
   public static final ChannelIdentifier activeChannelIdentifier = new LegacyChannelIdentifier("nlogin:addon");

   @Subscribe
   public void executePluginMessageEvent(PluginMessageEvent target) {
      if (target.getResult().isAllowed()) {
         ChannelMessageSource input = target.getSource();

         try {
            ChannelIdentifier output = target.getIdentifier();
            if (output.equals(this.VelocityPlatform.loadChannelIdentifier()) || output.equals(this.VelocityPlatform.findChannelIdentifier())) {
               target.setResult(ForwardResult.handled());
               if (!(input instanceof ServerConnection)) {
                  if (input instanceof Player) {
                     Player response = (Player)input;
                     response.disconnect(SafeLoginService.resolveTextComponent("§cAttempted to send a message on a protected nLogin channel."));
                     PasswordHashContainer.performMessage(response.getUsername() + " attempted to send a message on a protected nLogin channel.");
                  }

                  return;
               }

               VerifiedServerAdapter request = this.VelocityPlatform.b().processVerifiedServerAdapter(target.getTarget());
               String data = ((ServerConnection)input).getServer().getServerInfo().getName();
               byte[] value = target.getData();
               this.VelocityPlatform.getPasswordHashAdapter().sendVerifiedServerAdapter(request, data, value);
            } else if (output.equals(channelIdentifier) || output.equals(activeChannelIdentifier)) {
               LoginCollection context = this.VelocityPlatform.fetchPasswordStore().loadInternalAccountHandler().getLoginCollection();
               if (context == null) {
                  return;
               }

               if (!(input instanceof Player)) {
                  return;
               }

               if (context.fetchSettingsProcessor().canState(this.VelocityPlatform.b().processVerifiedServerAdapter(input), target.getData())) {
                  target.setResult(ForwardResult.handled());
               }
            }
         } catch (Throwable result) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input + ")", result);
            target.setResult(ForwardResult.handled());
            if (input instanceof Player) {
               ((Player)input).disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
            }
         }
      }
   }

   public VelocityGuard(VelocityPlatform target) {
      this.VelocityPlatform = target;
   }
}
