package com.nickuc.login.storage.settings;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.auth.settings.CachedSettingsProcessor;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.discord.DiscordListener;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.server.ServerAdapter;
import com.nickuc.login.platform.proxy.StoredProxyCatalog;
import com.nickuc.login.premium.FloodgateResolver;
import com.nickuc.login.premium.IndirectPasswordHashVerifier;
import com.nickuc.login.premium.LowLoginResolver;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SettingsLookup;
import com.nickuc.login.security.hashing.PasswordHashProvider;
import com.nickuc.login.session.BusyLimboStore;
import com.nickuc.login.tasks.LoginMainQueueTask;
import java.io.File;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class SettingsStore implements ServerAdapter {
   private final BukkitPlatform BukkitPlatform;

   private void processTask() {
      PluginManager target = this.BukkitPlatform.getServer().getPluginManager();
      Plugin input = target.getPlugin("NickUC-Updater");
      if (input != null) {
         target.disablePlugin(input);
         File output = new File(this.BukkitPlatform.resolveFile().getParentFile(), "nUpdater.jar");
         if (output.exists() && !output.delete()) {
            PasswordHashContainer.updateMessage("Unable to delete " + output.getPath() + " file!");
         }
      }
   }

   @Override
   public nLoginAPI loadNLoginAPI() {
      return new SpawnCollection(this.BukkitPlatform.fetchPasswordStore());
   }

   @Override
   public boolean retrieveState() {
      return SettingsLookup.validateState(this.BukkitPlatform.fetchPasswordStore());
   }

   @Override
   public InternalAccountHandler getInternalAccountHandler() {
      return new DiscordListener(this.BukkitPlatform);
   }

   private static Object loadObject(Lookup instance, String target, MethodType input) {
      try {
         return new MutableCallSite(
            instance.findStatic(
                  SettingsStore.class,
                  new String(new byte[]{97}, StandardCharsets.UTF_8),
                  MethodType.fromMethodDescriptorString("(IJ)Ljava/lang/String;", SettingsStore.class.getClassLoader())
               )
               .asType(input)
         );
      } catch (Exception context) {
         throw new RuntimeException("com/nickuc/login/βζερηΣολσαμ:" + target + ":" + input.toString(), context);
      }
   }

   public SettingsStore(BukkitPlatform target) {
      this.BukkitPlatform = target;
   }

   @Override
   public FloodgateResolver fetchFloodgateResolver() {
      return new PasswordHashProvider((PasswordStore)this.BukkitPlatform.b(), this.BukkitPlatform, false);
   }

   public BusyLimboStore resolveBusyLimboStore() {
      return new BusyLimboStore(this.BukkitPlatform);
   }

   @Override
   public void executeTask() {
   }

   @Override
   public void performTask() {
      PasswordStore target = this.BukkitPlatform.fetchPasswordStore();
      this.BukkitPlatform
         .b()
         .fetchCollection()
         .forEach(instance -> instance.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.SHARED_LOUDPROXYSTATE, instance)));
      StoredProxyCatalog.saveNLoginBukkit(this.BukkitPlatform, false);
      IndirectPasswordHashVerifier input = target.findIndirectPasswordHashVerifier();
      input.performTask();
      CachedSettingsProcessor.executeSilentProxyState(this.BukkitPlatform.b().retrieveSilentProxyState(), input);
      LoginMainQueueTask.dispatchPasswordStore(target);
      this.BukkitPlatform
         .b()
         .fetchCollection()
         .forEach(
            targetValue -> target.findSettingsLinker()
               .retrievePacketCoordinator()
               .handleVerifiedServerAdapter(targetValue, target.loadLimboRegistry().loadLimboCoordinator(targetValue))
         );
      PasswordHashRepository.executePasswordStore(target);
      PasswordDao.passwordDao.b(target);
      LowLoginResolver output = this.BukkitPlatform.a().retrieveLowLoginResolver();
      output.processLowLoginResolver("registeredUsers", target.fetchLocalSettingsRepository().getTime());
      output.resolveLowLoginResolver("languageFile", CachedSettingsGateway.loadMessage());
      output.resolveLowLoginResolver("databaseType", Pbkdf2Linker.getDirectNoticeCatalog().name().toLowerCase(Locale.ENGLISH));
      this.processTask();
   }
}
