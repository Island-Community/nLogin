package com.nickuc.login.mail;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.api.enums.DatabaseType;
import com.nickuc.login.api.enums.ImplementationType;
import com.nickuc.login.api.enums.event.ChangePasswordSource;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.enums.event.UnregisterSource;
import com.nickuc.login.api.enums.event.UpdatePasswordSource;
import com.nickuc.login.api.exception.nLoginNotReadyException;
import com.nickuc.login.api.types.AccountData;
import com.nickuc.login.api.types.AccountDataImpl;
import com.nickuc.login.api.types.Identity;
import com.nickuc.login.auth.login.BusyLoginHandler;
import com.nickuc.login.auth.mojang.MojangProcessor;
import com.nickuc.login.auth.mojang.MojangService;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.auth.login.RemoteLoginBarrier;
import com.nickuc.login.auth.login.RemoteLoginProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.UpstreamSpawnState;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.storage.account.AccountGateway;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.settings.LocalSettingsRepository;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public abstract class PasswordHandler implements nLoginAPI {
   private final Cache<String, SpawnLookup> cache = Caffeine.newBuilder().expireAfterWrite(3L, TimeUnit.SECONDS).build();
   public final PasswordStore passwordStore;
   public static final int count = 11;

   public void dispatchTask() {
      if (!this.isAvailable()) {
         throw new nLoginNotReadyException("nLoginAPI is not fully started, please wait to use api");
      }
   }

   public void handleMessage(String target, String input) {
      if (target == null) {
         throw new IllegalArgumentException(input + " cannot be null!");
      }

      if (target.isEmpty()) {
         throw new IllegalArgumentException(input + " cannot be empty!");
      }
   }

   public boolean isAuthenticated(@Nonnull Identity target) {
      this.updateIdentity(target);
      this.dispatchTask();
      String input = this.createMessage(target);
      return this.isAuthenticated(input);
   }

   public DatabaseType getDatabaseType() {
      this.dispatchTask();
      return DatabaseType.valueOf(this.passwordStore.fetchLocalSettingsRepository().loadSharedListenerContract().resolveDirectNoticeCatalog().name());
   }

   @Nonnull
   public String getVersion() {
      return this.passwordStore.getMessage();
   }

   public boolean isAvailable() {
      return !this.passwordStore.findState();
   }

   public boolean setEmail(@Nonnull Identity target, @Nullable String input) {
      if (input != null) {
         this.handleMessage(input, "Email");
      }

      this.dispatchTask();
      SpawnLookup output = this.buildSpawnLookup(target);
      synchronized (output.object) {
         if (output.fetchState()) {
            output.fetchQuickDiscordHandler().saveMessage(input);
            return this.passwordStore.findIndirectPasswordResolver().isState(output, OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE);
         } else {
            return false;
         }
      }
   }

   public boolean performRegister(@Nonnull Identity target, @Nonnull String input, @Nullable String output) {
      this.handleMessage(input, "Password");
      if (output != null) {
         this.handleMessage(output, "IP Address");
      }

      this.dispatchTask();
      SpawnLookup context = this.buildSpawnLookup(target);
      synchronized (context.object) {
         return !context.fetchState() ? this.passwordStore.findIndirectPasswordResolver().checkState(context, this.handleMessage(target), input, null, output) : false;
      }
   }

   @CheckReturnValue
   public SpawnLookup buildSpawnLookup(Identity target) {
      String input = this.createMessage(target);
      VerifiedServerAdapter output = this.passwordStore.b().resolveVerifiedServerAdapter(input);
      return output != null ? this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output).loadSpawnLookup() : (SpawnLookup)this.cache.get(input, outputValue -> {
         IndirectPasswordResolver context = this.passwordStore.findIndirectPasswordResolver();
         SpawnLookup data;
         if (target instanceof RemoteLoginProcessor) {
            RemoteLoginProcessor value = (RemoteLoginProcessor)target;
            data = context.processSpawnLookup(value.getKnownName());
         } else {
            if (!(target instanceof MojangService)) {
               throw new IllegalArgumentException("Unsupported identity type! " + target.getClass().getCanonicalName());
            }

            MojangService result = (MojangService)target;
            data = context.computeSpawnLookup(result.getName(), result.getMojangId(), result.getBedrockId(), true);
         }

         if (data == null) {
            throw new RuntimeException("Unable to load the account of player \"" + input + "\".");
         } else {
            return data;
         }
      });
   }

   public int getApiVersion() {
      return 11;
   }

   public int getRemainingSeconds(@Nonnull Identity target) {
      this.updateIdentity(target);
      this.dispatchTask();
      String input = this.createMessage(target);
      VerifiedServerAdapter output = this.passwordStore.b().resolveVerifiedServerAdapter(input);
      if (output != null) {
         LimboCoordinator context = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
         Integer data = context.findInteger();
         if (data != null) {
            return data;
         }
      }

      return -1;
   }

   public boolean setDiscord(@Nonnull Identity target, long input) {
      this.dispatchTask();
      SpawnLookup context = this.buildSpawnLookup(target);
      synchronized (context.object) {
         if (context.fetchState()) {
            context.fetchQuickDiscordHandler().dispatchMessage(input > 0L ? Long.toString(input) : null);
            return this.passwordStore.findIndirectPasswordResolver().isState(context, OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE);
         } else {
            return false;
         }
      }
   }

   public boolean setLanguage(@Nonnull Identity target, @Nullable String input) {
      this.dispatchTask();
      if (input == null || !input.isEmpty() && input.length() <= 5) {
         SpawnLookup output = this.buildSpawnLookup(target);
         synchronized (output.object) {
            if (input != null) {
               output.resolveCachedPasswordHashHasher().updateMessage("language", input);
            } else {
               output.resolveCachedPasswordHashHasher().performMessage("language");
            }

            String data = this.createMessage(target);
            VerifiedServerAdapter value = this.passwordStore.b().resolveVerifiedServerAdapter(data);
            if (value != null) {
               LimboCoordinator result = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(value);
               LenientPremiumOption request = LenientPremiumOption.buildLenientPremiumOption(input);
               if (request != null) {
                  result.updateLenientMessageKind(LenientMessageKind.CACHED_LENIENTMESSAGEKIND, request);
               }
            }

            return !output.resolveCachedPasswordHashHasher().getState()
               || this.passwordStore.findIndirectPasswordResolver().isState(output, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE);
         }
      } else {
         throw new IllegalStateException("Language code cannot be empty or longer than 5 characters!");
      }
   }

   public boolean comparePassword(AccountData target, String input) {
      this.handleMessage(input, "Plain password");
      this.dispatchTask();
      AccountDataImpl output = this.resolveAccountDataImpl(target);
      return output.getHashedPassword().map(targetValue -> {
         UpstreamSpawnState inputValue = UpstreamSpawnState.createUpstreamSpawnState(targetValue);
         return inputValue != null && inputValue.loadIndirectPlayerContract().verifyState(input, targetValue);
      }).orElse(false);
   }

   public Optional<AccountData> getAccount(@Nonnull Identity target) {
      this.updateIdentity(target);
      this.dispatchTask();
      SpawnLookup input = this.buildSpawnLookup(target);
      return input.fetchState() ? Optional.of(MojangProcessor.from(input)) : Optional.empty();
   }

   public String createMessage(Identity target) {
      if (target instanceof RemoteLoginProcessor) {
         RemoteLoginProcessor value = (RemoteLoginProcessor)target;
         return value.getKnownName();
      }

      if (target instanceof MojangService) {
         MojangService input = (MojangService)target;
         String output = input.getName();
         int context = input.getMojangId() != null ? 1 : 0;
         String data;
         if (QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState() && !Pbkdf2Linker.isState(output, (boolean)context)) {
            data = Pbkdf2Linker.handleMessage(output, (boolean)context);
         } else {
            data = output;
         }

         return data;
      } else {
         throw new IllegalArgumentException("Unsupported identity type! " + target.getClass().getCanonicalName());
      }
   }

   public PasswordHandler(PasswordStore target) {
      this.passwordStore = target;
   }

   public boolean isAuthenticated(@Nonnull String target) {
      VerifiedServerAdapter input = this.passwordStore.b().resolveVerifiedServerAdapter(target);
      return input != null && this.passwordStore.loadLimboRegistry().canState(input);
   }

   public boolean performUnregister(@Nonnull Identity target) {
      this.dispatchTask();
      SpawnLookup input = this.buildSpawnLookup(target);
      if (input.fetchState()) {
         String output = this.createMessage(target);
         VerifiedServerAdapter context = this.passwordStore.b().resolveVerifiedServerAdapter(output);
         UUID data = context != null ? context.getUniqueId() : input.getUniqueId();
         if (this.passwordStore.verifyState(EventEnum.UNREGISTER, null, data, input.retrieveMessage(), UnregisterSource.BY_API)) {
            synchronized (input.object) {
               if (this.passwordStore.findIndirectPasswordResolver().canState(input)) {
                  PasswordHashContainer.dispatchMessage("The account of " + input.retrieveMessage() + " player was unregistered via API");

                  try {
                     this.passwordStore.verifyState(EventEnum.PASSWORD_UPDATE_EVENT, context, data, output, null, UpdatePasswordSource.BY_API);
                  } finally {
                     if (context != null) {
                        context.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.PRIVATE_LOUDPROXYSTATE, context));
                     }
                  }

                  return true;
               }
            }
         }
      }

      return false;
   }

   public void updateIdentity(Identity target) {
      if (target == null) {
         throw new IllegalArgumentException("Identity cannot be null!");
      }

      if (!(target instanceof BusyLoginHandler)) {
         throw new IllegalArgumentException(
            "Identity is not an instance of " + BusyLoginHandler.class.getCanonicalName() + " class! " + target.getClass().getCanonicalName()
         );
      }
   }

   public AccountDataImpl resolveAccountDataImpl(AccountData target) {
      if (target == null) {
         throw new IllegalArgumentException("Account data cannot be null!");
      } else if (!(target instanceof AccountDataImpl)) {
         throw new IllegalArgumentException(
            "Account data is not an instance of " + AccountDataImpl.class.getCanonicalName() + " class! " + target.getClass().getCanonicalName()
         );
      } else {
         return (AccountDataImpl)target;
      }
   }

   public boolean changePassword(@Nonnull Identity target, @Nonnull String input) {
      this.handleMessage(input, "New password");
      this.dispatchTask();
      SpawnLookup output = this.buildSpawnLookup(target);
      if (output.fetchState()) {
         String context = this.createMessage(target);
         VerifiedServerAdapter data = this.passwordStore.b().resolveVerifiedServerAdapter(context);
         UUID value = data != null ? data.getUniqueId() : output.getUniqueId();
         if (this.passwordStore.verifyState(EventEnum.CHANGE_PASSWORD, data, value, context, ChangePasswordSource.BY_API)) {
            synchronized (output.object) {
               if (this.passwordStore.findIndirectPasswordResolver().validateState(output, input)) {
                  PasswordHashContainer.dispatchMessage("The password of " + output.retrieveMessage() + " player was changed via API");
                  this.passwordStore.verifyState(EventEnum.PASSWORD_UPDATE_EVENT, data, value, context, input, UpdatePasswordSource.BY_API);
                  if (data != null) {
                     CachedSettingsGateway.executeOutgoingSenderAdapter(data, LoudProxyState.AUTHENTICATED_LOUDPROXYSTATE);
                  }

                  return true;
               }
            }
         }
      }

      return false;
   }

   public long getAccountCount() {
      this.dispatchTask();
      LocalSettingsRepository target = this.passwordStore.fetchLocalSettingsRepository();
      return target.getTime();
   }

   public boolean forceLogin(@Nonnull Identity target, boolean input) {
      this.dispatchTask();
      SpawnLookup output = this.buildSpawnLookup(target);
      if (output.fetchState()) {
         String context = this.createMessage(target);
         VerifiedServerAdapter data = this.passwordStore.b().resolveVerifiedServerAdapter(context);
         if (data != null) {
            this.passwordStore.findSettingsLinker().saveSpawnLookup(output, data, input, true);
            return true;
         }
      }

      return false;
   }

   @Nonnull
   public Iterator<AccountData> getAccounts() {
      this.dispatchTask();
      LocalSettingsRepository target = this.passwordStore.fetchLocalSettingsRepository();
      return new AccountGateway(this.passwordStore.findIndirectPasswordResolver(), target.loadSharedListenerContract(), target.getTime());
   }

   @Nonnull
   public List<AccountData> getAccountsByIp(@Nonnull String target) {
      this.handleMessage(target, "Address");
      this.dispatchTask();
      ArrayList input = new ArrayList();

      try {
         PrimaryLoginHandler output = this.passwordStore
            .fetchLocalSettingsRepository()
            .loadSharedListenerContract()
            .buildPrimaryLoginHandler(
               String.format(
                  "SELECT * FROM `%s` WHERE `%s` = ?", SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]), OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()
               ),
               target
            );

         try {
            ResultSet context = output.resolveObject();

            while (context.next()) {
               SpawnLookup data = this.passwordStore.findIndirectPasswordResolver().processSpawnLookup(context);
               if (data == null) {
                  throw new RuntimeException("Cannot fetch account");
               }

               input.add(MojangProcessor.from(data));
            }
         } catch (Throwable result) {
            if (output != null) {
               try {
                  output.close();
               } catch (Throwable value) {
                  result.addSuppressed(value);
               }
            }

            throw result;
         }

         if (output != null) {
            output.close();
         }
      } catch (Exception request) {
         PasswordHashContainer.handleMessage("Unable to load the user's IP: " + target, request);
      }

      return RemoteLoginBarrier.createRemoteLoginBarrier(input);
   }

   @Nonnull
   public ImplementationType getImplementationType() {
      return ImplementationType.NATIVE;
   }

   public String handleMessage(Identity target) {
      String input;
      if (target instanceof RemoteLoginProcessor) {
         RemoteLoginProcessor output = (RemoteLoginProcessor)target;
         input = output.getKnownName();
      } else {
         if (!(target instanceof MojangService)) {
            throw new IllegalArgumentException("Unsupported identity type! " + target.getClass().getCanonicalName());
         }

         MojangService context = (MojangService)target;
         input = context.getName();
      }

      String data;
      if ((data = Pbkdf2Linker.loadMessage(input, true)) != null) {
         return data;
      } else {
         return (data = Pbkdf2Linker.loadMessage(input, false)) != null ? data : input;
      }
   }
}
