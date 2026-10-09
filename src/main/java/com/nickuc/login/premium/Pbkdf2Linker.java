package com.nickuc.login.premium;

import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.locale.LocalLocaleFlow;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.PasswordHashLoader;
import com.nickuc.login.model.LoudNoticeCatalog;
import com.nickuc.login.model.NoticeKind;
import com.nickuc.login.model.PlatformCatalog;
import com.nickuc.login.model.ProxyState;
import com.nickuc.login.model.UpstreamLoginOption;
import com.nickuc.login.model.UpstreamSpawnState;
import com.nickuc.login.platform.proxy.SilentProxyState;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import com.nickuc.login.session.MojangCoordinator;
import com.nickuc.login.spawn.PrimaryMessageOption;
import com.nickuc.login.spawn.VerifiedNoticeKind;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.notice.DirectNoticeCatalog;
import com.nickuc.login.storage.password.LocalPasswordTable;
import com.nickuc.login.storage.settings.LocalSettingsRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import org.bukkit.configuration.file.YamlConfiguration;

public class Pbkdf2Linker {
   private static boolean enabled;
   private static Pattern pattern;
   private static boolean activeEnabled = true;
   private static ProxyState proxyState;
   private static List<Pattern> entries;
   private static UpstreamLoginOption upstreamLoginOption;
   private static NoticeKind noticeKind;
   private static String name;
   private static boolean pendingEnabled = true;
   private static boolean currentEnabled;
   private static byte[] values;
   private static UpstreamSpawnState upstreamSpawnState;
   private static KeyPair keyPair;
   private static DirectNoticeCatalog directNoticeCatalog;
   private static ProxyState activeProxyState;
   private static List<Pattern> activeEntries;
   private static Pattern activePattern;

   public static String getMessage() {
      return name;
   }

   public static List<Pattern> retrieveCollection() {
      return activeEntries;
   }

   private static void performTask() {
      String instance = Optional.ofNullable(MojangCoordinator.name).orElse(UpstreamLoginOption.ACTIVE_UPSTREAMLOGINOPTION.name()).toUpperCase(Locale.ENGLISH);

      try {
         upstreamLoginOption = UpstreamLoginOption.valueOf(instance);
      } catch (IllegalArgumentException value) {
         PasswordHashContainer.performMessage(
            CachedSettingsGateway.loadState() ? "O tipo de unique id \"" + instance + "\" não existe." : "The \"" + instance + "\" unique id type does not exist."
         );
         upstreamLoginOption = UpstreamLoginOption.ACTIVE_UPSTREAMLOGINOPTION;
      }

      String target = QuickPremiumOption.QUICK_PREMIUM_OPTION.a(new Object[0]).toUpperCase(Locale.ENGLISH);

      try {
         proxyState = ProxyState.valueOf(target);
      } catch (IllegalArgumentException data) {
         PasswordHashContainer.performMessage(
            CachedSettingsGateway.loadState() ? "A ação \"" + target + "\" não existe." : "The \"" + target + "\" action does not exist."
         );
         proxyState = ProxyState.valueOf((String)QuickPremiumOption.createObject(QuickPremiumOption.QUICK_PREMIUM_OPTION));
      }

      String input = QuickPremiumOption.ACTIVE_QUICKPREMIUMOPTION.a(new Object[0]).toUpperCase(Locale.ENGLISH);

      try {
         activeProxyState = ProxyState.valueOf(input);
      } catch (IllegalArgumentException context) {
         PasswordHashContainer.performMessage(
            CachedSettingsGateway.loadState() ? "A ação \"" + input + "\" não existe." : "The \"" + input + "\" action does not exist."
         );
         activeProxyState = ProxyState.valueOf((String)QuickPremiumOption.createObject(QuickPremiumOption.ACTIVE_QUICKPREMIUMOPTION));
      }

      if (proxyState == ProxyState.ACTIVE_PROXYSTATE) {
         saveQuickPremiumOption(QuickPremiumOption.PENDING_QUICKPREMIUMOPTION, false);
      }
   }

   private static void processPasswordStore(PasswordStore instance) {
      SpawnState target = SpawnState.ACTIVE_READY_SPAWNSTATE;
      int input = target.r();
      if (input < 0) {
         handleSpawnState(target, SpawnState.buildObject(target));
         PasswordHashContainer.performMessage("The number of PBKDF2 iterations is invalid (Current: " + input + ".");
      }
   }

