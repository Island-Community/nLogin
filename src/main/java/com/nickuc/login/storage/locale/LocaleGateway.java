package com.nickuc.login.storage.locale;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.login.StrictLoginHandler;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LocaleGateway extends LoginSource {
   private static float factor = Float.intBitsToFloat(1077936128);
   private static float activeFactor = Float.intBitsToFloat(1097859072);

   public LocaleGateway(PasswordStore target) {
      super(target, "clearip", "nlogin.admin", true, false);
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 2) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " <ip>"
         );
      } else {
         LiveLoginCheckpoint output = new LiveLoginCheckpoint();
         String context = input[1];
         if ("127.0.0.1".equals(context)) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
         } else {
            StrictLoginHandler data = this.passwordStore
               .fetchLocalSettingsRepository()
               .loadSharedListenerContract()
               .computeStrictLoginHandler(
                  String.format(
                     "UPDATE `%s` SET `%s` = '127.0.0.1' WHERE `%s` = ?",
                     SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]),
                     OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName(),
                     OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
                  ),
                  context
               );
            int value = (Integer)data.resolveObject();
            if (value == 0) {
               CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
            } else {
               CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, activeFactor, factor);
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§a✔ You have cleared the IP §f%s §aof all accounts §7(%s)", context, value);
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
               CachedSettingsGateway.handleOutgoingSenderAdapter(target, "§e⚑ This operation took §f" + output.loadMessage(TimeUnit.SECONDS, 2) + "s§e.");
            }
         }
      }
   }
}
