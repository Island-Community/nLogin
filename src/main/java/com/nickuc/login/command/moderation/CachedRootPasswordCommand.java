package com.nickuc.login.command.moderation;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.security.hashing.CachedPasswordHashHasher;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.Locale;

public class CachedRootPasswordCommand extends LoginSource {
   private static float factor = Float.intBitsToFloat(1097859072);
   private static float activeFactor = Float.intBitsToFloat(1077936128);

   private void dispatchOutgoingSenderAdapter(OutgoingSenderAdapter target, SpawnLookup input) {
      String output = input.findMessage();
      CachedPasswordHashHasher context = input.resolveCachedPasswordHashHasher();
      if (context.buildObject("block-" + output) == null) {
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, this.loadState() ? "§cEste jogador não está banido." : "§cThis player is not banned.");
         CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
      } else {
         context.updateMessage("block-" + output);
         context.updateMessage("block-tries-" + output);
         CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
         CachedSettingsGateway.handleOutgoingSenderAdapter(
            target,
            this.loadState()
               ? "§cO endereço de IP " + output + " §7(" + input.retrieveMessage() + ") §cfoi desbanido."
               : "§cThe IP address " + output + " §7(" + input.retrieveMessage() + ") §chas been unbanned."
         );
      }
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (input.length != 2) {
         CachedSettingsGateway.executeOutgoingSenderAdapter(
            target, LoudProxyState.LINKED_LOUDPROXYSTATE, "/nlogin " + this.findMessage().toLowerCase(Locale.ENGLISH) + " <player>"
         );
      } else {
         String output = input[1];
         IndirectPasswordResolver context = this.passwordStore.findIndirectPasswordResolver();
         SpawnLookup data = context.loadSpawnLookup(target, super.internalLoginOption, input, output);
         if (data != null) {
            synchronized (data.object) {
               if (!data.fetchState()) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.INCOMING_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                  return;
               }
            }

            this.dispatchOutgoingSenderAdapter(target, data);
         }
      }
   }

   public CachedRootPasswordCommand(PasswordStore target) {
      super(target, "unban", "nlogin.command.nlogin.unban", false, false);
   }
}