   @Nullable
   public static String loadMessage(String instance, boolean target) {
      String input = (target ? QuickPremiumOption.PRIMARY_QUICKPREMIUMOPTION : QuickPremiumOption.REMOTE_QUICKPREMIUMOPTION).a(new Object[0]);
      boolean output = target ? activeEnabled : pendingEnabled;
      if (input.isEmpty()) {
         return null;
      }

      if (input.length() >= instance.length()) {
         return null;
      }

      boolean context = output ? instance.endsWith(input) : instance.startsWith(input);
      if (!context) {
         return null;
      }

      int data = output ? 0 : input.length();
      int value = output ? instance.length() - input.length() : instance.length();
      return instance.substring(data, value);
   }

   public static LoudNoticeCatalog computeLoudNoticeCatalog(PasswordStore instance) {
      return computeLoudNoticeCatalog(instance, true, false);
   }

   public static NoticeKind retrieveNoticeKind() {
      return noticeKind;
   }

   public static void executeNoticeKind(NoticeKind instance) {
      noticeKind = instance;
   }

   public static KeyPair resolveKeyPair() {
      return keyPair;
   }

   private static void sendTask() {
      SpawnState instance = SpawnState.PENDING_UPSTREAM_SPAWNSTATE;
      List target = instance.b(new Object[0]);
      target.replaceAll(instanceValue -> {
         instanceValue = instanceValue.toLowerCase(Locale.ENGLISH);
         if (!instanceValue.startsWith("/")) {
            instanceValue = "/" + instanceValue;
         }

         return instanceValue;
      });
      handleSpawnState(instance, target);
   }

   public static boolean fetchState() {
      return enabled;
   }

   private static void performPasswordStore(PasswordStore instance, boolean target, boolean input) {
      if (!input) {
         instance.loadInternalAccountHandler().a(instance, target);
         instance.findIndirectPasswordHashVerifier().processPasswordStore(instance, target);
      }

      DiscordVerifier output = instance.resolveDiscordVerifier();
      if (output != null) {
         output.processPasswordStore(instance, target);
      }
   }

   public static String handleMessage(String instance, boolean target) {
      String input = (target ? QuickPremiumOption.PRIMARY_QUICKPREMIUMOPTION : QuickPremiumOption.REMOTE_QUICKPREMIUMOPTION).a(new Object[0]);
      boolean output = target ? activeEnabled : pendingEnabled;
      String data = instance;

      String context;
      do {
         context = output ? data + input : input + data;
         data = data.substring(0, data.length() - 1);
      } while (data.length() > 1 && context.getBytes(StandardCharsets.UTF_8).length > 16);

      return context;
   }

   public static Pattern findPattern() {
      return pattern;
   }

   private static void executeTask() {
      SpawnState instance = SpawnState.ACTIVE_PENDING_SPAWNSTATE;
      int target = instance.r();
      if (target <= 0) {
         handleSpawnState(instance, SpawnState.buildObject(instance));
         PasswordHashContainer.performMessage("The configured auth timeout is invalid (Current: " + target + ".");
      }
   }

   public static void updateKeyPair(KeyPair instance) {
      keyPair = instance;
   }

   private static void handleTask() {
      String instance = SpawnState.PENDING_CACHED_SPAWNSTATE.a(new Object[0]).toUpperCase(Locale.ENGLISH);

      try {
         noticeKind = NoticeKind.valueOf(instance);
      } catch (IllegalArgumentException input) {
         PasswordHashContainer.performMessage(
            CachedSettingsGateway.loadState() ? "A config \"" + instance + "\" de dialogs não existe." : "The \"" + instance + "\" dialog config does not exist."
         );
         noticeKind = NoticeKind.valueOf((String)SpawnState.buildObject(SpawnState.PENDING_CACHED_SPAWNSTATE));
      }
   }

