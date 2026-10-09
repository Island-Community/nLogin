package com.nickuc.login.account;

import com.nickuc.login.auth.password.IndirectPasswordCheckpoint;
import com.nickuc.login.auth.password.PasswordBarrier;
import com.nickuc.login.auth.password.PasswordFlow;
import com.nickuc.login.auth.password.PasswordProcessor;
import com.nickuc.login.premium.FastSettingsResolver;
import com.nickuc.login.premium.LimboVerifier;
import com.nickuc.login.premium.LinkedPasswordVerifier;
import com.nickuc.login.premium.LocaleProvider;
import com.nickuc.login.premium.LoudPasswordResolver;
import com.nickuc.login.premium.PasswordLookup;
import com.nickuc.login.premium.QuickPasswordResolver;
import com.nickuc.login.premium.RootPasswordLookup;
import com.nickuc.login.premium.SafePasswordLookup;
import com.nickuc.login.premium.SecurePasswordLookup;
import com.nickuc.login.security.hashing.PasswordProvider;
import com.nickuc.login.storage.password.BusyPasswordRepository;
import com.nickuc.login.storage.settings.BusySettingsTable;
import com.nickuc.login.storage.login.CachedLoginTable;
import com.nickuc.login.storage.settings.CachedSettingsRepository;
import com.nickuc.login.storage.settings.CurrentSettingsGateway;
import com.nickuc.login.storage.password.DeadPasswordRepository;
import com.nickuc.login.storage.settings.DeadSettingsRepository;
import com.nickuc.login.storage.settings.FastSettingsTable;
import com.nickuc.login.storage.settings.InternalSettingsTable;
import com.nickuc.login.storage.login.LocalLoginCollection;
import com.nickuc.login.storage.login.LoginRepository;
import com.nickuc.login.storage.password.MainPasswordDao;
import com.nickuc.login.storage.password.PasswordHashDao;
import com.nickuc.login.storage.password.PasswordHashRepository;
import com.nickuc.login.storage.password.PasswordSource;
import com.nickuc.login.storage.login.PendingLoginTable;
import com.nickuc.login.storage.password.PendingPasswordRepository;
import com.nickuc.login.storage.login.PrimaryLoginTable;
import com.nickuc.login.storage.password.SafePasswordGateway;
import com.nickuc.login.storage.password.SafePasswordRepository;
import com.nickuc.login.storage.settings.SecondarySettingsSource;
import com.nickuc.login.storage.settings.SettingsArchive;
import com.nickuc.login.storage.settings.SettingsCollection;
import com.nickuc.login.storage.settings.SettingsSource;
import com.nickuc.login.storage.converter.DeadPasswordConverter;
import com.nickuc.login.storage.converter.PasswordAdapter;
import com.nickuc.login.storage.converter.PasswordConverter;
import com.nickuc.login.storage.converter.PasswordImporter;
import com.nickuc.login.storage.converter.Sha256Adapter;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.Nullable;


