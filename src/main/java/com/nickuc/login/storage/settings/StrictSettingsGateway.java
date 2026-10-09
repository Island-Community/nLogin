package com.nickuc.login.storage.settings;

import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.config.PasswordHashContainer;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import com.nickuc.login.listener.PacketAdapter;
import io.netty.channel.Channel;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;

public class StrictSettingsGateway implements PacketAdapter {
   private final PasswordStore passwordStore;

   public StrictSettingsGateway(PasswordStore target) {
      this.passwordStore = target;
   }

   @EventHandler
   public void executeAsyncPlayerConnectionConfigureEvent(Object target) {
      try {
         Class<?> eventType = Class.forName("io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent");
         if (!eventType.isInstance(target)) {
            return;
         }
         Object input = eventType.getMethod("getConnection").invoke(target);
         Channel output = (Channel) SpigotReflectionUtil.getChannelFromPaperConnection(input);
         if (output == null) {
            disconnect(input, SafeLoginService.resolveTextComponent(
               OpenLocaleBarrier.loadMessage(
                  "§4[nLogin]", "", "§cUnable to get the channel from " + target.getClass().getSimpleName() + " event", "", "§ePlease contact an administrator."
               )
            ));
         } else {
            PacketLink context = (PacketLink) output.attr(PacketLink.attributeKey).get();
            if (context == null) {
               Object profile = input.getClass().getMethod("getProfile").invoke(input);
               String profileName = (String) profile.getClass().getMethod("getName").invoke(profile);
               String response = "Unable to find connection cache for user "
                  + profileName
                  + " in "
                  + target.getClass().getSimpleName()
                  + " event (behavior changed by a plugin?)";
               PasswordHashContainer.performMessage(response);
               disconnect(input, SafeLoginService.resolveTextComponent(OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + response, "", "§ePlease contact an administrator.")));
            } else {
               CompletableFuture<Void> data = new CompletableFuture<>();
               if (this.passwordStore.resolveRootMessageHandler().fetchPasswordService().validateState(context.user, ignored -> data.complete(null))) {
                  try {
                     data.get(SpawnState.ACTIVE_PENDING_SPAWNSTATE.r() + 30, TimeUnit.SECONDS);
                  } catch (Exception result) {
                     if (result instanceof InterruptedException || result.getCause() instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                     } else {
                        PasswordHashContainer.handleMessage("Unexpected error while waiting for dialogs callback", result);
                        disconnect(input, Component.text("Unexpected error while waiting for dialogs callback", NamedTextColor.RED));
                     }
                  }
               }
            }
         }
      } catch (ClassNotFoundException ignored) {
      } catch (ReflectiveOperationException ex) {
         PasswordHashContainer.handleMessage("Unable to handle Paper configuration event", ex);
      }
   }

   private static void disconnect(Object connection, Component message) throws ReflectiveOperationException {
      Method disconnect = connection.getClass().getMethod("disconnect", Component.class);
      disconnect.invoke(connection, message);
   }
}