   private static void executePasswordStore(PasswordStore instance) {
      SpawnState target = SpawnState.PENDING_ACTIVE_SPAWNSTATE;
      int input = target.r();
      if (input < 3) {
         handleSpawnState(target, SpawnState.buildObject(target));
         PasswordHashContainer.performMessage("The number of Argon2 iterations must be greater than or equal to 3 (Current: " + input + ".");
      } else if (input > 64) {
         handleSpawnState(target, SpawnState.buildObject(target));
         PasswordHashContainer.performMessage("The number of Argon2 iterations must be less than or equal to 64 (Current: " + input + ".");
      }

      SpawnState output = SpawnState.PENDING_CURRENT_SPAWNSTATE;
      int context = output.r();
      if (context % 4 != 0) {
         handleSpawnState(output, SpawnState.buildObject(output));
         PasswordHashContainer.performMessage("The number of Argon2 memory must be multiple of 4 (Current: " + input + ".");
      } else if (context < 4) {
         handleSpawnState(output, SpawnState.buildObject(output));
         PasswordHashContainer.performMessage("The number of Argon2 memory must be greater than or equal to 4MB (Current: " + input + "MB.");
      } else if (context > 2048) {
         handleSpawnState(output, SpawnState.buildObject(output));
         PasswordHashContainer.performMessage("The number of Argon2 memory must be less than or equal to 2048MB (Current: " + input + "MB.");
      }

      SpawnState data = SpawnState.PENDING_PRIMARY_SPAWNSTATE;
      int value = data.r();
      if (value < 1) {
         handleSpawnState(data, SpawnState.buildObject(data));
         PasswordHashContainer.performMessage("The number of Argon2 parallelism must be greater than or equal to 1 (Current: " + input + ".");
      } else if (value > 8) {
         handleSpawnState(data, SpawnState.buildObject(data));
         PasswordHashContainer.performMessage("The number of Argon2 parallelism must be less than or equal to 8 (Current: " + input + ".");
      }

      try {
         if (!instance.a().verifyState(VerifiedNoticeKind.PENDING_VERIFIEDNOTICEKIND, VerifiedNoticeKind.CURRENT_VERIFIEDNOTICEKIND)) {
            int result = upstreamSpawnState != UpstreamSpawnState.UPSTREAM_SPAWN_STATE
                  && upstreamSpawnState != UpstreamSpawnState.ACTIVE_UPSTREAMSPAWNSTATE
                  && upstreamSpawnState != UpstreamSpawnState.PENDING_UPSTREAMSPAWNSTATE
               ? 0
               : 1;
            PasswordHashContainer.updateMessage(
               "Unable to download Argon2 dependencies ("
                  + VerifiedNoticeKind.PENDING_VERIFIEDNOTICEKIND.A()
                  + " && "
                  + VerifiedNoticeKind.CURRENT_VERIFIEDNOTICEKIND.A()
                  + ")."
                  + (result != 0 ? " Using PBKDF2..." : "")
            );
            if (result != 0) {
               upstreamSpawnState = UpstreamSpawnState.CURRENT_UPSTREAMSPAWNSTATE;
            }
         } else {
            currentEnabled = true;
         }
      } catch (Throwable source) {
         boolean request = PasswordHashContainer.findState();
         if (request) {
            PasswordHashContainer.handleMessage("Argon2 is unavailable for your current system!", source);
         }

         int response = upstreamSpawnState != UpstreamSpawnState.UPSTREAM_SPAWN_STATE
               && upstreamSpawnState != UpstreamSpawnState.ACTIVE_UPSTREAMSPAWNSTATE
               && upstreamSpawnState != UpstreamSpawnState.PENDING_UPSTREAMSPAWNSTATE
            ? 0
            : 1;
         if (response != 0) {
            if (!request) {
               PasswordHashContainer.updateMessage("Argon2 algorithm is unavailable for your current system, using PBKDF2");
            }

            upstreamSpawnState = UpstreamSpawnState.CURRENT_UPSTREAMSPAWNSTATE;
         }
      }
   }

   private static void updateTask() {
      String instance = SpawnState.ACTIVE_PRIMARY_SPAWNSTATE.a(new Object[0]);

      try {
         activePattern = Pattern.compile(instance);
      } catch (PatternSyntaxException input) {
         PasswordHashContainer.handleMessage("Invalid nickname pattern! \"" + instance + "\". Using default...", input);
         activePattern = Pattern.compile((String)SpawnState.ACTIVE_PRIMARY_SPAWNSTATE.getObject());
      }
   }

