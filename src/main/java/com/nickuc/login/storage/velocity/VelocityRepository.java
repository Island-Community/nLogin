package com.nickuc.login.storage.velocity;

import com.nickuc.login.api.enums.ServerConnectType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.event.velocity.connection.DefineAuthServerEvent;
import com.nickuc.login.api.event.velocity.connection.ServerPreConnectEvent;
import com.nickuc.login.auth.login.SafeLoginService;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.sender.StrictSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.velocity.VelocityPlatform;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LowLimboTracker;
import com.nickuc.login.spawn.PrimaryMessageOption;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class VelocityRepository implements StrictSenderAdapter {
   private final PasswordStore passwordStore;
   private final VelocityPlatform VelocityPlatform;

   @Subscribe(order = PostOrder.LAST)
   public void dispatchPlayerChooseInitialServerEvent(PlayerChooseInitialServerEvent target) {
      try {
         this.performPlayerChooseInitialServerEvent(target);
      } catch (Throwable output) {
         PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + target.getPlayer().getUsername() + ")", output);
         target.getPlayer().disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
      }
   }

   private void performPlayerChooseInitialServerEvent(PlayerChooseInitialServerEvent target) {
      Player input = target.getPlayer();
      VerifiedServerAdapter output = this.VelocityPlatform.b().processVerifiedServerAdapter(input);
      if (!output.findState()) {
         LimboCoordinator context = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
         if (!context.loadTightPlatformCatalog().isState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)) {
            Optional data = target.getInitialServer();
            String value = data.<String>map(instance -> instance.getServerInfo().getName()).orElse("???");
            SpawnLookup result = context.loadSpawnLookup();
            boolean request = this.passwordStore
               .findSettingsLinker()
               .retrievePacketCoordinator()
               .isState(output, context, result, input.getUniqueId(), input.isOnlineMode(), input.getRemoteAddress());
            context.updateLenientMessageKind(LenientMessageKind.ACTIVE_CURRENT_LENIENTMESSAGEKIND, request);
            if (request) {
               if (PrimaryMessageOption.LOCAL_PRIMARYMESSAGEOPTION.ar()) {
                  String response = result.resolveCachedPasswordHashHasher().handleObject("last-server");
                  if (response != null
                     && !this.VelocityPlatform.isState(context, response)
                     && !PrimaryMessageOption.REMOTE_PRIMARYMESSAGEOPTION.a(new Object[0]).contains(response)) {
                     Optional source = this.VelocityPlatform.findProxyServer().getServer(response);
                     if (source.isPresent()) {
                        RegisteredServer entry = (RegisteredServer)source.get();
                        ServerPreConnectEvent record = this.passwordStore
                           .loadObject(EventEnum.SERVER_PRE_CONNECT, output, ServerConnectType.WITH_LAST_SERVER, entry);
                        if (this.passwordStore.callEvent(record)) {
                           target.setInitialServer(record.getServer());
                           return;
                        }
                     }
                  }
               }

               if (PrimaryMessageOption.CACHED_PRIMARYMESSAGEOPTION.ar()) {
                  List content = PrimaryMessageOption.STORED_PRIMARYMESSAGEOPTION
                     .a(new Object[0])
                     .stream()
                     .map(targetValue -> this.VelocityPlatform.findProxyServer().getServer(targetValue))
                     .map(instance -> (RegisteredServer)instance.orElse(null))
                     .filter(Objects::nonNull)
                     .collect(Collectors.toList());
                  if (content.isEmpty()) {
                     target.setInitialServer(null);
                     String option = context.loadState()
                        ? "§cCertifique-se de configurar corretamente os servidores de redirecionamento em 'plugins/nLogin/proxy/config.yml'.\n\n§cServidor sendo conectado: %s\n§cLista de servidores válidos: %s\n§cLista de servidores: %s"
                        : "§cMake sure you configure the redirect servers correctly in 'plugins/nLogin/proxy/config.yml'.\n\n§cServer being connected: %s\n§cList of valid servers: %s\n§cList of servers: %s";
                     String attribute = String.format(option, value, content, PrimaryMessageOption.PRIMARY_MESSAGE_OPTION.a(new Object[0]));
                     input.disconnect(SafeLoginService.resolveTextComponent(attribute));
                     return;
                  }

                  RegisteredServer reference = (RegisteredServer)content.get(SecureLoginHandler.retrieveRandom().nextInt(content.size()));
                  ServerPreConnectEvent setting = this.passwordStore
                     .loadObject(EventEnum.SERVER_PRE_CONNECT, output, ServerConnectType.WITH_CONFIGURED_SERVER, reference);
                  if (this.passwordStore.callEvent(setting)) {
                     target.setInitialServer(setting.getServer());
                     return;
                  }
               }

               if (data.isPresent()) {
                  ServerPreConnectEvent payload = this.passwordStore
                     .loadObject(EventEnum.SERVER_PRE_CONNECT, output, ServerConnectType.WITH_PLATFORM_SERVER, data.get());
                  if (this.passwordStore.callEvent(payload)) {
                     target.setInitialServer(payload.getServer());
                     return;
                  }
               }
            }

            if (PrimaryMessageOption.CURRENT_PRIMARYMESSAGEOPTION.ar()) {
               data.<String>map(instance -> instance.getServerInfo().getName())
                  .filter(inputValue -> !this.VelocityPlatform.isState(context, inputValue))
                  .ifPresent(targetValue -> context.updateLenientMessageKind(LenientMessageKind.ACTIVE_VERIFIED_LENIENTMESSAGEKIND, targetValue));
               List holder = PrimaryMessageOption.PRIMARY_MESSAGE_OPTION
                  .a(new Object[0])
                  .stream()
                  .map(targetValue -> this.VelocityPlatform.findProxyServer().getServer(targetValue))
                  .map(instance -> (RegisteredServer)instance.orElse(null))
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList());
               RegisteredServer subject = !holder.isEmpty() ? (RegisteredServer)holder.get(SecureLoginHandler.retrieveRandom().nextInt(holder.size())) : null;
               DefineAuthServerEvent property = this.passwordStore.loadObject(EventEnum.DEFINE_AUTH_SERVER, output, subject);
               this.passwordStore.callEvent(property);
               RegisteredServer parameter = (RegisteredServer)property.getServer().orElse(null);
               if (parameter == null) {
                  String item = context.loadState()
                     ? "§cCertifique-se de configurar corretamente os servidores de autenticação em 'plugins/nLogin/proxy/config.yml'.\n\n§cServidor sendo conectado: %s\n§cLista de servidores válidos: %s\n§cLista de servidores: %s"
                     : "§cMake sure you configure the authentication servers correctly in 'plugins/nLogin/proxy/config.yml'.\n\n§cServer being connected: %s\n§cList of valid servers: %s\n§cList of servers: %s";
                  String element = String.format(item, value, holder, PrimaryMessageOption.PRIMARY_MESSAGE_OPTION.a(new Object[0]));
                  input.disconnect(SafeLoginService.resolveTextComponent(element));
                  return;
               }

               context.updateLenientMessageKind(LenientMessageKind.ACTIVE_PRIMARY_LENIENTMESSAGEKIND, parameter.getServerInfo().getName());
               target.setInitialServer(parameter);
            }
         }
      }
   }

   @Subscribe
   public void dispatchServerPostConnectEvent(ServerPostConnectEvent target) {
      Player input = target.getPlayer();

      try {
         VerifiedServerAdapter output = this.VelocityPlatform.b().processVerifiedServerAdapter(input);
         if (output.findState()) {
            return;
         }

         ((LowLimboTracker)this.passwordStore.getFloodgateResolver())
            .executeVerifiedServerAdapter(
               output,
               ((ServerConnection)input.getCurrentServer()
                     .orElseThrow(() -> new IllegalStateException("Player#getCurrentServer is unavailable in ServerPostConnectEvent event!")))
                  .getServerInfo()
                  .getName(),
               target.getPreviousServer() == null
            );
      } catch (Throwable context) {
         PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getUsername() + ")", context);
         input.disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
      }
   }

   @Subscribe(order = PostOrder.LAST)
   public void processServerPreConnectEvent(com.velocitypowered.api.event.player.ServerPreConnectEvent target) {
      if (target.getResult().isAllowed()) {
         Player input = target.getPlayer();
         if (!input.isActive()) {
            target.setResult(ServerResult.denied());
         } else {
            try {
               VerifiedServerAdapter output = this.VelocityPlatform.b().processVerifiedServerAdapter(input);
               if (output.findState()) {
                  return;
               }

               LimboCoordinator context = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
               if (context.loadTightPlatformCatalog().isState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)) {
                  return;
               }

               Optional data = target.getResult().getServer();
               if (!data.isPresent()
                  || !this.VelocityPlatform.isState(context, ((RegisteredServer)data.get()).getServerInfo().getName())
                     && !context.d(LenientMessageKind.ACTIVE_CURRENT_LENIENTMESSAGEKIND)) {
                  target.setResult(ServerResult.denied());
               }
            } catch (Throwable value) {
               PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getUsername() + ")", value);
               target.setResult(ServerResult.denied());
               input.disconnect(SafeLoginService.resolveTextComponent("§4[nLogin] Severe internal error detected. Please report to an admin."));
            }
         }
      }
   }

   public VelocityRepository(VelocityPlatform target, PasswordStore input) {
      this.VelocityPlatform = target;
      this.passwordStore = input;
   }
}
