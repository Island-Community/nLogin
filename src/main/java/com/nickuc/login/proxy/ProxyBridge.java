package com.nickuc.login.proxy;

import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.PremiumState;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.nickuc.login.protocol.AccountAdapter;
import com.nickuc.login.protocol.LenientCommandHandler;
import com.nickuc.login.protocol.QuickCommandHandler;
import com.nickuc.login.protocol.SettingsInterceptor;
import com.nickuc.login.storage.password.PasswordGateway;
import com.nickuc.login.storage.password.PasswordStore;
import io.netty.channel.Channel;
import java.net.InetAddress;


public class ProxyBridge implements LenientCommandHandler, QuickCommandHandler {
   private final PasswordStore passwordStore;

   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      User input = target.getUser();
      Channel output = (Channel)target.getChannel();
      AccountAdapter context = (AccountAdapter)output.attr(AccountAdapter.attributeKey).get();
      if (context == null) {
         String request = "Unable to find connection cache for user " + input.getName() + " in ProxyEncryptionAdapter (behavior changed by a plugin?)";
         PasswordHashContainer.performMessage(request);
         SettingsInterceptor.handleUser(input, OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + request, "", "§ePlease contact an administrator."));
      } else {
         String data = context.loadMessage();
         InetAddress value = input.getAddress().getAddress();
         PremiumState result = PasswordGateway.loadPremiumState(data, value);
         switch (result) {
            case CURRENT_PREMIUMSTATE:
            case PRIMARY_PREMIUMSTATE:
               PasswordGateway.processPasswordStore(this.passwordStore, data, value, PremiumState.PREMIUM_STATE);
         }
      }
   }

   public ProxyBridge(PasswordStore target) {
      this.passwordStore = target;
   }

   @Override
   public void savePacketSendEvent(PacketSendEvent target) {
      User input = target.getUser();
      Channel output = (Channel)target.getChannel();
      AccountAdapter context = (AccountAdapter)output.attr(AccountAdapter.attributeKey).get();
      if (context == null) {
         String request = "Unable to find connection cache for user " + input.getName() + " in ProxyEncryptionAdapter (behavior changed by a plugin?)";
         PasswordHashContainer.performMessage(request);
         SettingsInterceptor.handleUser(input, OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + request, "", "§ePlease contact an administrator."));
      } else {
         String data = context.loadMessage();
         InetAddress value = input.getAddress().getAddress();
         PremiumState result = PasswordGateway.loadPremiumState(data, value);
         PasswordGateway.processPasswordStore(
            this.passwordStore, data, value, result == PremiumState.ACTIVE_PREMIUMSTATE ? PremiumState.CURRENT_PREMIUMSTATE : PremiumState.PRIMARY_PREMIUMSTATE
         );
      }
   }
}