   public static LoudNoticeCatalog computeLoudNoticeCatalog(PasswordStore instance, boolean target, boolean input) {
      LiveLoginCheckpoint output = new LiveLoginCheckpoint();
      PasswordHashLoader context = instance.a();
      if (!context.resolveStateForState()) {
         LoudNoticeCatalog data = SettingsLookup.loadLoudNoticeCatalog(instance, context, "config_%s.yml", target);
         if (!data.loadState()) {
            return data;
         }
      } else if (!input && !context.loadState()) {
         return LoudNoticeCatalog.ACTIVE_LOUDNOTICECATALOG;
      }

      if (!context.resolveState()) {
         return LoudNoticeCatalog.ACTIVE_LOUDNOTICECATALOG;
      }

      VerifiedPasswordHashHasher.performValues(SpawnState.values(), SpawnState.secureLoginGate, context);
      if (PasswordStore.resolvePasswordStore().a().getCount() == 9) {
         PasswordHashLoader result = new PasswordHashLoader("config.yml", new File(instance.resolveFile(), "premium"));
         if (!result.resolveStateForState()) {
            LoudNoticeCatalog value = SettingsLookup.loadLoudNoticeCatalog(instance, result, "premium/config_%s.yml", target);
            if (!value.loadState()) {
               return value;
            }
         } else if (!input && !result.resolveState()) {
            return LoudNoticeCatalog.ACTIVE_LOUDNOTICECATALOG;
         }

         VerifiedPasswordHashHasher.performValues(QuickPremiumOption.values(), QuickPremiumOption.secureLoginGate, result);
      }

      savePasswordStore(instance, false);
      dispatchPasswordStore(instance);
      savePasswordStore(instance);
      performTask();
      updatePasswordStore(instance);
      executeTask();
      processPasswordStoreForValue(instance);
      processTask();
      handleTask();
      sendTask();
      updateTask();
      dispatchPasswordStore(instance, input);
      performPasswordStore(instance, false);
      performPasswordStore(instance, target, input);
      updatePasswordStore(instance, target);
      PasswordHashContainer.dispatchMessage("Settings were loaded in " + output.loadTime() + " ms.");
      return LoudNoticeCatalog.LOUD_NOTICE_CATALOG;
   }

   public static LoudNoticeCatalog loadLoudNoticeCatalog(PasswordStore instance) {
      return computeLoudNoticeCatalog(instance, false, false);
   }

   public static List<Pattern> loadCollection() {
      return entries;
   }

   private static void updatePasswordStore(PasswordStore instance, boolean target) {
      if (instance.loadState()) {
         PrimaryMessageOption.dispatchIndirectSessionHandler(instance, target);
         values = FastMessageHandler.buildPayload(instanceValue -> {
            Set targetValue = Arrays.stream(SpawnState.values()).filter(instanceValue -> SpawnState.checkState(instanceValue)).collect(Collectors.toSet());
            instanceValue.dispatchCount(4);
            instanceValue.dispatchCount(targetValue.size());
            targetValue.forEach(targetValue -> {
               Object input = targetValue.g();
               instanceValue.processMessage(targetValue.busyLoginProcessor.fetchNames()[0]);
               if (input instanceof String) {
                  instanceValue.performCount(0);
                  instanceValue.processMessage((String)input);
               } else if (input instanceof Boolean) {
                  instanceValue.performCount(1);
                  instanceValue.sendState((Boolean)input);
               } else if (input instanceof Integer) {
                  instanceValue.performCount(2);
                  instanceValue.updateCount((Integer)input);
               } else if (input instanceof Collection) {
                  Collection output = (Collection)input;
                  instanceValue.performCount(3);
                  instanceValue.updateCount(output.size());
                  output.forEach(instanceValue::processMessage);
               }
            });
         });
         name = PlatformCatalog.PENDING_PLATFORMCATALOG.createMessage(values);
      }
   }

   public static void performPasswordStore(PasswordStore instance, boolean target) {
      instance.a().retrieveLowLoginResolver().resolveLowLoginResolver("languageFile", CachedSettingsGateway.loadMessage());
      if (!target) {
         instance.a()
            .retrieveLowLoginResolver()
            .processLowLoginResolver(
               "registeredUsers", Optional.ofNullable(instance.fetchLocalSettingsRepository()).map(LocalSettingsRepository::getTime).orElse(0L)
            )
            .resolveLowLoginResolver("databaseType", getDirectNoticeCatalog().name().toLowerCase(Locale.ENGLISH));
      }
   }

