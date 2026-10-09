package com.nickuc.login.command.setup;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.PrivateLoginOption;
import com.nickuc.login.notification.NoticeSender;
import com.nickuc.login.platform.player.DirectPlayerContract;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.packet.SilentPacketAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.LowLocaleVerifier;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.password.PasswordStore;
import java.util.Locale;


public abstract class DeletePurgeCommand implements SilentPacketAdapter, DirectPlayerContract {
   private final CachedProxyCatalog cachedProxyCatalog;
   private final String name;

   public abstract void handlePasswordStore(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context);

   @Override
   public void dispatchPasswordStore(
      PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context, PrivateLoginOption data
   ) {
      if (data == PrivateLoginOption.INTERNAL_PRIVATELOGINOPTION) {
         LowLocaleVerifier value = (LowLocaleVerifier)InternalLoginOption.INTERNAL_LOGIN_OPTION.fetchPasswordHashCommand();

         try {
            value.executeOutgoingSenderAdapter(input, value.loadMessage(), new String[]{"install", this.name});
         } catch (Exception request) {
            output.getSecondaryAccountHandler()
               .executeMessage("§cSorry, but an error occurred while executing this command. More information on the console. (spoofed)");
            PasswordHashContainer.handleMessage(
               "Unable to run command " + value.loadMessage().toLowerCase(Locale.ENGLISH) + "! sender = " + input.getName() + ", async = false, spoofed = true",
               request
            );
         }
      }

      SilentPacketAdapter.super.a(target, input, output, context, data);
   }

   @Override
   public BusyLoginBarrier[] loadValues(PasswordStore target, VerifiedServerAdapter input, LimboCoordinator output, SecondaryAccountHandler context) {
      String data;
      String value;
      String result;
      if (output.loadState()) {
         data = "§a[⬇ Instalar]";
         value = "§c[✖ Recusar]";
         result = "§7Clique aqui para instalar automaticamente o plugin.";
      } else {
         data = "§a[⬇ Install]";
         value = "§c[✖ Refuse]";
         result = "§7Click here to automatically install the plugin.";
      }

      this.handlePasswordStore(target, input, output, context);
      return new BusyLoginBarrier[]{
         new BusyLoginBarrier(PrivateLoginOption.INTERNAL_PRIVATELOGINOPTION, new NoticeSender(data, result)),
         new BusyLoginBarrier(PrivateLoginOption.PENDING_PRIVATELOGINOPTION, new NoticeSender(value, null))
      };
   }

   public DeletePurgeCommand(CachedProxyCatalog target, String input) {
      this.cachedProxyCatalog = target;
      this.name = input;
   }

   public String at() {
      return this.name;
   }

   @Override
   public CachedProxyCatalog fetchCachedProxyCatalog() {
      return this.cachedProxyCatalog;
   }
}
