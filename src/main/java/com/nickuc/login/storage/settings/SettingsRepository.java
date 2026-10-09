package com.nickuc.login.storage.settings;

import com.nickuc.login.config.PasswordHashContainer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import com.nickuc.login.protocol.PacketBridge;
import com.nickuc.login.protocol.SharedPacketInterceptor;
import io.netty.channel.Channel;
import java.util.Arrays;

import org.bukkit.entity.Player;

public class SettingsRepository {
   public final SharedPacketInterceptor sharedPacketInterceptor = new SharedPacketInterceptor(this);
   private final PasswordStore passwordStore;
   private static final WrapperPlayServerWindowItems wrapperPlayServerWindowItems;
   public final PacketBridge packetBridge = new PacketBridge(this);

   private boolean getState() {
      return !SpawnState.TOP_SPAWNSTATE.ar();
   }

   public void updateChannel(Channel target) {
      try {
         PacketEvents.getAPI().getProtocolManager().sendPacketSilently(target, wrapperPlayServerWindowItems);
      } catch (Exception output) {
         PasswordHashContainer.handleMessage("Unexpected error while sending blank inventory", output);
      }
   }

   private void handlePacketSendEvent(PacketSendEvent target, int input) {
      if (input == 0) {
         Player output = (Player)target.getPlayer();
         if (output == null || !this.passwordStore.loadLimboRegistry().canState(this.passwordStore.b().processVerifiedServerAdapter(output))) {
            target.setCancelled(true);
         }
      }
   }

   static {
      ItemStack[] instance = new ItemStack[45];
      Arrays.fill(instance, ItemStack.EMPTY);
      wrapperPlayServerWindowItems = new WrapperPlayServerWindowItems(0, 0, Arrays.asList(instance), null);
   }

   public SettingsRepository(PasswordStore target) {
      this.passwordStore = target;
   }
}
