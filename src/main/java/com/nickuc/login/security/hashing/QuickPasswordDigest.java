package com.nickuc.login.security.hashing;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.account.StrictPremiumOption;
import com.nickuc.login.auth.twofactor.PasswordChallenge;
import com.nickuc.login.model.SecondaryPlatformCatalog;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.listener.RootListenerContract;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class QuickPasswordDigest extends PasswordChallenge {
   private static final List<String> entries = Arrays.asList(
      "@gmail.com", "@hotmail.com", "@outlook.com", "@live.com", "@yahoo.com", "@mail.ru", "@interia.pl", "@yandex.com"
   );

   public QuickPasswordDigest(InternalLoginOption target) {
      super(target, StrictMessageKind.ACTIVE_STRICTMESSAGEKIND);
   }

   @Override
   public void updateVerifiedServerAdapter(VerifiedServerAdapter target, SpawnLookup input, RootListenerContract output, String context, String[] data) {
      if (data.length != 2) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target,
            LoudProxyState.LINKED_LOUDPROXYSTATE,
            "/" + context.toLowerCase(Locale.ENGLISH) + " " + data[0].toLowerCase(Locale.ENGLISH) + " <" + this.strictMessageKind.findMessage() + ">"
         );
      } else {
         String value = this.strictMessageKind.computeMessage(input);
         if (value == null) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_PRIMARY_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
         } else {
            String result = data[1];
            if (!value.equalsIgnoreCase(result)) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PRIMARY_PENDING_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
            } else if (this.strictMessageKind.canState(input, SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG) && this.strictMessageKind.isState(input)) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_STORED_LOUDPROXYSTATE, 15);
            } else {
               output.dispatchSpawnLookup(input, target);
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_SECONDARY_LOUDPROXYSTATE);
            }
         }
      }
   }

   @Override
   public void updateVerifiedServerAdapter(
      VerifiedServerAdapter target, SpawnLookup input, LimboCoordinator output, RootListenerContract context, String data, String[] value
   ) {
      if (value.length != 2) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target,
            LoudProxyState.LINKED_LOUDPROXYSTATE,
            "/" + data.toLowerCase(Locale.ENGLISH) + " " + value[0].toLowerCase(Locale.ENGLISH) + " <" + this.strictMessageKind.findMessage() + ">"
         );
      } else {
         String result = value[1].toLowerCase(Locale.ENGLISH);
         String request = this.strictMessageKind.createMessage(input);
         if (request != null && this.strictMessageKind.canState(input, SecondaryPlatformCatalog.SECONDARY_PLATFORM_CATALOG)) {
            if (request.equalsIgnoreCase(result)) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(
                  target, LoudProxyState.PENDING_AUTHENTICATED_LOUDPROXYSTATE, this.strictMessageKind.findMessage(), request
               );
               return;
            }

            if (this.strictMessageKind.isState(input)) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_STORED_LOUDPROXYSTATE, 15);
               return;
            }
         }

         if (input.fetchQuickDiscordHandler().loadMessage() != null) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_MAIN_LOUDPROXYSTATE, this.strictMessageKind.findMessage());
         } else {
            String[] response = result.split("@");
            if (result.length() < 254 && response.length == 2) {
               List source = StrictPremiumOption.SECONDARY_STRICTPREMIUMOPTION.a(new Object[0]);
               if (!source.isEmpty() && !source.contains(response[1].toLowerCase(Locale.ENGLISH))) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PRIMARY_CURRENT_LOUDPROXYSTATE);
               } else {
                  List entry = StrictPremiumOption.DIRECT_STRICTPREMIUMOPTION.a(new Object[0]);
                  if (!entry.isEmpty() && entry.contains(response[1].toLowerCase(Locale.ENGLISH))) {
                     CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PRIMARY_CURRENT_LOUDPROXYSTATE);
                  } else {
                     String record = this.indirectSessionHandler.resolveDiscordVerifier().getMailNotifier().findStoredLoginGate().pendingName;
                     if (record.equalsIgnoreCase(result)) {
                        CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PRIMARY_MAIN_LOUDPROXYSTATE);
                     } else {
                        int item = StrictPremiumOption.INCOMING_STRICTPREMIUMOPTION.r();
                        if (item > 0 && this.strictMessageKind.processCount(this.indirectSessionHandler, result) >= item) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(
                              target, LoudProxyState.PENDING_LOCAL_LOUDPROXYSTATE, this.strictMessageKind.findMessage()
                           );
                        } else {
                           this.strictMessageKind.handleSpawnLookup(input);
                           this.strictMessageKind.updateSpawnLookup(input, result);
                           context.processSpawnLookup(input, target, result);
                           CachedSettingsGateway.executeOutgoingSenderAdapter(
                              target, LoudProxyState.PENDING_AUTHENTICATED_LOUDPROXYSTATE, this.strictMessageKind.findMessage(), result
                           );
                        }
                     }
                  }
               }
            } else {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PRIMARY_PENDING_LOUDPROXYSTATE);
            }
         }
      }
   }

   @Override
   public List<String> buildCollection(OutgoingSenderAdapter target, String input, String[] output) {
      if (output.length == 2) {
         String context = output[1];
         if (!context.isEmpty()) {
            int data = context.lastIndexOf(64);
            if (data != -1) {
               List value = StrictPremiumOption.SECONDARY_STRICTPREMIUMOPTION.a(new Object[0]);
               List result = StrictPremiumOption.DIRECT_STRICTPREMIUMOPTION.a(new Object[0]);
               if (!value.isEmpty()) {
                  return value.stream()
                     .map(instance -> "@" + instance)
                     .filter(inputValue -> inputValue.toLowerCase(Locale.ENGLISH).startsWith(context.substring(data)))
                     .filter(targetValue -> !result.contains(targetValue))
                     .map(inputValue -> context.substring(0, data) + inputValue)
                     .collect(Collectors.toList());
               }

               return entries.stream()
                  .filter(inputValue -> inputValue.toLowerCase(Locale.ENGLISH).startsWith(context.substring(data)))
                  .filter(targetValue -> !result.contains(targetValue))
                  .map(inputValue -> context.substring(0, data) + inputValue)
                  .collect(Collectors.toList());
            }
         }
      }

      return super.buildCollection(target, input, output);
   }
}