   private static void handlePasswordStore(PasswordStore instance) {
      SpawnState target = SpawnState.ACTIVE_OPEN_SPAWNSTATE;
      int input = target.r();
      if (input < 8) {
         handleSpawnState(target, SpawnState.buildObject(target));
         PasswordHashContainer.performMessage("The number of BCrypt rounds must be greater than or equal to 8 (Current: " + input + ".");
      } else if (input > 31) {
         handleSpawnState(target, SpawnState.buildObject(target));
         PasswordHashContainer.performMessage("The number of BCrypt rounds must be less than or equal to 31 (Current: " + input + ".");
      }

      if (!instance.a().verifyState(VerifiedNoticeKind.VERIFIED_NOTICE_KIND, VerifiedNoticeKind.ACTIVE_VERIFIEDNOTICEKIND)) {
         int output = upstreamSpawnState != UpstreamSpawnState.PRIMARY_UPSTREAMSPAWNSTATE && upstreamSpawnState != UpstreamSpawnState.MAIN_UPSTREAMSPAWNSTATE
            ? 0
            : 1;
         PasswordHashContainer.updateMessage(
            "Unable to download Bcrypt dependencies ("
               + VerifiedNoticeKind.ACTIVE_VERIFIEDNOTICEKIND.A()
               + " && "
               + VerifiedNoticeKind.VERIFIED_NOTICE_KIND.A()
               + ")."
               + (output != 0 ? " Using native driver..." : "")
         );
         if (output != 0) {
            upstreamSpawnState = UpstreamSpawnState.PRIMARY_UPSTREAMSPAWNSTATE;
         }

         enabled = true;
      }
   }

   public static UpstreamSpawnState loadUpstreamSpawnState() {
      return upstreamSpawnState;
   }

   public static Pattern resolvePattern() {
      return activePattern;
   }

   private static void processPasswordStoreForValue(PasswordStore instance) {
      SpawnState target = SpawnState.ACTIVE_SECURE_SPAWNSTATE;
      String input = target.a(new Object[0]).toUpperCase(Locale.ENGLISH);

      try {
         upstreamSpawnState = UpstreamSpawnState.createUpstreamSpawnStateForUpstreamSpawnState(input);
         if (!upstreamSpawnState.getState()) {
            PasswordHashContainer.performMessage(
               CachedSettingsGateway.loadState()
                  ? "O algoritmo de criptografia \"" + input + "\" existe, mas não pode ser usado."
                  : "The \"" + input + "\" encryption algorithm exists, but it cannot be used."
            );
            upstreamSpawnState = UpstreamSpawnState.valueOf((String)target.getObject());
         }
      } catch (Exception data) {
         PasswordHashContainer.performMessage(
            CachedSettingsGateway.loadState()
               ? "O algoritmo de criptografia \"" + input + "\" não existe."
               : "The \"" + input + "\" encryption algorithm does not exist."
         );
         upstreamSpawnState = UpstreamSpawnState.valueOf((String)target.getObject());
      }

      handlePasswordStore(instance);
      processPasswordStore(instance);
      executePasswordStore(instance);
      handleSpawnState(SpawnState.ACTIVE_OUTGOING_SPAWNSTATE, Math.max(SpawnState.ACTIVE_OUTGOING_SPAWNSTATE.r(), 3));
      handleSpawnState(SpawnState.ACTIVE_SECONDARY_SPAWNSTATE, Math.min(SpawnState.ACTIVE_SECONDARY_SPAWNSTATE.r(), 50));

      try {
         pattern = Pattern.compile(SpawnState.ACTIVE_ROOT_SPAWNSTATE.a(new Object[0]));
      } catch (PatternSyntaxException context) {
         pattern = Pattern.compile((String)SpawnState.ACTIVE_ROOT_SPAWNSTATE.getObject());
      }
   }

   public static ProxyState fetchProxyState() {
      return proxyState;
   }

