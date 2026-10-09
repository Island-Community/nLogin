package com.nickuc.login.command.completion;

import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.listener.RootServerAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.bungee.BungeePlatform;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.List;
import java.util.Locale;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.ClientSettings;

public class LocalLinkSpawnCommand implements RootServerAdapter {
   private final BungeePlatform BungeePlatform;
   private final PasswordStore passwordStore;

   private boolean isState(String target) {
      if (!target.isEmpty() && target.charAt(0) == '/') {
         String[] input = target.split(" ");
         if (input.length > 1) {
            String output = input[0].toLowerCase(Locale.ENGLISH);
            return output.equals("/nlogin");
         }
      }

      return false;
   }

   @EventHandler
   public void saveSet(SettingsChangedEvent target) {
      ProxiedPlayer input = target.getPlayer();
      if (input instanceof UserConnection) {
         try {
            VerifiedServerAdapter output = this.BungeePlatform.b().processVerifiedServerAdapter(input);
            if (output.findState()) {
               return;
            }

            ClientSettings context = ((UserConnection)input).getSettings();
            LenientPremiumOption data = LenientPremiumOption.buildLenientPremiumOption(context.getLocale());
            if (data != null) {
               LimboCoordinator value = this.passwordStore.loadLimboRegistry().buildLimboCoordinator(output);
               if (value != null) {
                  value.updateLenientMessageKind(LenientMessageKind.CACHED_LENIENTMESSAGEKIND, data);
               }
            }
         } catch (Throwable result) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getName() + ")", result);
            input.disconnect(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
         }
      }
   }

   @EventHandler(priority = -64)
   public void handleChatEvent(ChatEvent target) {
      if (!target.isCancelled()) {
         Connection input = target.getSender();
         if (input instanceof ProxiedPlayer) {
            String output = target.getMessage().trim();
            if (!output.isEmpty()) {
               ProxiedPlayer context = (ProxiedPlayer)input;

               try {
                  VerifiedServerAdapter data = this.BungeePlatform.b().processVerifiedServerAdapter(context);
                  if (data.findState()) {
                     return;
                  }

                  if (output.charAt(0) == '/') {
                     String value = this.passwordStore.getFloodgateResolver().handleMessage(data, output);
                     if (value == null) {
                        target.setCancelled(true);
                        return;
                     }

                     if (!output.equals(value)) {
                        target.setMessage(value);
                     }
                  } else if (this.passwordStore.getFloodgateResolver().verifyState(this.BungeePlatform.b().processVerifiedServerAdapter(context), output)) {
                     target.setCancelled(true);
                  }
               } catch (Throwable result) {
                  PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + context.getName() + ")", result);
                  target.setCancelled(true);
                  context.disconnect(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
               }
            }
         }
      }
   }

   @EventHandler(priority = 127)
   public void sendChatEvent(ChatEvent target) {
      if (target.isCancelled() && this.isState(target.getMessage())) {
         target.setCancelled(false);
      }
   }

   @EventHandler
   public void sendServerKickEvent(ServerKickEvent target) {
      if (!target.isCancelled()) {
         String input = BaseComponent.toLegacyText(target.getKickReasonComponent());
         if (input.equalsIgnoreCase("You logged in from another location")) {
            target.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = -64)
   public void updateTabCompleteEvent(TabCompleteEvent target) {
      if (!target.isCancelled()) {
         Connection input = target.getSender();
         if (input instanceof ProxiedPlayer) {
            ProxiedPlayer output = (ProxiedPlayer)input;

            try {
               List context = target.getSuggestions();
               if (context.isEmpty()) {
                  return;
               }

               String data = target.getCursor().trim();
               if (!data.isEmpty() && data.charAt(0) != '/') {
                  return;
               }

               VerifiedServerAdapter value = this.BungeePlatform.b().processVerifiedServerAdapter(output);
               if (value.findState()) {
                  return;
               }

               int result = !value.i("nlogin.command.nlogin") && !value.i("nlogin.admin") ? 0 : 1;
               context.removeIf(
                  outputValue -> {
                     if (outputValue.trim().isEmpty()) {
                        return false;
                     } else if (outputValue.charAt(0) != '/') {
                        return false;
                     } else {
                        String[] contextValue = outputValue.split(" ");
                        String dataValue = contextValue[0].toLowerCase(Locale.ENGLISH);
                        if (!result && dataValue.equals("/nlogin")) {
                           return true;
                        } else {
                           return this.passwordStore.loadLimboRegistry().canState(value)
                              ? false
                              : this.passwordStore.findIndirectPasswordHashVerifier().validateState(outputValue);
                        }
                     }
                  }
               );
            } catch (Throwable request) {
               PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + output.getName() + ")", request);
               target.setCancelled(true);
               output.disconnect(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
            }
         }
      }
   }

   public LocalLinkSpawnCommand(BungeePlatform target, PasswordStore input) {
      this.BungeePlatform = target;
      this.passwordStore = input;
   }
}
