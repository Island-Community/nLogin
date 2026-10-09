package com.nickuc.login.protocol;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.auth.login.LenientLoginFlow;
import com.nickuc.login.auth.login.VerifiedLoginGate;
import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.SettingsRegistry;
import com.nickuc.login.model.PremiumState;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.crypto.SaltSignature;
import com.github.retrooper.packetevents.wrapper.login.client.WrapperLoginClientEncryptionResponse;
import com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerEncryptionRequest;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.IndirectPasswordResolver;
import com.nickuc.login.premium.LocaleVerifier;
import com.nickuc.login.premium.LoginVerifier;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.security.hashing.Sha256Digest;
import com.nickuc.login.session.Sha256Tracker;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordGateway;
import com.nickuc.login.storage.spawn.SpawnState;
import io.netty.channel.Channel;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class MojangBridge implements LenientCommandHandler, QuickCommandHandler {
   public void saveUser(User target, Channel input, WrapperLoginClientEncryptionResponse output, PacketInterceptor context, boolean data) {
      if (this.validateState(target, output, context)) {
         byte[] value;
         SecretKeySpec result;
         try {
            value = Sha256Tracker.buildPayload(SettingsRegistry.computeKeyPair(this.settingsRegistry), output.getEncryptedSharedSecret());
            result = new SecretKeySpec(value, "AES");
         } catch (GeneralSecurityException event) {
            target.closeConnection();
            return;
         }

         Object request = context.resolveObject();
         if (!data) {
            this.performObject(request, result);
         }

         boolean response = QuickPremiumOption.CURRENT_QUICKPREMIUMOPTION.retrieveState();
         SpawnLookup source = context.loadSpawnLookup();
         String entry = context.findMessage();
         String record = Sha256Tracker.buildMessage("", value, SettingsRegistry.computeKeyPair(this.settingsRegistry).getPublic());
         VerifiedLoginGate item = LocaleVerifier.loadVerifiedLoginGate(SettingsRegistry.createNLoginBukkit(this.settingsRegistry), entry, record, null);
         switch (item.findCount()) {
            case 0:
               SettingsInterceptor.sendUser(target, "multiplayer.disconnect.authservers_down");
               PasswordHashContainer.updateMessage("Unable to contact Mojang servers for " + entry + ": auth servers are down [got " + item.findCount() + "]");
               break;
            case 200:
               LoginVerifier packet = LocaleVerifier.computeLoginVerifier(item.loadMessage());
               String content = packet.getName();
               if (content == null) {
                  SettingsInterceptor.handleUser(target, "Username field null for " + content);
                  return;
               }

               UUID payload = packet.fetchUniqueId();
               if (payload == null) {
                  SettingsInterceptor.handleUser(target, "UUID field null for " + content);
                  return;
               }

               byte holder = 0;
               if (source != null && source.getMojangId() != null && !payload.equals(source.getMojangId())) {
                  PasswordHashContainer.dispatchMessage(
                     "Mojang ID mismatch detected, is another account using an offline UUID? expected uuid = %s, received uuid = %s, expected name = %s, received name = %s",
                     source.getMojangId(),
                     payload,
                     source.retrieveMessage(),
                     content
                  );
                  source = null;
                  holder = 1;
               }

               IndirectPasswordResolver reference = SettingsRegistry.computePasswordStore(this.settingsRegistry).findIndirectPasswordResolver();
               SpawnLookup subject = reference.computeSpawnLookup(content, payload, null, false);
               if (subject == null) {
                  SettingsInterceptor.handleUser(target, CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                  return;
               }

               if (source == null || subject.resolveStateForState()) {
                  source = subject;
               }

               InetAddress option = target.getAddress().getAddress();
               String setting = option.getHostAddress();
               String property = response ? Pbkdf2Linker.handleMessage(content, true) : content;
               if (SettingsRegistry.computePasswordStore(this.settingsRegistry)
                  .findIndirectPasswordResolver()
                  .isState(targetValue -> SettingsInterceptor.handleUser(target, targetValue), source, setting, SpawnState.ACTIVE_LOCAL_SPAWNSTATE)) {
                  return;
               }

               if (SettingsRegistry.computePasswordStore(this.settingsRegistry)
                  .findIndirectPasswordResolver()
                  .canState(targetValue -> SettingsInterceptor.handleUser(target, targetValue), source, setting, SpawnState.ACTIVE_INTERNAL_SPAWNSTATE)) {
                  return;
               }

               if (!reference.canState(source, payload, content, setting, (boolean)holder)) {
                  SettingsInterceptor.handleUser(target, CachedSettingsGateway.computeMessage(LoudProxyState.DIRECT_LOUDPROXYSTATE));
                  return;
               }

               VerifiedServerAdapter attribute = SettingsRegistry.createNLoginBukkit(this.settingsRegistry).b().resolveVerifiedServerAdapter(property);
               if (attribute != null && attribute.loadState()) {
                  attribute.buildCompletableFuture(CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_LINKED_LOUDPROXYSTATE));
               }

               LenientLoginFlow[] parameter = packet.findValues();
               LenientLoginFlow argument = parameter.length > 0 ? parameter[0] : null;
               Runnable message = MojangInterceptor.computeRunnable(request, property, source.getUniqueId(), argument);
               PacketLink notice = new PacketLink(target, source, content, source.getUniqueId(), false, message, input, argument);
               input.attr(PacketLink.attributeKey).set(notice);
               PacketLink.updateMessage(property, context.resolveMessage(), option, notice);
               if (data) {
                  target.receivePacketSilently(output);
               } else {
                  SettingsRegistry.saveUser(target, context.findWrapperLoginClientLoginStart(), property);
               }
               break;
            case 204:
               String element = !response && (source == null || !source.fetchStateAndState())
                  ? CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_FAST_LOUDPROXYSTATE, entry)
                  : CachedSettingsGateway.computeMessage(LoudProxyState.ACTIVE_TOP_LOUDPROXYSTATE, entry);
               SettingsInterceptor.handleUser(target, element);
               break;
            case 429:
               SettingsInterceptor.sendUser(target, "multiplayer.disconnect.authservers_down");
               PasswordHashContainer.updateMessage("Unable to contact Mojang servers for " + entry + ": too many requests [got " + item.findCount() + "]");
               break;
            default:
               SettingsInterceptor.sendUser(target, "multiplayer.disconnect.authservers_down");
               PasswordHashContainer.updateMessage(
                  "Unable to contact Mojang servers for " + entry + ": unexpected response code [got " + item.findCount() + "]"
               );
         }
      }
   }

   @Override
   public void savePacketSendEvent(PacketSendEvent target) {
      WrapperLoginServerEncryptionRequest input = new WrapperLoginServerEncryptionRequest(target);
      User output = target.getUser();
      Channel context = (Channel)target.getChannel();
      PacketInterceptor data = (PacketInterceptor)context.attr(PacketInterceptor.attributeKey).get();
      if (data == null) {
         SettingsInterceptor.handleUser(output, "Invalid state");
      } else {
         String value = data.findMessage();
         InetAddress result = output.getAddress().getAddress();
         PremiumState request = PasswordGateway.loadPremiumState(value, result);
         PasswordGateway.processPasswordStore(
            SettingsRegistry.computePasswordStore(this.settingsRegistry),
            value,
            result,
            request == PremiumState.ACTIVE_PREMIUMSTATE ? PremiumState.CURRENT_PREMIUMSTATE : PremiumState.PRIMARY_PREMIUMSTATE
         );
         context.attr(PacketInterceptor.attributeKey).set(data.resolvePacketInterceptor(input.getVerifyToken()));
      }
   }

   @Override
   public void dispatchPacketReceiveEvent(PacketReceiveEvent target) {
      User input = target.getUser();
      Channel output = (Channel)target.getChannel();
      PacketInterceptor context = (PacketInterceptor)output.attr(PacketInterceptor.attributeKey).getAndSet(null);
      if (context == null) {
         SettingsInterceptor.handleUser(input, "Invalid state");
      } else {
         target.setCancelled(true);
         WrapperLoginClientEncryptionResponse data = new WrapperLoginClientEncryptionResponse(target);
         boolean value = SettingsRegistry.createNLoginBukkit(this.settingsRegistry).getServer().getOnlineMode();
         String result = context.findMessage();
         InetAddress request = input.getAddress().getAddress();
         PremiumState response = PasswordGateway.loadPremiumState(result, request);
         switch (response) {
            case CURRENT_PREMIUMSTATE:
            case PRIMARY_PREMIUMSTATE:
               PasswordGateway.processPasswordStore(SettingsRegistry.computePasswordStore(this.settingsRegistry), result, request, PremiumState.PREMIUM_STATE);
            default:
               SettingsRegistry.createNLoginBukkit(this.settingsRegistry)
                  .processLinkedSessionHandler(true)
                  .buildStrictCommandHandler(
                     () -> {
                        try {
                           if (!output.isOpen() || !output.isActive()) {
                              return;
                           }

                           this.saveUser(input, output, data, context, value);
                        } catch (Throwable resultValue) {
                           SettingsInterceptor.handleUser(input, "[nLogin] Exception during encryption process. Check the console for more details.");
                           PasswordHashContainer.handleMessage(
                              "Unexpected exception during encryption process for " + context.findMessage() + " (probably this is a bug!)", resultValue
                           );
                        }
                     }
                  );
         }
      }
   }

   private void performObject(Object target, SecretKey input) {
      if (SettingsRegistry.getMethod() != null) {
         Object output = SettingsRegistry.getMethod().invoke(null, 2, input);
         Object context = SettingsRegistry.getMethod().invoke(null, 1, input);
         SettingsRegistry.resolveMethod().invoke(target, output, context);
      } else {
         SettingsRegistry.resolveMethod().invoke(target, input);
      }
   }

   private boolean validateState(User target, WrapperLoginClientEncryptionResponse input, PacketInterceptor output) {
      try {
         Sha256Digest context = output.resolveShaDigest();
         if (context != null) {
            SaltSignature request = (SaltSignature)input.getSaltSignature().orElse(null);
            if (request == null) {
               SettingsInterceptor.handleUser(target, "Client public signature not present.");
               PasswordHashContainer.dispatchMessage("GameProfile " + output.findMessage() + " (" + target.getAddress() + ") did not send profile key.");
               return false;
            } else if (!context.isState(request.getSignature(), output.findPayload(), request.getSalt())) {
               SettingsInterceptor.handleUser(target, "Invalid client public signature.");
               return false;
            } else {
               return true;
            }
         } else {
            byte[] data = (byte[])input.getEncryptedVerifyToken().orElse(null);
            if (data == null) {
               SettingsInterceptor.handleUser(target, "Client response verify token not present.");
               PasswordHashContainer.dispatchMessage("GameProfile " + output.findMessage() + " (" + target.getAddress() + ") did not send response verify token.");
               return false;
            } else {
               byte[] value = output.findPayload();
               if (!Arrays.equals(value, Sha256Tracker.buildPayload(SettingsRegistry.computeKeyPair(this.settingsRegistry), data))) {
                  SettingsInterceptor.handleUser(target, "Unable to decrypt verification token.");
                  PasswordHashContainer.dispatchMessage(
                     "GameProfile "
                        + output.findMessage()
                        + " ("
                        + target.getAddress()
                        + ") tried to login with an invalid verify token. Server: "
                        + Arrays.toString(value)
                        + " Client: "
                        + Arrays.toString(data)
                  );
                  return false;
               } else {
                  return true;
               }
            }
         }
      } catch (Exception result) {
         SettingsInterceptor.handleUser(target, "Unable to decrypt verification token.");
         if (!(result instanceof GeneralSecurityException)) {
            PasswordHashContainer.updateMessage("Unable to decrypt the verification token (probably this is a bug!)");
         }

         return false;
      }
   }

   private MojangBridge(SettingsRegistry target) {
      this.settingsRegistry = target;
   }
}
