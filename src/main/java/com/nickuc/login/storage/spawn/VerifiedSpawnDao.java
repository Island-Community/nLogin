package com.nickuc.login.storage.spawn;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.TightPlatformCatalog;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import com.nickuc.login.listener.PacketAdapter;
import com.nickuc.login.session.BusyLimboStore;
import com.nickuc.login.session.LimboCoordinator;
import io.netty.channel.Channel;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class VerifiedSpawnDao implements PacketAdapter {
   private final PasswordStore passwordStore;
   private final BusyLimboStore busyLimboStore;

   public VerifiedSpawnDao(PasswordStore target, BusyLimboStore input) {
      this.passwordStore = target;
      this.busyLimboStore = input;
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void executeAsyncPlayerSpawnLocationEvent(Object target) {
      try {
         Class<?> eventType = Class.forName("io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent");
         if (!eventType.isInstance(target)) {
            return;
         }
         Object input = eventType.getMethod("getConnection").invoke(target);
         PlayerProfile output = (PlayerProfile) input.getClass().getMethod("getProfile").invoke(input);
         boolean newPlayer = (Boolean) eventType.getMethod("isNewPlayer").invoke(target);
         if (!newPlayer) {
            InetSocketAddress clientAddress = (InetSocketAddress) input.getClass().getMethod("getClientAddress").invoke(input);
            LimboCoordinator context = this.passwordStore
               .loadLimboRegistry()
               .resolveLimboCoordinator(output.getName(), output.getId(), clientAddress.getAddress());
            if (context != null && context.loadTightPlatformCatalog().isState(TightPlatformCatalog.LOCAL_TIGHTPLATFORMCATALOG)) {
               return;
            }
         }

         Channel request = (Channel) SpigotReflectionUtil.getChannelFromPaperConnection(input);
         if (request == null) {
            disconnect(input, SafeLoginService.resolveTextComponent(
               OpenLocaleBarrier.loadMessage(
                  "§4[nLogin]", "", "§cUnable to get the channel from " + target.getClass().getSimpleName() + " event", "", "§ePlease contact an administrator."
               )
            ));
         } else {
            PacketLink data = (PacketLink) request.attr(PacketLink.attributeKey).get();
            if (data == null) {
               String response = "Unable to find connection cache for user "
                  + output.getName()
                  + " in "
                  + target.getClass().getSimpleName()
                  + " event (behavior changed by a plugin?)";
               PasswordHashContainer.performMessage(response);
               disconnect(input, SafeLoginService.resolveTextComponent(OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + response, "", "§ePlease contact an administrator.")));
            } else {
               Location value = (Location) eventType.getMethod("getSpawnLocation").invoke(target);
               Location result = this.busyLimboStore.loadSpawnSession().buildLocation(value, false);
               if (result != null) {
                  data.location = value;
                  eventType.getMethod("setSpawnLocation", Location.class).invoke(target, result);
               }
            }
         }
      } catch (ClassNotFoundException ignored) {
      } catch (ReflectiveOperationException ex) {
         PasswordHashContainer.handleMessage("Unable to handle Paper spawn event", ex);
      }
   }

   private static void disconnect(Object connection, Component message) throws ReflectiveOperationException {
      Method disconnect = connection.getClass().getMethod("disconnect", Component.class);
      disconnect.invoke(connection, message);
   }
}
