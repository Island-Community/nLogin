package com.nickuc.login.listener.bukkit;

import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LenientPremiumOption;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.wrapper.common.client.WrapperCommonClientSettings;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.protocol.LenientCommandHandler;
import com.nickuc.login.session.LimboCoordinator;


public class LocaleListener implements LenientCommandHandler {
   private final BukkitPlatform BukkitPlatform;

   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      Object input = target.getPlayer();
      if (input != null) {
         VerifiedServerAdapter output = this.BukkitPlatform.b().processVerifiedServerAdapter(input);
         LimboCoordinator context = this.BukkitPlatform.fetchPasswordStore().loadLimboRegistry().buildLimboCoordinator(output);
         if (context != null) {
            WrapperCommonClientSettings data = new WrapperCommonClientSettings(target);
            LenientPremiumOption value = LenientPremiumOption.buildLenientPremiumOption(data.getLocale());
            if (value != null) {
               context.updateLenientMessageKind(LenientMessageKind.CACHED_LENIENTMESSAGEKIND, value);
            }
         }
      }
   }

   public LocaleListener(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }
}
