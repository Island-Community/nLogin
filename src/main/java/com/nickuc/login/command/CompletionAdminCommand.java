package com.nickuc.login.command;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.account.StrictPremiumOption;
import com.nickuc.login.auth.login.LocalLoginProcessor;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.auth.twofactor.PasswordChallenge;
import com.nickuc.login.discord.DiscordHandler;
import com.nickuc.login.discord.QuickDiscordHandler;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.SecondaryMessageKind;
import com.nickuc.login.platform.listener.RootListenerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import java.util.function.Consumer;

public class CompletionAdminCommand extends PasswordChallenge {
   public CompletionAdminCommand(InternalLoginOption target) {
      super(target, StrictMessageKind.STRICT_MESSAGE_KIND);
   }

   @Override
   public void updateVerifiedServerAdapter(
      VerifiedServerAdapter target, SpawnLookup input, LimboCoordinator output, RootListenerContract context, String data, String[] value
   ) {
      QuickDiscordHandler result = input.fetchQuickDiscordHandler();
      if (result.getMessage() != null) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_MAIN_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
      } else {
         String request = StrictPremiumOption.ACTIVE_STRICTPREMIUMOPTION.a(new Object[0]);
         String response = (String)output.d(LenientMessageKind.ACTIVE_PRIVATE_LENIENTMESSAGEKIND);
         if (response == null) {
            response = LocalLoginProcessor.processObject(
               DiscordHandler.cache.asMap(), () -> SecureLoginHandler.loadMessage(SecondaryMessageKind.CURRENT_SECONDARYMESSAGEKIND, 6)
            );
            output.updateLenientMessageKind(LenientMessageKind.ACTIVE_PRIVATE_LENIENTMESSAGEKIND, response);
            DiscordHandler.cache.put(response, target.getName());
         }

         this.executeVerifiedServerAdapter(target, output, request, response);
      }
   }

   private void executeVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, String output, String context) {
      String data = "/link code:" + context;
      SecondaryAccountHandler value = input.getSecondaryAccountHandler();
      Consumer result = inputValue -> {
         String outputValue = inputValue.trim();
         if (outputValue.length() > 3 && outputValue.charAt(0) == '[' && outputValue.charAt(outputValue.length() - 1) == ']') {
            int contextValue = 0;

            for (char request : inputValue.toCharArray()) {
               if (request != ' ') {
                  break;
               }

               contextValue++;
            }

            String response = OpenLocaleBarrier.buildMessage(" ", contextValue) + outputValue.substring(1, outputValue.length() - 1).replace("@command", data);
            value.saveMessage(response, data);
         } else {
            value.executeMessage(inputValue.replace("@command", data));
         }
      };
      CachedSettingsGateway.handleOutgoingSenderAdapter(
         target,
         LoudProxyState.PENDING_ROOT_LOUDPROXYSTATE,
         result,
         output,
         this.indirectSessionHandler.resolveDiscordVerifier().retrieveFastDiscordNotifier().fetchJDA().getSelfUser().getAsTag()
      );
   }

   @Override
   public void executeVerifiedServerAdapter(VerifiedServerAdapter target, SpawnLookup input, LimboCoordinator output, String context, String[] data) {
      this.performVerifiedServerAdapter(target, output, input, context);
   }

   @Override
   public void updateVerifiedServerAdapter(VerifiedServerAdapter target, SpawnLookup input, RootListenerContract output, String context, String[] data) {
      if (this.strictMessageKind.computeMessage(input) == null) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_PRIMARY_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
      } else {
         output.dispatchSpawnLookup(input, target);
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_SECONDARY_LOUDPROXYSTATE);
      }
   }
}
