package com.nickuc.login.command.completion;

import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.platform.sender.StrictSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.velocity.VelocityPlatform;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.password.PasswordStore;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.command.CommandExecuteEvent.CommandResult;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerSettingsChangedEvent;
import com.velocitypowered.api.event.player.TabCompleteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent.ChatResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.PlayerSettings;
import java.util.List;
import java.util.Locale;


public class VersionCompletionCommand implements StrictSenderAdapter {
   private final PasswordStore passwordStore;
   private final VelocityPlatform VelocityPlatform;

   @Subscribe(order = PostOrder.LAST)
   public void executeCommandExecuteEvent(CommandExecuteEvent target) {
      if (!target.getResult().isAllowed() && this.isState(target.getCommand())) {
         target.setResult(CommandResult.allowed());
      }
   }

   @Subscribe
   public void saveTabCompleteEvent(TabCompleteEvent target) {
      List input = target.getSuggestions();
      if (!input.isEmpty()) {
         String output = target.getPartialMessage().trim();
         if (output.isEmpty() || output.charAt(0) == '/') {
            Player context = target.getPlayer();

            try {
               VerifiedServerAdapter data = this.VelocityPlatform.b().processVerifiedServerAdapter(context);
               if (data.findState()) {
                  return;
               }

               int value = !data.i("nlogin.command.nlogin") && !data.i("nlogin.admin") ? 0 : 1;
               input.removeIf(
                  outputValue -> {
                     if (outputValue.trim().isEmpty()) {
                        return false;
                     } else if (outputValue.charAt(0) != '/') {
                        return false;
                     } else {
                        String[] contextValue = outputValue.split(" ");
                        String dataValue = contextValue[0].toLowerCase(Locale.ENGLISH);
                        if (!value && "/nlogin".equals(dataValue)) {
                           return true;
                        } else {
                           return this.passwordStore.loadLimboRegistry().canState(data)
                              ? false
                              : this.passwordStore.findIndirectPasswordHashVerifier().validateState(outputValue);
                        }
                     }
                  }
               );
            } catch (Throwable result) {
               PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + context.getUsername() + ")", result);
               context.disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
            }
         }
      }
   }

   private boolean isState(String target) {
      if (!target.trim().isEmpty()) {
         String[] input = target.split(" ");
         if (input.length > 1) {
            String output = input[0].toLowerCase(Locale.ENGLISH);
            return output.equals("nlogin");
         }
      }

      return false;
   }

   @Subscribe
   public void performSet(PlayerSettingsChangedEvent target) {
      Player input = target.getPlayer();

      try {
         VerifiedServerAdapter output = this.VelocityPlatform.b().processVerifiedServerAdapter(input);
         if (output.findState()) {
            return;
         }

         PlayerSettings context = target.getPlayerSettings();
         LenientPremiumOption data = LenientPremiumOption.buildLenientPremiumOption(context.getLocale().toLanguageTag());
         if (data != null) {
            LimboCoordinator value = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
            value.updateLenientMessageKind(LenientMessageKind.CACHED_LENIENTMESSAGEKIND, data);
         }
      } catch (Throwable result) {
         PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getUsername() + ")", result);
         input.disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
      }
   }

   @Subscribe(order = PostOrder.FIRST)
   public void handlePlayerChatEvent(PlayerChatEvent target) {
      String input = target.getMessage().trim();
      if (!input.isEmpty()) {
         Player output = target.getPlayer();

         try {
            VerifiedServerAdapter context = this.VelocityPlatform.b().processVerifiedServerAdapter(output);
            if (context.findState()) {
               return;
            }

            if (this.passwordStore.getFloodgateResolver().verifyState(context, input)) {
               LimboCoordinator data = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(context);
               if (output.getProtocolVersion().getProtocol() >= 759
                  && (data.isState(LenientMessageKind.ACTIVE_REMOTE_LENIENTMESSAGEKIND) || data.d(LenientMessageKind.PENDING_LENIENTMESSAGEKIND))) {
                  return;
               }

               target.setResult(ChatResult.denied());
            }
         } catch (Throwable value) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + output.getUsername() + ")", value);
            output.disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
         }
      }
   }

   public VersionCompletionCommand(VelocityPlatform target, PasswordStore input) {
      this.VelocityPlatform = target;
      this.passwordStore = input;
   }

   @Subscribe
   public void processCommandExecuteEvent(CommandExecuteEvent target) {
      if (target.getResult().isAllowed()) {
         CommandSource input = target.getCommandSource();
         if (input instanceof Player) {
            String output = target.getCommand().trim();
            if (!output.isEmpty()) {
               Player context = (Player)input;

               try {
                  VerifiedServerAdapter data = this.VelocityPlatform.b().processVerifiedServerAdapter(context);
                  if (data.findState()) {
                     return;
                  }

                  String value = this.passwordStore.getFloodgateResolver().handleMessage(data, "/" + output);
                  if (value == null) {
                     LimboCoordinator result = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(data);
                     if (context.getProtocolVersion().getProtocol() >= 759
                        && (result.isState(LenientMessageKind.ACTIVE_REMOTE_LENIENTMESSAGEKIND) || result.d(LenientMessageKind.PENDING_LENIENTMESSAGEKIND))) {
                        target.setResult(CommandResult.forwardToServer());
                        return;
                     }

                     target.setResult(CommandResult.denied());
                  }
               } catch (Throwable request) {
                  PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + context.getUsername() + ")", request);
                  context.disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
               }
            }
         }
      }
   }
}