   private static void savePasswordStore(PasswordStore instance) {
      SpawnState target = SpawnState.ACTIVE_SPAWNSTATE;
      String input = target.a(new Object[0]).toUpperCase(Locale.ENGLISH);

      try {
         directNoticeCatalog = DirectNoticeCatalog.valueOf(input);
      } catch (Throwable value) {
         PasswordHashContainer.performMessage(
            CachedSettingsGateway.loadState() ? "O tipo de database \"" + input + "\" não existe." : "The \"" + input + "\" database type does not exist."
         );
         directNoticeCatalog = DirectNoticeCatalog.valueOf((String)target.getObject());
      }

      if (directNoticeCatalog != DirectNoticeCatalog.DIRECT_NOTICE_CATALOG
         && directNoticeCatalog != DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG
         && directNoticeCatalog != DirectNoticeCatalog.CURRENT_DIRECTNOTICECATALOG) {
         PasswordHashContainer.performMessage(
            CachedSettingsGateway.loadState()
               ? "O tipo de database \"" + input + "\" existe, mas não é permitido."
               : "The database type \"" + input + "\" exists, but it is not allowed."
         );
         directNoticeCatalog = DirectNoticeCatalog.valueOf((String)target.getObject());
      }

      LocalSettingsRepository output = instance.fetchLocalSettingsRepository();

      try {
         if (output != null) {
            if (LocalPasswordTable.validateState(output.loadSharedListenerContract(), SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]), "last_address")) {
               handleSpawnState(SpawnState.VERIFIED_SPAWNSTATE, "last_address");
            }

            if (LocalPasswordTable.validateState(output.loadSharedListenerContract(), SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]), "last_login")) {
               handleSpawnState(SpawnState.AUTHENTICATED_SPAWNSTATE, "last_login");
            }
         }
      } catch (SQLException data) {
         PasswordHashContainer.handleMessage("Cannot verify if the database is using old \"last_address\" column", data);
      }
   }

   public static void handleSpawnState(SpawnState instance, Object target) {
      VerifiedPasswordHashHasher.executeInternalListenerContract(instance, instance.loadSecureLoginGate(), target);
   }

   public static boolean retrieveState() {
      return currentEnabled;
   }

   public static boolean isState(String instance, boolean target) {
      String input = (target ? QuickPremiumOption.PRIMARY_QUICKPREMIUMOPTION : QuickPremiumOption.REMOTE_QUICKPREMIUMOPTION).a(new Object[0]);
      boolean output = target ? activeEnabled : pendingEnabled;
      if (input.isEmpty()) {
         return true;
      } else if (input.length() >= instance.length()) {
         return false;
      } else {
         return output ? instance.endsWith(input) : instance.startsWith(input);
      }
   }

   public static void savePasswordStore(PasswordStore instance, boolean target) {
      String input = SpawnState.SPAWN_STATE.a(new Object[0]);
      if (!input.endsWith(".yml")) {
         handleSpawnState(SpawnState.SPAWN_STATE, input + ".yml");
      }

      SettingsLookup.executePasswordStore(instance, target);
   }

   public static void saveQuickPremiumOption(QuickPremiumOption instance, Object target) {
      VerifiedPasswordHashHasher.executeInternalListenerContract(instance, instance.loadSecureLoginGate(), target);
   }

   private static void dispatchPasswordStore(PasswordStore instance, boolean target) {
   }

   public static ProxyState findProxyState() {
      return activeProxyState;
   }

   private static void updatePasswordStore(PasswordStore instance) {
      ArrayList target = new ArrayList();
      ArrayList input = new ArrayList();
      QuickPremiumOption.LOCAL_QUICKPREMIUMOPTION.a(new Object[0]).forEach(targetValue -> {
         try {
            target.add(Pattern.compile(targetValue));
         } catch (PatternSyntaxException outputValue) {
            PasswordHashContainer.performMessage("Invalid pattern for premium domain \"" + targetValue + "\", please review your configuration");
         }
      });
      QuickPremiumOption.STORED_QUICKPREMIUMOPTION.a(new Object[0]).forEach(targetValue -> {
         try {
            input.add(Pattern.compile(targetValue));
         } catch (PatternSyntaxException outputValue) {
            PasswordHashContainer.performMessage("Invalid pattern for offline domain \"" + targetValue + "\", please review your configuration");
         }
      });
      entries = target;
      activeEntries = input;
      activeEnabled = checkState("premium", QuickPremiumOption.MAIN_QUICKPREMIUMOPTION);
      pendingEnabled = checkState("offline", QuickPremiumOption.CACHED_QUICKPREMIUMOPTION);
      String output = QuickPremiumOption.PRIMARY_QUICKPREMIUMOPTION.a(new Object[0]).trim();
      String context = QuickPremiumOption.REMOTE_QUICKPREMIUMOPTION.a(new Object[0]).trim();
      saveQuickPremiumOption(QuickPremiumOption.PRIMARY_QUICKPREMIUMOPTION, output);
      saveQuickPremiumOption(QuickPremiumOption.REMOTE_QUICKPREMIUMOPTION, context);
      if (activeEnabled == pendingEnabled && output.equals(context) || output.isEmpty() && context.isEmpty()) {
         saveQuickPremiumOption(QuickPremiumOption.PRIMARY_QUICKPREMIUMOPTION, QuickPremiumOption.PRIMARY_QUICKPREMIUMOPTION.getObject());
         saveQuickPremiumOption(QuickPremiumOption.MAIN_QUICKPREMIUMOPTION, QuickPremiumOption.MAIN_QUICKPREMIUMOPTION.getObject());
         saveQuickPremiumOption(QuickPremiumOption.REMOTE_QUICKPREMIUMOPTION, QuickPremiumOption.REMOTE_QUICKPREMIUMOPTION.getObject());
         saveQuickPremiumOption(QuickPremiumOption.CACHED_QUICKPREMIUMOPTION, QuickPremiumOption.CACHED_QUICKPREMIUMOPTION.getObject());
         if (QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
            PasswordHashContainer.performMessage("Premium and offline appendix cannot be the same! \"" + output + "\" = \"" + context + "\"");
         }
      }

      if (instance.findIncomingLoginGate().retrieveSilentProxyState() == SilentProxyState.SILENT_PROXY_STATE) {
         YamlConfiguration data = BungeeWriter.findYamlConfiguration();
         if (data != null && data.getBoolean("unsupported-settings.perform-username-validation", false)) {
            PasswordHashContainer.performMessage("The \"%s\" option is enabled in config/paper-global.yml", "unsupported-settings.perform-username-validation");
            PasswordHashContainer.performMessage("The username-appender feature may fail with an invalid nickname kick message.");
         }
      }
   }

   public static DirectNoticeCatalog getDirectNoticeCatalog() {
      return directNoticeCatalog;
   }

   public static byte[] resolvePayload() {
      return values;
   }

   private static boolean checkState(String instance, QuickPremiumOption target) {
      String input = target.a(new Object[0]);
      switch (input.toLowerCase(Locale.ENGLISH)) {
         case "prefix":
            return false;
         case "suffix":
            return true;
         default:
            PasswordHashContainer.performMessage("Invalid position for " + instance + " appendix: \"" + input + "\", using default value.");
            return true;
      }
   }

   private static void dispatchPasswordStore(PasswordStore instance) {
      PasswordHashLoader target = instance.a();
      String input = target.a("database.SQLITE.database-filename", target.a("Database.SQLITE.database-filename", "accounts.db"));
      if (!input.endsWith(".db")) {
         input = input + ".db";
      }

      File output = new File(instance.resolveFile(), "database");
      if (output.exists()) {
         File context = new File(output, input);
         File data = new File(instance.resolveFile(), "nlogin.db");
         if (!data.exists() && context.exists() && !context.renameTo(data)) {
            PasswordHashContainer.updateMessage("Unable to migrate " + input + " to nlogin.db!");
         }

         String[] value = output.list();
         if (value != null && value.length == 0 && !output.delete()) {
            PasswordHashContainer.updateMessage("Unable to delete `database` folder!");
         }
      }
   }

   private static void processTask() {
      handleSpawnState(SpawnState.READY_SPAWNSTATE, SpawnState.READY_SPAWNSTATE.b(new Object[0]).stream().map(String::toLowerCase).collect(Collectors.toList()));
      handleSpawnState(
         SpawnState.LIVE_SPAWNSTATE, SpawnState.LIVE_SPAWNSTATE.b(new Object[0]).stream().map(LocalLocaleFlow::handleMessage).collect(Collectors.toList())
      );
   }

   public static UpstreamLoginOption resolveUpstreamLoginOption() {
      return upstreamLoginOption;
   }
}
