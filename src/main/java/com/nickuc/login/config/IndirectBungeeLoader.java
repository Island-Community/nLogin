package com.nickuc.login.config;

import com.nickuc.login.api.enums.ServerConnectType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.event.bungee.connection.DefineAuthServerEvent;
import com.nickuc.login.api.event.bungee.connection.ServerPreConnectEvent;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.listener.RootServerAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.bungee.BungeePlatform;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LowLimboTracker;
import com.nickuc.login.spawn.PrimaryMessageOption;
import com.nickuc.login.storage.password.PasswordStore;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.event.EventHandler;

public class IndirectBungeeLoader implements RootServerAdapter {
   private final PasswordStore passwordStore;
   private final BungeePlatform BungeePlatform;

   private void executeServerConnectEvent(ServerConnectEvent target) {
      ProxiedPlayer input = target.getPlayer();
      VerifiedServerAdapter output = this.BungeePlatform.b().processVerifiedServerAdapter(input);
      if (!output.findState()) {
         if (!output.loadState()) {
            target.setCancelled(true);
         } else {
            LimboCoordinator context = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
            if (!context.loadTightPlatformCatalog().isState(TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)) {
               ServerInfo data = target.getTarget();
               String value = data.getName();
               int result = input.getServer() == null ? 1 : 0;
               if (result == 0) {
                  if (!this.BungeePlatform.isState(context, value)) {
                     target.setCancelled(true);
                  }
               } else {
                  context.updateLenientMessageKind(LenientMessageKind.ACTIVE_AUTHENTICATED_LENIENTMESSAGEKIND, true);
                  SpawnLookup request = context.loadSpawnLookup();
                  boolean response = this.passwordStore
                     .findSettingsLinker()
                     .retrievePacketCoordinator()
                     .isState(output, context, request, input.getUniqueId(), input.getPendingConnection().isOnlineMode(), (InetSocketAddress)input.getSocketAddress());
                  if (response) {
                     if (PrimaryMessageOption.LOCAL_PRIMARYMESSAGEOPTION.ar()) {
                        String source = request.resolveCachedPasswordHashHasher().handleObject("last-server");
                        if (source != null
                           && !this.BungeePlatform.isState(context, source)
                           && !PrimaryMessageOption.REMOTE_PRIMARYMESSAGEOPTION.a(new Object[0]).contains(source)) {
                           ServerInfo entry = this.BungeePlatform.findProxyServer().getServerInfo(source);
                           if (entry != null) {
                              ServerPreConnectEvent record = this.passwordStore
                                 .loadObject(EventEnum.SERVER_PRE_CONNECT, output, ServerConnectType.WITH_LAST_SERVER, entry);
                              if (this.passwordStore.callEvent(record)) {
                                 target.setTarget(record.getServer());
                                 return;
                              }
                           }
                        }
                     }

                     if (PrimaryMessageOption.CACHED_PRIMARYMESSAGEOPTION.ar()) {
                        List payload = PrimaryMessageOption.STORED_PRIMARYMESSAGEOPTION
                           .a(new Object[0])
                           .stream()
                           .map(targetValue -> this.BungeePlatform.findProxyServer().getServerInfo(targetValue))
                           .filter(Objects::nonNull)
                           .collect(Collectors.toList());
                        if (payload.isEmpty()) {
                           String setting = context.loadState()
                              ? "§cCertifique-se de configurar corretamente os servidores de redirecionamento em 'plugins/nLogin/proxy/config.yml'.\n\n§cServidor sendo conectado: %s\n§cLista de servidores válidos: %s\n§cLista de servidores: %s"
                              : "§cMake sure you configure the redirect servers correctly in 'plugins/nLogin/proxy/config.yml'.\n\n§cServer being connected: %s\n§cList of valid servers: %s\n§cList of servers: %s";
                           BaseComponent[] parameter = TextComponent.fromLegacyText(
                              String.format(setting, value, payload, PrimaryMessageOption.STORED_PRIMARYMESSAGEOPTION.a(new Object[0]))
                           );
                           input.disconnect(parameter);
                           return;
                        }

                        ServerInfo subject = (ServerInfo)payload.get(SecureLoginHandler.retrieveRandom().nextInt(payload.size()));
                        ServerPreConnectEvent property = this.passwordStore
                           .loadObject(EventEnum.SERVER_PRE_CONNECT, output, ServerConnectType.WITH_CONFIGURED_SERVER, subject);
                        if (this.passwordStore.callEvent(property)) {
                           target.setTarget(property.getServer());
                           return;
                        }
                     }

                     ServerPreConnectEvent holder = this.passwordStore
                        .loadObject(EventEnum.SERVER_PRE_CONNECT, output, ServerConnectType.WITH_PLATFORM_SERVER, data);
                     if (this.passwordStore.callEvent(holder)) {
                        target.setTarget(holder.getServer());
                        return;
                     }
                  }

                  if (PrimaryMessageOption.CURRENT_PRIMARYMESSAGEOPTION.ar()) {
                     if (!this.BungeePlatform.isState(context, value)) {
                        context.updateLenientMessageKind(LenientMessageKind.ACTIVE_VERIFIED_LENIENTMESSAGEKIND, value);
                     }

                     List reference = PrimaryMessageOption.PRIMARY_MESSAGE_OPTION
                        .a(new Object[0])
                        .stream()
                        .map(targetValue -> this.BungeePlatform.findProxyServer().getServerInfo(targetValue))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                     ServerInfo option = !reference.isEmpty() ? (ServerInfo)reference.get(SecureLoginHandler.retrieveRandom().nextInt(reference.size())) : null;
                     DefineAuthServerEvent attribute = this.passwordStore.loadObject(EventEnum.DEFINE_AUTH_SERVER, output, option);
                     this.passwordStore.callEvent(attribute);
                     ServerInfo item = (ServerInfo)attribute.getServer().orElse(null);
                     if (item == null) {
                        target.setCancelled(true);
                        String element = context.loadState()
                           ? "§cCertifique-se de configurar corretamente os servidores de autenticação em 'plugins/nLogin/proxy/config.yml'.\n\n§cServidor sendo conectado: %s\n§cLista de servidores válidos: %s\n§cLista de servidores: %s"
                           : "§cMake sure you configure the authentication servers correctly in 'plugins/nLogin/proxy/config.yml'.\n\n§cServer being connected: %s\n§cList of valid servers: %s\n§cList of servers: %s";
                        BaseComponent[] content = TextComponent.fromLegacyText(
                           String.format(element, value, reference, PrimaryMessageOption.PRIMARY_MESSAGE_OPTION.a(new Object[0]))
                        );
                        input.disconnect(content);
                        return;
                     }

                     context.updateLenientMessageKind(LenientMessageKind.ACTIVE_PRIMARY_LENIENTMESSAGEKIND, item.getName());
                     target.setTarget(item);
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = 32)
   public void performServerConnectEvent(ServerConnectEvent target) {
      if (!target.isCancelled()) {
         try {
            this.executeServerConnectEvent(target);
         } catch (Throwable output) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + target.getPlayer().getName() + ")", output);
            target.setCancelled(true);
            target.getPlayer().disconnect(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
         }
      }
   }

   @EventHandler
   public void processServerSwitchEvent(ServerSwitchEvent target) {
      ProxiedPlayer input = target.getPlayer();
      if (input.isConnected()) {
         try {
            if (input.getPendingConnection().getVersion() < 764) {
               VerifiedServerAdapter output = this.BungeePlatform.b().processVerifiedServerAdapter(input);
               if (output.findState()) {
                  return;
               }

               LimboCoordinator context = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
               ((LowLimboTracker)this.passwordStore.getFloodgateResolver())
                  .executeVerifiedServerAdapter(
                     output,
                     input.getServer().getInfo().getName(),
                     Boolean.TRUE.equals(context.loadObject(LenientMessageKind.ACTIVE_AUTHENTICATED_LENIENTMESSAGEKIND))
                  );
            }
         } catch (Throwable data) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getName() + ")", data);
            input.disconnect(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
         }
      }
   }

   @EventHandler
   public void executeServerConnectedEvent(ServerConnectedEvent target) {
      ProxiedPlayer input = target.getPlayer();
      if (input.isConnected()) {
         try {
            if (input.getPendingConnection().getVersion() >= 764) {
               VerifiedServerAdapter output = this.BungeePlatform.b().processVerifiedServerAdapter(input);
               if (output.findState()) {
                  return;
               }

               LimboCoordinator context = this.passwordStore.loadLimboRegistry().loadLimboCoordinator(output);
               ((LowLimboTracker)this.passwordStore.getFloodgateResolver())
                  .executeVerifiedServerAdapter(
                     output,
                     input.getServer().getInfo().getName(),
                     Boolean.TRUE.equals(context.loadObject(LenientMessageKind.ACTIVE_AUTHENTICATED_LENIENTMESSAGEKIND))
                  );
            }
         } catch (Throwable data) {
            PasswordHashContainer.handleMessage("Severe error during " + target.getClass().getSimpleName() + " event (" + input.getName() + ")", data);
            input.disconnect(TextComponent.fromLegacyText("§4[nLogin] Severe internal error detected. Please report to an admin."));
         }
      }
   }

   public IndirectBungeeLoader(BungeePlatform target, PasswordStore input) {
      this.BungeePlatform = target;
      this.passwordStore = input;
   }
}
