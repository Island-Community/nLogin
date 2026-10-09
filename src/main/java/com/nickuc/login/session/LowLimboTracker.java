package com.nickuc.login.session;

import com.nickuc.login.api.enums.LoginType;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.PrimaryLoginService;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.FloodgateResolver;
import com.nickuc.login.protocol.AccountAdapter;
import com.nickuc.login.spawn.PremiumOption;
import com.nickuc.login.storage.password.PasswordStore;
import io.netty.channel.Channel;
import javax.annotation.Nullable;

public class LowLimboTracker extends FloodgateResolver {
   private final SecondarySenderAdapter secondarySenderAdapter;

   public void executeVerifiedServerAdapter(VerifiedServerAdapter target, String input, boolean output) {
      long context = System.nanoTime();

      try {
         if (target.loadState()) {
            LimboCoordinator value = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(target);
            this.secondarySenderAdapter.getPasswordHashAdapter().dispatchVerifiedServerAdapter(target, value, input);
            if (value.loadTightPlatformCatalog().isState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)) {
               this.secondarySenderAdapter
                  .getPasswordHashAdapter()
                  .updateVerifiedServerAdapter(target, 2, "type", LoginType.SESSION.ordinal(), "sessionFromProxy", true, "restoreLimbo", true);
            } else if (output) {
               super.processVerifiedServerAdapter(target, value);
            }

            this.secondarySenderAdapter.getPasswordHashAdapter().performVerifiedServerAdapter(target, value);
            return;
         }
      } catch (Throwable source) {
         PasswordHashContainer.handleMessage("Severe error during login request when joining. (" + target.getName() + ")", source);
         target.buildCompletableFuture("§4[nLogin] Severe internal error detected. Please report to an admin.");
         return;
      } finally {
         PrimaryLoginService.savePremiumOption(PremiumOption.CURRENT_PREMIUMOPTION, context);
      }
   }

   @Override
   public void performVerifiedServerAdapter(VerifiedServerAdapter target) {
      long input = System.nanoTime();
      LimboRegistry context = this.passwordStore.loadLimboRegistry();

      try {
         super.performVerifiedServerAdapter(target);
      } catch (Throwable response) {
         PasswordHashContainer.handleMessage("Severe error during quit handler. (" + target.getName() + ")", response);
      } finally {
         context.saveVerifiedServerAdapter(target);
         PrimaryLoginService.savePremiumOption(PremiumOption.PRIMARY_PREMIUMOPTION, input);
      }
   }

   @Nullable
   public String handleMessage(VerifiedServerAdapter target, Object input, Channel output, boolean context) {
      long data = System.nanoTime();
      String result = target.getName();

      try {
         if (output == null) {
            return OpenLocaleBarrier.loadMessage(
               "§4[nLogin]", "", "§cUnable to get the channel from " + input.getClass().getSimpleName() + " event", "", "§ePlease contact an administrator."
            );
         } else {
            AccountAdapter request = (AccountAdapter)output.attr(AccountAdapter.attributeKey).get();
            if (request == null) {
               String payload = "Unable to find connection cache for user "
                  + target.getName()
                  + " in "
                  + input.getClass().getSimpleName()
                  + " event (behavior changed by a plugin?)";
               PasswordHashContainer.performMessage(payload);
               return OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + payload, "", "§ePlease contact an administrator.");
            } else {
               SecondaryConnectionContract response = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
               return super.createMessage(
                  target,
                  request.spawnLookup,
                  request.name,
                  target.retrieveInetSocketAddress(),
                  context,
                  request.floodgateBarrier != null || response != null && response.isState(target.getUniqueId()),
                  request
               );
            }
         }
      } catch (Throwable element) {
         PasswordHashContainer.handleMessage("Severe error during login handler. (" + result + ")", element);
         return "§4[nLogin] Severe internal error detected. Please report to an admin.";
      } finally {
         PrimaryLoginService.savePremiumOption(PremiumOption.PENDING_PREMIUMOPTION, data);
      }
   }

   public LowLimboTracker(PasswordStore target, SecondarySenderAdapter input) {
      super(target);
      this.secondarySenderAdapter = input;
   }
}
