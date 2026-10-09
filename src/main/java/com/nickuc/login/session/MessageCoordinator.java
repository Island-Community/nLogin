package com.nickuc.login.session;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.locale.LocaleCollection;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.ArrayList;
import java.util.List;

public class MessageCoordinator extends LocaleCollection {
   @Override
   public void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (!(target instanceof VerifiedServerAdapter)) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.TOP_LOUDPROXYSTATE);
      } else {
         VerifiedServerAdapter context = (VerifiedServerAdapter)target;
         LimboRegistry data = this.indirectSessionHandler.loadLimboRegistry();
         if (data.canState(context)) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(context, LoudProxyState.LIVE_LOUDPROXYSTATE);
            CachedSettingsGateway.updateVerifiedServerAdapter(context, SharedLoginOption.MAIN_SHAREDLOGINOPTION);
            CachedSettingsGateway.processOutgoingSenderAdapter(context, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
         } else {
            LimboCoordinator value = data.loadLimboCoordinator(context);
            SpawnLookup result = value.loadSpawnLookup();
            List request = createCollection(result);
            switch (request.size()) {
               case 0:
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.PENDING_PRIMARY_LOUDPROXYSTATE, "2FA");
                  break;
               case 1:
                  StrictMessageKind reference = (StrictMessageKind)request.get(0);
                  InternalLoginOption subject;
                  switch (reference) {
                     case STRICT_MESSAGE_KIND:
                        subject = InternalLoginOption.PENDING_INTERNALLOGINOPTION;
                        break;
                     case ACTIVE_STRICTMESSAGEKIND:
                        subject = InternalLoginOption.CURRENT_INTERNALLOGINOPTION;
                        if (output.length != 1) {
                           CachedSettingsGateway.executeOutgoingSenderAdapter(
                              target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/" + input + " <" + reference.findMessage() + ">"
                           );
                           return;
                        }
                        break;
                     default:
                        throw new IllegalStateException("Unexpected value: " + request);
                  }

                  String[] option = new String[output.length + 1];
                  option[0] = "recover";
                  System.arraycopy(output, 0, option, 1, output.length);
                  subject.performVerifiedServerAdapter(context, value, option);
                  break;
               default:
                  SecondaryAccountHandler response = value.getSecondaryAccountHandler();

                  for (String record : CachedSettingsGateway.computeCollection(LoudProxyState.PENDING_INTERNAL_LOUDPROXYSTATE, context)) {
                     if (record.length() > 3 && record.contains("[") && record.contains("]")) {
                        int item = record.indexOf("[") + 1;
                        int element = record.lastIndexOf("]");
                        if (element - item <= 1) {
                           throw new IllegalArgumentException(
                              "Wrong line format for "
                                 + LoudProxyState.PENDING_INTERNAL_LOUDPROXYSTATE.retrieveBusyLoginProcessor().fetchNames()[0]
                                 + " message! \""
                                 + record
                                 + "\""
                           );
                        }

                        String content = "";
                        if (item > 0) {
                           content = record.substring(0, item - 1);
                        }

                        String payload = "";
                        if (element + 1 != record.length()) {
                           payload = record.substring(element);
                        }

                        String holder = content + record.substring(item, element) + payload;
                        request.forEach(inputValue -> {
                           String outputValue;
                           String contextValue;
                           switch (inputValue) {
                              case STRICT_MESSAGE_KIND:
                                 outputValue = InternalLoginOption.PENDING_INTERNALLOGINOPTION.fetchPasswordHashCommand().loadMessage();
                                 contextValue = "/" + outputValue + " recover";
                                 break;
                              case ACTIVE_STRICTMESSAGEKIND:
                                 outputValue = InternalLoginOption.CURRENT_INTERNALLOGINOPTION.fetchPasswordHashCommand().loadMessage();
                                 contextValue = "/" + outputValue + " recover <" + inputValue.findMessage() + ">";
                                 break;
                              default:
                                 throw new IllegalStateException("Unexpected value: " + inputValue);
                           }

                           response.dispatchMessage(OpenLocaleBarrier.handleMessage(holder, OpenLocaleBarrier.buildMessage(inputValue.findMessage()), outputValue), contextValue);
                        });
                     } else {
                        response.executeMessage(record);
                     }
                  }
            }
         }
      }
   }

   public MessageCoordinator(InternalLoginOption target) {
      super(target);
   }

   private static List<StrictMessageKind> createCollection(SpawnLookup instance) {
      ArrayList target = new ArrayList();

      for (StrictMessageKind data : StrictMessageKind.values()) {
         if (data.computeMessage(instance) != null) {
            target.add(data);
         }
      }

      return target;
   }

   public static boolean verifyState(PasswordStore instance, SpawnLookup target) {
      return createCollection(target).stream().anyMatch(targetValue -> targetValue.hasState(instance) && targetValue.processRootListenerContract(instance).findState());
   }
}
