package com.nickuc.login.premium;

import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.model.LoudNoticeCatalog;
import com.nickuc.login.model.StrictPlatformCatalog;
import com.nickuc.login.i18n.LocaleBundle;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.io.File;
import java.util.Arrays;
import javax.annotation.Nullable;

public class SettingsLookup {
   public static LoudNoticeCatalog computeLoudNoticeCatalog(
      IndirectSessionHandler<?> instance, PasswordHashLoader target, String input, @Nullable LenientPremiumOption output, boolean context
   ) {
      if (target.resolveStateForState() && !context) {
         return LoudNoticeCatalog.LOUD_NOTICE_CATALOG;
      }

      if (output == null) {
         StrictPlatformCatalog data = LocaleBundle.loadStrictPlatformCatalog();
         if (data != null) {
            switch (data) {
               case STRICT_PLATFORM_CATALOG:
                  output = LenientPremiumOption.VERIFIED_LENIENTPREMIUMOPTION;
                  break;
               case ACTIVE_STRICTPLATFORMCATALOG:
                  output = LenientPremiumOption.ACTIVE_LENIENTPREMIUMOPTION;
                  break;
               case PENDING_STRICTPLATFORMCATALOG:
                  output = LenientPremiumOption.PENDING_LENIENTPREMIUMOPTION;
                  break;
               case CURRENT_STRICTPLATFORMCATALOG:
                  output = LenientPremiumOption.LENIENT_PREMIUM_OPTION;
                  break;
               case PRIMARY_STRICTPLATFORMCATALOG:
                  output = LenientPremiumOption.PRIMARY_LENIENTPREMIUMOPTION;
            }
         }
      }

      if (output == null) {
         File request = new File(instance.resolveFile().getParentFile(), "OpeNLogin");
         if (request.exists() && request.isDirectory()) {
            PasswordHashLoader value = new PasswordHashLoader("config.yml", request);
            if (value.canState("languageFile")) {
               String result = value.b("languageFile");
               if (result != null) {
                  if (!result.endsWith(".yml")) {
                     result = result + ".yml";
                  }

                  output = LenientPremiumOption.resolveLenientPremiumOption(result);
               }
            }
         }
      }

      if (output == null) {
         String response = instance.retrievePasswordHashLoader().a("language-file", instance.retrievePasswordHashLoader().a("languageFile", "messages_en.yml"));
         output = LenientPremiumOption.resolveLenientPremiumOption(response);
      }

      if (context) {
         LenientPremiumOption source = LenientPremiumOption.computeLenientPremiumOption(target.a("language-version"));
         if (source == output) {
            return LoudNoticeCatalog.LOUD_NOTICE_CATALOG;
         }

         File entry = target.retrieveFile();
         File record = MessageProcessor.buildFile(entry, MessageProcessor.resolveMessage(entry) + "-%s.yml");
         if (!entry.renameTo(record)) {
            PasswordHashContainer.performMessage("Unable to move " + entry.getAbsolutePath() + " to " + record.getAbsolutePath());
            return LoudNoticeCatalog.ACTIVE_LOUDNOTICECATALOG;
         }
      }

      if (target.hasStateForState("com/nickuc/login/config/" + String.format(input, output.currentName))) {
         PasswordHashContainer.dispatchMessage("The " + target.retrieveFile().getName() + " file was created successfully.");
         if ("config_%s.yml".equals(input) || "premium/config_%s.yml".equals(input)) {
            instance.fetchParentSettingsLookup().loadPrimaryPasswordHashVerifier().createPrimaryPasswordHashVerifier("config-reset", true).sendTask();
         }
      }

      return LoudNoticeCatalog.LOUD_NOTICE_CATALOG;
   }

   public static void executePasswordStore(PasswordStore instance, boolean target) {
      LiveLoginCheckpoint input = new LiveLoginCheckpoint();
      String output = CachedSettingsGateway.loadMessage();
      CachedSettingsGateway.lenientPremiumOption = LenientPremiumOption.resolveLenientPremiumOption(output);
      int context = !target && SpawnState.PENDING_SHARED_SPAWNSTATE.ar() ? 0 : 1;
      LenientPremiumOption[] data = context != 0
         ? new LenientPremiumOption[]{CachedSettingsGateway.lenientPremiumOption}
         : Arrays.stream(LenientPremiumOption.values())
            .filter(instanceValue -> instanceValue != LenientPremiumOption.ROOT_LENIENTPREMIUMOPTION)
            .toArray(LenientPremiumOption[]::new);

      for (LenientPremiumOption response : data) {
         CachedSettingsGateway.dispatchPasswordStore(instance, response);
      }

      PasswordHashContainer.dispatchMessage("Messages were loaded in %s ms (%s).", input.loadTime(), context != 0 ? "single file" : "multiple files");
   }

   public static boolean validateState(PasswordStore instance) {
      return Pbkdf2Linker.computeLoudNoticeCatalog(instance, false, true).loadState();
   }

   public static LoudNoticeCatalog loadLoudNoticeCatalog(IndirectSessionHandler<?> instance, PasswordHashLoader target, String input, boolean output) {
      return computeLoudNoticeCatalog(instance, target, input, null, output);
   }
}
