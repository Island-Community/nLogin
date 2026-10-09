package com.nickuc.login.discord;

import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.login.LoginSource;
import com.nickuc.login.storage.password.PasswordStore;

public class DiscordLinker extends LoginSource {
   public DiscordLinker(PasswordStore target) {
      super(target, "support", "nlogin.admin", false, false);
   }

   @Override
   public void processOutgoingSenderAdapter(OutgoingSenderAdapter target, String[] input) {
      if (target instanceof VerifiedServerAdapter) {
         SecondaryAccountHandler output = this.passwordStore.loadLimboRegistry().loadLimboCoordinator((VerifiedServerAdapter)target).getSecondaryAccountHandler();
         output.executeMessage("");
         output.executeMessage(" §aTrusted sources:");
         output.executeMessage("  §7Website: §fhttps://nickuc.com", "https://www.nickuc.com");
         output.executeMessage("  §7GitHub: §fhttps://github.com/nickuc-com", "https://www.github.com/nickuc-com");
         output.executeMessage("");
         output.executeMessage(" §aOfficial support channels:");
         output.executeMessage("  §7Documentation: §fhttps://docs.nickuc.com/nlogin/", "https://docs.nickuc.com/nlogin/");
         output.executeMessage("  §7Discord: §fhttps://nickuc.com/discord", "https://www.nickuc.com/discord");
         output.executeMessage("  §7Email: §fsupport@nickuc.com");
         if (this.lenientPremiumOption == LenientPremiumOption.PRIMARY_LENIENTPREMIUMOPTION) {
            output.executeMessage("  §7VK community: §fnickuc.com/vk", "https://www.nickuc.com/vk");
         }

         output.executeMessage("");
      } else {
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §aTrusted sources:");
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7Website: §fhttps://nickuc.com");
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7GitHub: §fhttps://github.com/nickuc-com");
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, " §aOfficial support channels:");
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7Documentation: §fhttps://docs.nickuc.com/nlogin/");
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7Discord: §fhttps://nickuc.com/discord");
         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7Email: §fsupport@nickuc.com");
         if (this.lenientPremiumOption == LenientPremiumOption.PRIMARY_LENIENTPREMIUMOPTION) {
            CachedSettingsGateway.handleOutgoingSenderAdapter(target, "  §7VK community: §fnickuc.com/vk");
         }

         CachedSettingsGateway.handleOutgoingSenderAdapter(target, "");
      }
   }
}
