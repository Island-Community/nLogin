package com.nickuc.login.storage.locale;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.auth.settings.CachedSettingsProcessor;
import com.nickuc.login.discord.DiscordNotifier;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.sender.SecondarySenderAdapter;
import com.nickuc.login.platform.server.ServerAdapter;
import com.nickuc.login.premium.FloodgateResolver;
import com.nickuc.login.premium.IndirectPasswordHashVerifier;
import com.nickuc.login.premium.LowLoginResolver;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SettingsLookup;
import com.nickuc.login.protocol.PasswordHashAdapter;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.session.LowLimboTracker;
import com.nickuc.login.tasks.LoginMainQueueTask;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.charset.StandardCharsets;
import java.util.Locale;


public abstract class OpenLocaleGateway implements ServerAdapter {
   private final IndirectSessionHandler<?> indirectSessionHandler;

   private static Object resolveObject(Lookup instance, String target, MethodType input) {
      try {
         return new MutableCallSite(
            instance.findStatic(
                  OpenLocaleGateway.class,
                  new String(new byte[]{97}, StandardCharsets.UTF_8),
                  MethodType.fromMethodDescriptorString("(IJ)Ljava/lang/String;", OpenLocaleGateway.class.getClassLoader())
               )
               .asType(input)
         );
      } catch (Exception context) {
         throw new RuntimeException("com/nickuc/login/ΩδψψλοτωΨΣπμζΩΠ:" + target + ":" + input.toString(), context);
      }
   }

   @Override
   public FloodgateResolver fetchFloodgateResolver() {
      return new LowLimboTracker(
         this.indirectSessionHandler.findDiscordNotifier(), this.indirectSessionHandler.<DiscordNotifier>findDiscordNotifier().findObject()
      );
   }

   @Override
   public void performTask() {
      PasswordStore target = this.indirectSessionHandler.findDiscordNotifier();
      this.handleTask();
      IndirectPasswordHashVerifier input = target.findIndirectPasswordHashVerifier();
      input.performTask();
      CachedSettingsProcessor.executeSilentProxyState(this.indirectSessionHandler.findIncomingLoginGate().retrieveSilentProxyState(), input);
      LoginMainQueueTask.dispatchPasswordStore(target);
      LimboRegistry output = target.loadLimboRegistry();
      target.b().fetchCollection().forEach(inputValue -> {
         if (!inputValue.findState()) {
            LimboCoordinator outputValue = output.buildLimboCoordinator(inputValue);
            if (outputValue != null) {
               target.findSettingsLinker().retrievePacketCoordinator().handleVerifiedServerAdapter(inputValue, output.loadLimboCoordinator(inputValue));
            } else {
               inputValue.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_PRIVATE_LOUDPROXYSTATE));
            }
         }
      });
      PasswordHashRepository.executePasswordStore(target);
      LowLoginResolver context = target.a().retrieveLowLoginResolver();
      context.processLowLoginResolver("registeredUsers", target.fetchLocalSettingsRepository().getTime());
      context.resolveLowLoginResolver("languageFile", CachedSettingsGateway.loadMessage());
      context.resolveLowLoginResolver("databaseType", Pbkdf2Linker.getDirectNoticeCatalog().name().toLowerCase(Locale.ENGLISH));
   }

   @Override
   public void executeTask() {
      this.sendTask();
      SecondarySenderAdapter target = (SecondarySenderAdapter)this.indirectSessionHandler;
      PasswordHashAdapter input = target.getPasswordHashAdapter();
      if (input != null) {
         input.executeTask();
      }
   }

   public OpenLocaleGateway(IndirectSessionHandler<?> target) {
      this.indirectSessionHandler = target;
   }

   @Override
   public boolean retrieveState() {
      PasswordStore target = this.indirectSessionHandler.findDiscordNotifier();
      return SettingsLookup.validateState(target);
   }

   public abstract void handleTask();

   public abstract void sendTask();
}
