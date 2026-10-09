package com.nickuc.login.session;

import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.command.completion.LocalLinkSpawnCommand;
import com.nickuc.login.config.IndirectBungeeLoader;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.listener.proxy.StrictBungeeGuard;
import com.nickuc.login.platform.account.InternalAccountHandler;
import com.nickuc.login.platform.account.OutgoingAccountHandler;
import com.nickuc.login.premium.BedrockLinker;
import com.nickuc.login.proxy.SpawnGateway;
import com.nickuc.login.bungee.BungeePlatform;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.floodgate.FloodgateArchive;
import com.nickuc.login.storage.locale.OpenLocaleGateway;
import com.nickuc.login.storage.spawn.SpawnStore;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.MethodHandles.Lookup;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PreLoginEvent;

public class BungeeCoordinator extends OpenLocaleGateway {
   private final BungeePlatform BungeePlatform;
   private final StrictBungeeGuard strictBungeeGuard;

   @Override
   public void performTask() {
      super.performTask();
      if (QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState() && BungeeCord.getInstance().getConfig().isPreventProxyConnections()) {
         String target = ProxyServer.getInstance().getName();
         String input = "prevent_proxy_connections";
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
   public void sendTask() {
      this.BungeePlatform.fetchProxyCodec().updateObject("nlogin:main");
   }

   @Override
   public nLoginAPI loadNLoginAPI() {
      return new SpawnGateway(this.BungeePlatform.fetchPasswordStore());
   }

   public BungeeCoordinator(BungeePlatform target) {
      super(target);
      this.BungeePlatform = target;
      this.strictBungeeGuard = new StrictBungeeGuard(target);
   }

   public SpawnStore retrieveSpawnStore() {
      return new SpawnStore(this.BungeePlatform.fetchPasswordStore());
   }

   @Override
   public void handleTask() {
      this.BungeePlatform.fetchProxyCodec().dispatchObject("nlogin:main", this.strictBungeeGuard);
      this.BungeePlatform.a(new BedrockLinker(this.BungeePlatform, this.BungeePlatform.fetchPasswordStore()), new OutgoingAccountHandler[0]);
      this.BungeePlatform.a(new IndirectBungeeLoader(this.BungeePlatform, this.BungeePlatform.fetchPasswordStore()), new OutgoingAccountHandler[0]);
      this.BungeePlatform.a(new LocalLinkSpawnCommand(this.BungeePlatform, this.BungeePlatform.fetchPasswordStore()), new OutgoingAccountHandler[0]);
      AtomicBoolean target = new AtomicBoolean();
      PasswordHashContainer.saveConnectionContract(
         (targetValue, input, output) -> {
            switch (output.length) {
               case 2:
                  if (!target.get()
                     || !input.equals("Event {0} took {1}ms to process!")
                        && (!input.startsWith("Event ") || !input.contains(" took ") || !input.endsWith("ms to process!"))) {
                     break;
                  }

                  return output[0] instanceof PreLoginEvent
                     && QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()
                     && !QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState();
               case 3:
                  if (input.equals("Plugin listener {0} took {1}ms to process event {2}!")
                     || input.startsWith("Plugin listener ") && input.contains(" took ") && input.contains("ms to process event ")) {
                     String context = (String)output[0];
                     if (context.startsWith("com.nickuc.login")) {
                        Object data = output[1];
                        Object value = output[2];
                        if (context.equals(BedrockLinker.class.getCanonicalName())
                           && QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState()
                           && !QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState()) {
                           PasswordHashContainer.dispatchMessage("Login events listener took " + data + "ms to process!");
                        } else {
                           PasswordHashContainer.performMessage(
                              "Event listener from " + context + " (" + value.getClass().getSimpleName() + ") took " + data + "ms to process!"
                           );
                        }

                        target.set(true);
                        return true;
                     }
                  }
                  break;
               default:
                  target.set(false);
            }

            return false;
         }
      );
   }

   private static Object resolveObject(Lookup instance, String target, MethodType input) {
      try {
         return new MutableCallSite(
            instance.findStatic(
                  BungeeCoordinator.class,
                  new String(new byte[]{97}, StandardCharsets.UTF_8),
                  MethodType.fromMethodDescriptorString("(IJ)Ljava/lang/String;", BungeeCoordinator.class.getClassLoader())
               )
               .asType(input)
         );
      } catch (Exception context) {
         throw new RuntimeException("com/nickuc/login/ζβκψγαωηκΠΠ:" + target + ":" + input.toString(), context);
      }
   }

   @Override
   public InternalAccountHandler getInternalAccountHandler() {
      return new FloodgateArchive(this.BungeePlatform, this.strictBungeeGuard);
   }
}