public enum MessageOption {
   MESSAGE_OPTION("AuthMe", "AuthMe", PasswordAdapter.class, true),
   ACTIVE_MESSAGEOPTION("LoginSecurity", "LoginSecurity", DeadPasswordRepository.class, true),
   PENDING_MESSAGEOPTION("AtlasLogin", "AtlasLogin", QuickPasswordResolver.class, true),
   CURRENT_MESSAGEOPTION("AtlasLobby", "AtlasLobby", PendingPasswordRepository.class, true),
   PRIMARY_MESSAGEOPTION("OpeNLogin", "OpeNLogin", IndirectPasswordCheckpoint.class, true),
   MAIN_MESSAGEOPTION("LockLogin", "LockLogin", PasswordProcessor.class, true),
   LOCAL_MESSAGEOPTION("BaronessAuth", "BaronessAuth", SafePasswordLookup.class, true),
   REMOTE_MESSAGEOPTION("JPremium", "JPremium", PasswordLookup.class, true),
   CACHED_MESSAGEOPTION("LimboAuth", "LimboAuth", LimboVerifier.class, true),
   STORED_MESSAGEOPTION("LightLogin", "LightLogin", PasswordConverter.class, true),
   VERIFIED_MESSAGEOPTION("LibreLogin", "LibreLogin", RootPasswordLookup.class, true),
   AUTHENTICATED_MESSAGEOPTION("PixelLogin", "PixelLogin", SafePasswordRepository.class, true),
   SHARED_MESSAGEOPTION("SimpleLogin", "SimpleLogin", PasswordBarrier.class),
   PRIVATE_MESSAGEOPTION("DynamicBungeeAuth", "DynamicBungeeAuth", LinkedPasswordVerifier.class, true),
   INTERNAL_MESSAGEOPTION("tiAuth", "tiAuth", SecurePasswordLookup.class, true),
   UPSTREAM_MESSAGEOPTION("NavAuth", "NavAuth", DeadPasswordConverter.class, true),
   INCOMING_MESSAGEOPTION("Aegis", "Aegis", LoudPasswordResolver.class, true),
   OUTGOING_MESSAGEOPTION("Authy", "Authy", SafePasswordGateway.class, true),
   SECONDARY_MESSAGEOPTION("MineLogin", "mineLogin", PasswordSource.class, true),
   DIRECT_MESSAGEOPTION("CrazyLogin", "CrazyLogin", BusyPasswordRepository.class, true),
   LINKED_MESSAGEOPTION("UserLogin", "UserLogin", MainPasswordDao.class, true),
   ROOT_MESSAGEOPTION("StormLogin", "StormLogin", Sha256Adapter.class, true),
   TOP_MESSAGEOPTION("MambaLogin", "Login", SecondarySettingsSource.class, true),
   FAST_MESSAGEOPTION("TLogin", "TLogin", PasswordImporter.class),
   SAFE_MESSAGEOPTION("AshLogin", "AshLogin", PrimaryLoginTable.class),
   SECURE_MESSAGEOPTION("AdvancedLogin", "AdvancedLogin", CurrentSettingsGateway.class),
   OPEN_MESSAGEOPTION("NexAuth", "NexAuth", PasswordProvider.class, true),
   READY_MESSAGEOPTION("LoginPlus", "LoginPlus", PasswordHashDao.class, true),
   LIVE_MESSAGEOPTION("TGLogin", "TGLogin", SettingsArchive.class, true),
   ACTIVE_PENDING_MESSAGEOPTION("NtAuth", "NtAuth", PasswordFlow.class),
   ACTIVE_CURRENT_MESSAGEOPTION("gLogin", "gLogin", LocalLoginCollection.class),
   ACTIVE_PRIMARY_MESSAGEOPTION("LoginSeguro", "LoginSeguro", SettingsCollection.class),
   ACTIVE_MAIN_MESSAGEOPTION("PirateLogin", "PirateLogin", BusySettingsTable.class),
   ACTIVE_LOCAL_MESSAGEOPTION("PrimeLogin", "PrimeLogin", CachedLoginTable.class),
   ACTIVE_REMOTE_MESSAGEOPTION("uLogin", "uLogin", SettingsSource.class),
   ACTIVE_CACHED_MESSAGEOPTION("utLogin", "utLogin", FastSettingsTable.class),
   ACTIVE_STORED_MESSAGEOPTION("zLogin", "HeavyAuth", InternalSettingsTable.class),
   ACTIVE_VERIFIED_MESSAGEOPTION("InsaneLogin", "InsanePluginsLOGIN", CachedSettingsRepository.class),
   ACTIVE_AUTHENTICATED_MESSAGEOPTION("sLogin", "sLogin", DeadSettingsRepository.class),
   ACTIVE_SHARED_MESSAGEOPTION("FastLogin", "FastLogin", FastSettingsResolver.class),
   ACTIVE_PRIVATE_MESSAGEOPTION("Nyx", "Nyx", LocaleProvider.class),
   ACTIVE_INTERNAL_MESSAGEOPTION("SQLiteToMySQL", null, LoginRepository.class, "SQLite", "MySQL", false),
   ACTIVE_UPSTREAM_MESSAGEOPTION("MySQLToSQLite", null, PendingLoginTable.class, "MySQL", "SQLite", false);

   private static final Collection<MessageOption> collection = Arrays.stream(values())
      .sorted((instance, target) -> instance.name.compareToIgnoreCase(target.name))
      .collect(Collectors.toList());
   private final String name;
   private final String activeName;
   private final Class<? extends PasswordHashRepository> value;
   private final String pendingName;
   private final String currentName;
   private final boolean enabled;

   MessageOption(String output, String context, Class<? extends PasswordHashRepository> data, boolean value) {
      this(output, context, data, output, "nLogin", value);
   }

   public static Collection<MessageOption> findCollection() {
      return collection;
   }

   public String resolveMessage() {
      return this.activeName;
   }

   @Nullable
   public static MessageOption processMessageOption(String instance) {
      for (MessageOption context : values()) {
         if (context.name.equalsIgnoreCase(instance)) {
            return context;
         }
      }

      return null;
   }

   MessageOption(String output, String context, Class<? extends PasswordHashRepository> data, String value, String result, boolean request) {
      this.name = output;
      this.activeName = context;
      this.value = data;
      this.pendingName = value;
      this.currentName = result;
      this.enabled = request;
   }

   public boolean resolveState() {
      return this == ACTIVE_INTERNAL_MESSAGEOPTION || this == ACTIVE_UPSTREAM_MESSAGEOPTION;
   }

   public PasswordHashRepository retrievePasswordHashRepository() {
      return PasswordHashRepository.loadSet().stream().filter(target -> target.messageOption == this).findFirst().orElse(null);
   }

   MessageOption(String output, String context, Class<? extends PasswordHashRepository> data) {
      this(output, context, data, output, "nLogin", false);
   }

   public String getName() {
      return this.name;
   }
}
