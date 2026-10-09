package com.nickuc.login.session;

import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.bedrock.FloodgateHandler;
import com.nickuc.login.command.completion.VersionCompletionCommand;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.listener.proxy.VelocityGuard;
import com.nickuc.login.listener.proxy.VelocityListener;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.velocity.VelocityPlatform;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.floodgate.FloodgateStore;
import com.nickuc.login.storage.locale.OpenLocaleGateway;
import com.nickuc.login.storage.spawn.SpawnArchive;
import com.nickuc.login.storage.spawn.SpawnStore;
import com.nickuc.login.storage.velocity.VelocityRepository;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.charset.StandardCharsets;

public class VelocityCoordinator extends OpenLocaleGateway {
   private final VelocityGuard velocityGuard;
   private final VelocityPlatform VelocityPlatform;

   public VelocityCoordinator(VelocityPlatform target) {
      super(target);
      this.VelocityPlatform = target;
      this.velocityGuard = new VelocityGuard(target);
   }

   @Override
   public nLoginAPI loadNLoginAPI() {
      return new SpawnArchive(this.VelocityPlatform.fetchPasswordStore());
   }

   @Override
   public void performTask() {
      super.performTask();
      if (QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()
         && this.VelocityPlatform.findProxyServer().getConfiguration().shouldPreventClientProxyConnections()) {
         String target = this.VelocityPlatform.findProxyServer().getVersion().getName();
         String input = "prevent-client-proxy-connections";
         if (CachedSettingsGateway.loadState()) {
            PasswordHashContainer.performMessage("A opção \"" + input + "\" está ativa na config.yml do " + target + ".");
            PasswordHashContainer.performMessage("Essa opção é conhecida por causar problemas de \"Sessão inválida\" para alguns jogadores e é desaconselhada.");
         } else {
            PasswordHashContainer.performMessage("The option \"" + input + "\" is enabled in " + target + "'s config.yml.");
            PasswordHashContainer.performMessage("This option is known to cause \"Invalid session\" errors for some players and is not recommended.");
         }
      }
   }

   @Override
   public void handleTask() {
      VelocityListener target = this.VelocityPlatform.retrieveVelocityListener();
      target.dispatchObject(this.VelocityPlatform.findChannelIdentifier(), this.velocityGuard);
      target.dispatchObject(this.VelocityPlatform.loadChannelIdentifier(), this.velocityGuard);
      this.VelocityPlatform.a(new FloodgateHandler(this.VelocityPlatform, this.VelocityPlatform.fetchPasswordStore()), new OutgoingAccountHandler[0]);
      this.VelocityPlatform.a(new VelocityRepository(this.VelocityPlatform, this.VelocityPlatform.fetchPasswordStore()), new OutgoingAccountHandler[0]);
      this.VelocityPlatform.a(new VersionCompletionCommand(this.VelocityPlatform, this.VelocityPlatform.fetchPasswordStore()), new OutgoingAccountHandler[0]);
   }

   @Override
   public void sendTask() {
      VelocityListener target = this.VelocityPlatform.retrieveVelocityListener();
      target.updateObject(this.VelocityPlatform.findChannelIdentifier());
      target.updateObject(this.VelocityPlatform.loadChannelIdentifier());
   }

   @Override
   public InternalAccountHandler getInternalAccountHandler() {
      return new FloodgateStore(this.VelocityPlatform, this.velocityGuard);
   }

   private static Object resolveObject(Lookup instance, String target, MethodType input) {
      try {
         return new MutableCallSite(
            instance.findStatic(
                  VelocityCoordinator.class,
                  new String(new byte[]{97}, StandardCharsets.UTF_8),
                  MethodType.fromMethodDescriptorString("(IJ)Ljava/lang/String;", VelocityCoordinator.class.getClassLoader())
               )
               .asType(input)
         );
      } catch (Exception context) {
         throw new RuntimeException("com/nickuc/login/ρεεμΣωωψ:" + target + ":" + input.toString(), context);
      }
   }

   public SpawnStore retrieveSpawnStore() {
      return new SpawnStore(this.VelocityPlatform.fetchPasswordStore());
   }
}
