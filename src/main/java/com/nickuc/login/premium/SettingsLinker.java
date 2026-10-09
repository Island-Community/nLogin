package com.nickuc.login.premium;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.SharedLoginOption;
import com.nickuc.login.account.SpawnOption;
import com.nickuc.login.api.enums.LoginType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.auth.login.PrimaryLoginService;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.player.LoudPlayerContract;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.platform.account.UpstreamAccountHandler;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.PacketCoordinator;
import com.nickuc.login.session.ParentLimboTracker;
import com.nickuc.login.spawn.PremiumOption;
import com.nickuc.login.storage.session.ActiveSessionRepository;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import javax.annotation.Nullable;


public abstract class SettingsLinker {
   private final ParentLimboTracker parentLimboTracker;
   private final PacketCoordinator packetCoordinator;
   private static float factor = Float.intBitsToFloat(1097859072);
   private final ActiveSessionRepository activeSessionRepository;
   private static float activeFactor = Float.intBitsToFloat(1077936128);
   public final PasswordStore passwordStore;

   public void processSpawnLookup(
      SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output, String context, @Nullable String data, boolean value, boolean result
   ) {
      if (this.isState(output, "Register", result)) {
         if (this.isState(target, input, output, context, data, LoudProxyState.REMOTE_LOUDPROXYSTATE, SharedLoginOption.CURRENT_SHAREDLOGINOPTION, LoginType.REGISTER)) {
            String request = input.getName();
            PasswordHashContainer.processMessage(
               CachedSettingsGateway.loadState() ? "O usuário " + request + " se registrou com sucesso." : "The user " + request + " has successfully registered."
            );
         }
      }
   }

   public SettingsLinker(PasswordStore target) {
      this.passwordStore = target;
      this.packetCoordinator = new PacketCoordinator(target, this);
      this.parentLimboTracker = new ParentLimboTracker(target, this);
      this.activeSessionRepository = new ActiveSessionRepository(target);
   }

