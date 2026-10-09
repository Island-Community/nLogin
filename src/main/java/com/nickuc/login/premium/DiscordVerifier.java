package com.nickuc.login.premium;

import com.nickuc.login.account.StrictPremiumOption;
import com.nickuc.login.auth.login.PrivateLoginGate;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.discord.FastDiscordNotifier;
import com.nickuc.login.discord.PasswordHashBridge;
import com.nickuc.login.mail.MailNotifier;
import com.nickuc.login.platform.account.IncomingAccountHandler;
import com.nickuc.login.platform.sender.TightSenderAdapter;
import com.nickuc.login.spawn.VerifiedNoticeKind;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import java.io.File;
import javax.annotation.Nullable;


public class DiscordVerifier implements IncomingAccountHandler {
   @Nullable
   private PasswordHashLoader passwordHashLoader;
   private FastDiscordNotifier fastDiscordNotifier;
   private MailNotifier mailNotifier;
   @Nullable
   private PasswordHashLoader activePasswordHashLoader;

   @Nullable
   public PasswordHashLoader retrievePasswordHashLoader() {
      return this.activePasswordHashLoader;
   }

   public MailNotifier getMailNotifier() {
      return this.mailNotifier;
   }

   @Nullable
   public PasswordHashLoader fetchPasswordHashLoader() {
      return this.passwordHashLoader;
   }

   @Override
   public void processPasswordStore(PasswordStore target, boolean input) {
      this.savePasswordStore(target);
      if (target.a().getCount() == 9) {
         File output = new File(target.resolveFile() + File.separator + "premium", "2fa");
         SettingsLookup.loadLoudNoticeCatalog(
            target, this.passwordHashLoader = new PasswordHashLoader("discord.yml", output), "premium/2fa/discord/discord_%s.yml", input
         );
         SettingsLookup.loadLoudNoticeCatalog(
            target, this.activePasswordHashLoader = new PasswordHashLoader("email.yml", output), "premium/2fa/email/email_%s.yml", input
         );
         StrictPremiumOption.savePasswordStore(target);
         PasswordHashBridge context = target.a();
         boolean data = CachedSettingsGateway.loadState();

         try {
            if (StrictPremiumOption.STRICT_PREMIUM_OPTION.retrieveState()
               && context.verifyState(new TightSenderAdapter[]{VerifiedNoticeKind.VERIFIED_VERIFIEDNOTICEKIND})) {
               (this.fastDiscordNotifier = new FastDiscordNotifier(target, this)).updateTask();
            }
         } catch (Throwable request) {
            PasswordHashContainer.handleMessage(data ? "[Discord] Não foi possível conectar com o Discord." : "[Discord] Unable to connect with Discord.", request);
         }

         try {
            if (StrictPremiumOption.VERIFIED_STRICTPREMIUMOPTION.retrieveState()
               && (
                  PrivateLoginGate.loadCount() < 11
                     || context.verifyState(
                        VerifiedNoticeKind.PRIMARY_VERIFIEDNOTICEKIND, VerifiedNoticeKind.MAIN_VERIFIEDNOTICEKIND, VerifiedNoticeKind.LOCAL_VERIFIEDNOTICEKIND
                     )
               )
               && context.verifyState(
                  VerifiedNoticeKind.REMOTE_VERIFIEDNOTICEKIND, VerifiedNoticeKind.STORED_VERIFIEDNOTICEKIND, VerifiedNoticeKind.CACHED_VERIFIEDNOTICEKIND
               )) {
               (this.mailNotifier = new MailNotifier(target, this)).updateTask();
            }
         } catch (Throwable result) {
            PasswordHashContainer.handleMessage(data ? "[Email] Não foi possível conectar com o e-mail." : "[Email] Unable to connect with email.", result);
         }
      }
   }

   public FastDiscordNotifier retrieveFastDiscordNotifier() {
      return this.fastDiscordNotifier;
   }

   public void savePasswordStore(PasswordStore target) {
      try {
         if (this.fastDiscordNotifier != null) {
            this.fastDiscordNotifier.loadState();
            this.fastDiscordNotifier = null;
         }
      } catch (Exception context) {
         PasswordHashContainer.handleMessage("Failed to close discord", context);
      }

      try {
         if (this.mailNotifier != null) {
            this.mailNotifier.aE();
            this.mailNotifier = null;
         }
      } catch (Exception output) {
         PasswordHashContainer.handleMessage("Failed to close email", output);
      }

      this.passwordHashLoader = null;
      this.activePasswordHashLoader = null;
   }
}