   public void saveSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (this.isState(output, "Session login", false)) {
         if (this.isState(target, input, output, null, null, LoudProxyState.CACHED_LOUDPROXYSTATE, SharedLoginOption.CACHED_SHAREDLOGINOPTION, LoginType.SESSION)) {
            this.passwordStore.verifyState(EventEnum.SESSION_LOGIN_EVENT, input);
            String context = input.getName();
            PasswordHashContainer.processMessage(
               CachedSettingsGateway.loadState()
                  ? "O usuário " + context + " logou automaticamente (sessão de login)"
                  : "The user " + context + " logged in automatically (login session)"
            );
         }
      }
   }

   private boolean isState(
      SpawnLookup target,
      VerifiedServerAdapter input,
      LimboCoordinator output,
      String context,
      @Nullable String data,
      @Nullable LoudProxyState value,
      @Nullable SharedLoginOption result,
      LoginType request
   ) {
      long response = System.nanoTime();

      try {
         switch (request) {
            case REGISTER:
               if (!this.passwordStore.verifyState(EventEnum.REGISTER_EVENT, input, context)) {
                  return false;
               }

               String entry = output.loadMessage();
               if (!this.passwordStore.findIndirectPasswordResolver().checkState(target, entry, context, data, input.resolveMessage())) {
                  CachedSettingsGateway.executeOutgoingSenderAdapter(input, LoudProxyState.DIRECT_LOUDPROXYSTATE);
                  CachedSettingsGateway.processOutgoingSenderAdapter(input, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                  return false;
               }
               break;
            case LOGIN:
               if (!this.passwordStore.verifyState(EventEnum.LOGIN_EVENT, input, context)) {
                  return false;
               }
         }

         output.executeTightPlatformCatalog(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG, TightPlatformCatalog.PRIMARY_TIGHTPLATFORMCATALOG);
         output.updateLenientMessageKind(LenientMessageKind.SECONDARY_LENIENTMESSAGEKIND, request);
         output.updateLenientMessageKind(LenientMessageKind.FAST_LENIENTMESSAGEKIND, true);
         if (context != null) {
            output.updateLenientMessageKind(LenientMessageKind.LINKED_LENIENTMESSAGEKIND, context);
         }

         output.loadObject(LenientMessageKind.SECURE_LENIENTMESSAGEKIND);
         CachedSettingsGateway.processOutgoingSenderAdapter(input, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
         input.executeTask();
         input.handleMessage("");
         if (value != null) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(input, value);
         }

         if (result != null) {
            CachedSettingsGateway.updateVerifiedServerAdapter(input, result);
         }

         LoudPlayerContract reference = null;

         try {
            reference = this.activeSessionRepository.computeLoudPlayerContract(input, output);
         } catch (Exception payload) {
            PasswordHashContainer.handleMessage("Unable to send notifications after authenticating", payload);
         }

         if (!(reference instanceof UpstreamAccountHandler)) {
            this.parentLimboTracker.saveSpawnLookup(target, input, output, request, context);
         }

         return true;
      } finally {
         PrimaryLoginService.savePremiumOption(request == LoginType.REGISTER ? PremiumOption.REMOTE_PREMIUMOPTION : PremiumOption.LOCAL_PREMIUMOPTION, response);
         this.executeLimboCoordinator(output);
      }
   }

   public void sendSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output, String context, boolean data, boolean value) {
      if (this.isState(output, "Login", value)) {
         if (this.isState(
            target,
            input,
            output,
            context,
            null,
            data ? LoudProxyState.LOCAL_LOUDPROXYSTATE : null,
            data ? SharedLoginOption.PENDING_SHAREDLOGINOPTION : null,
            LoginType.LOGIN
         )) {
            String result = input.getName();
            PasswordHashContainer.processMessage(
               CachedSettingsGateway.loadState() ? "O usuário " + result + " se autenticou com sucesso." : "The user " + result + " has successfully logged in."
            );
         }
      }
   }

   public PacketCoordinator retrievePacketCoordinator() {
      return this.packetCoordinator;
   }

   public ParentLimboTracker resolveParentLimboTracker() {
      return this.parentLimboTracker;
   }

   public abstract void executeVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input);

   private void executeLimboCoordinator(LimboCoordinator target) {
      synchronized (target.object) {
         if (target.loadTightPlatformCatalog() == TightPlatformCatalog.PRIMARY_TIGHTPLATFORMCATALOG) {
            target.executeTightPlatformCatalog(TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG, TightPlatformCatalog.PRIMARY_TIGHTPLATFORMCATALOG);
         }
      }
   }

   public abstract void saveVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, boolean output);

   public ActiveSessionRepository fetchActiveSessionRepository() {
      return this.activeSessionRepository;
   }

   public void saveSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, boolean output, boolean context) {
      if (input != null && input.loadState()) {
         LimboCoordinator data = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(input);
         synchronized (data.object) {
            TightPlatformCatalog result = data.loadTightPlatformCatalog();
            if (!data.loadTightPlatformCatalog().isState(TightPlatformCatalog.PRIMARY_TIGHTPLATFORMCATALOG)) {
               if (context) {
                  data.updateLenientMessageKind(LenientMessageKind.ACTIVE_PENDING_LENIENTMESSAGEKIND, true);
               }

               if (result == TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG) {
                  this.passwordStore.processLinkedSessionHandler(true).buildStrictCommandHandler(() -> {
                     if (input.loadState()) {
                        if (!data.loadTightPlatformCatalog().hasState(TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG)) {
                           PasswordHashContainer.dispatchMessage("Forcing " + input.getName() + " login...");
                           this.sendSpawnLookup(target, input, data, null, output, false);
                        }
                     }
                  });
               } else {
                  data.updateLenientMessageKind(LenientMessageKind.LIVE_LENIENTMESSAGEKIND, output);
               }
            }
         }
      }
   }

   public void executeSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (this.isState(output, "Premium login", false)) {
         SpawnOption context = target.loadSpawnOption();
         if (context != SpawnOption.SPAWN_OPTION) {
            throw new IllegalStateException("Account \"" + input.getName() + "\" is not marked as premium! current type = " + context);
         }

         if (!output.d(LenientMessageKind.PENDING_LENIENTMESSAGEKIND)) {
            throw new IllegalStateException("Trying to log in automatically via premium account with an unencrypted connection! player = " + input.getName());
         }

         if (this.isState(target, input, output, null, null, LoudProxyState.STORED_LOUDPROXYSTATE, SharedLoginOption.STORED_SHAREDLOGINOPTION, LoginType.PREMIUM)) {
            this.passwordStore.verifyState(EventEnum.PREMIUM_LOGIN_EVENT, input);
            String data = input.getName();
            PasswordHashContainer.processMessage(
               CachedSettingsGateway.loadState()
                  ? "O usuário " + data + " logou automaticamente (conta original)"
                  : "The user " + data + " logged in automatically (premium account)"
            );
         }
      }
   }

   public void processSpawnLookup(SpawnLookup target, VerifiedServerAdapter input, LimboCoordinator output) {
      if (this.isState(output, "Bedrock login", false)) {
         SpawnOption context = target.loadSpawnOption();
         if (context != SpawnOption.PENDING_SPAWNOPTION) {
            throw new IllegalStateException("Account \"" + input.getName() + "\" is not registered as bedrock! current type = " + context);
         }

         SecondaryConnectionContract data = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
         if (data != null && output.d(LenientMessageKind.PRIMARY_LENIENTMESSAGEKIND)) {
            if (this.isState(
               target, input, output, null, null, LoudProxyState.VERIFIED_LOUDPROXYSTATE, SharedLoginOption.VERIFIED_SHAREDLOGINOPTION, LoginType.BEDROCK
            )) {
               this.passwordStore.verifyState(EventEnum.BEDROCK_LOGIN_EVENT, input);
               String value = input.getName();
               PasswordHashContainer.processMessage(
                  CachedSettingsGateway.loadState()
                     ? "O usuário " + value + " logou automaticamente (conta Bedrock)"
                     : "The user " + value + " logged in automatically (Bedrock account)"
               );
            }
         } else {
            throw new IllegalStateException("Trying to log in automatically via Bedrock account with a Java connection! player = " + input.getName());
         }
      }
   }

   public boolean isState(LimboCoordinator target, String input, boolean output) {
      synchronized (target.object) {
         if (output && target.loadTightPlatformCatalog() != TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG) {
            return false;
         }

         target.executeTightPlatformCatalog(TightPlatformCatalog.PRIMARY_TIGHTPLATFORMCATALOG, TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG);
         return true;
      }
   }
}
