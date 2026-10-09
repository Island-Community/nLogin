package com.nickuc.login.auth.password;

import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.QuickPremiumOption;
import com.nickuc.login.auth.twofactor.LowPasswordChallenge;
import com.nickuc.login.bukkit.PacketLink;
import com.nickuc.login.config.LowSettingsDefinition;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LoudPremiumOption;
import com.nickuc.login.model.NoticeKind;
import com.github.retrooper.packetevents.protocol.dialog.CommonDialogData;
import com.github.retrooper.packetevents.protocol.dialog.DialogAction;
import com.github.retrooper.packetevents.protocol.dialog.MultiActionDialog;
import com.github.retrooper.packetevents.protocol.dialog.action.DynamicCustomAction;
import com.github.retrooper.packetevents.protocol.dialog.body.DialogBody;
import com.github.retrooper.packetevents.protocol.dialog.body.PlainMessage;
import com.github.retrooper.packetevents.protocol.dialog.body.PlainMessageDialogBody;
import com.github.retrooper.packetevents.protocol.dialog.button.ActionButton;
import com.github.retrooper.packetevents.protocol.dialog.button.CommonButtonData;
import com.github.retrooper.packetevents.protocol.dialog.input.Input;
import com.github.retrooper.packetevents.protocol.dialog.input.TextInputControl;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.configuration.server.WrapperConfigServerClearDialog;
import com.github.retrooper.packetevents.wrapper.configuration.server.WrapperConfigServerShowDialog;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerClearDialog;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerShowDialog;
import com.nickuc.login.platform.player.ChainedPlayerContract;
import com.nickuc.login.platform.packet.LoudPacketAdapter;
import com.nickuc.login.platform.connection.SecondaryConnectionContract;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.protocol.AccountAdapter;
import com.nickuc.login.protocol.DialogHandler;
import com.nickuc.login.protocol.PendingSettingsInterceptor;
import com.nickuc.login.protocol.SettingsInterceptor;
import com.nickuc.login.security.hashing.PasswordHasher;
import com.nickuc.login.session.MessageCoordinator;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.storage.spawn.SpawnState;
import io.netty.channel.Channel;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import net.kyori.adventure.text.TextComponent;

public class PasswordService {
   public final LowPasswordChallenge lowPasswordChallenge;
   private final PasswordStore passwordStore;
   public final PendingSettingsInterceptor pendingSettingsInterceptor;
   public final LowSettingsDefinition lowSettingsDefinition;
   public final PasswordHasher passwordHasher = new PasswordHasher(this);

   public PasswordService(PasswordStore target) {
      this.pendingSettingsInterceptor = new PendingSettingsInterceptor(this);
      this.lowSettingsDefinition = new LowSettingsDefinition();
      this.lowPasswordChallenge = new LowPasswordChallenge(this);
      this.passwordStore = target;
   }

   @Nullable
   private LoudPacketAdapter computeLoudPacketAdapter(User target, Channel input) {
      switch (this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState()) {
         case SILENT_PROXY_STATE:
            PacketLink data = (PacketLink)input.attr(PacketLink.attributeKey).get();
            if (data == null) {
               String value = "Unable to find connection cache for user " + target.getName() + " in CustomClickAction (PE) event";
               PasswordHashContainer.performMessage(value);
               this.executeUser(target);
               SettingsInterceptor.executeUser(
                  target,
                  SafeLoginService.resolveTextComponent(OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + value, "", "§ePlease contact an administrator."))
               );
               return null;
            }

            return data;
         case PENDING_SILENTPROXYSTATE:
         case ACTIVE_SILENTPROXYSTATE:
            AccountAdapter output = (AccountAdapter)input.attr(AccountAdapter.attributeKey).get();
            if (output == null) {
               String context = "Unable to find connection cache for user " + target.getName() + " in CustomClickAction (PE) event";
               PasswordHashContainer.performMessage(context);
               this.executeUser(target);
               SettingsInterceptor.executeUser(
                  target,
                  SafeLoginService.resolveTextComponent(OpenLocaleBarrier.loadMessage("§4[nLogin]", "", "§c" + context, "", "§ePlease contact an administrator."))
               );
               return null;
            }

            return output;
         default:
            throw new IllegalStateException("Platform " + this.passwordStore.findIncomingLoginGate().retrieveSilentProxyState() + " not implemented!");
      }
   }

   public void sendUser(User target, String input, boolean output, @Nullable ChainedPlayerContract context) {
      TextComponent data = SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.PRIMARY_FAST_LOUDPROXYSTATE, input, null));
      RemoteLoginBarrier value = RemoteLoginBarrier.loadRemoteLoginBarrier(
         new PlainMessageDialogBody(
            new PlainMessage(
               SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.PRIMARY_SAFE_LOUDPROXYSTATE, input, null)), 300
            )
         )
      );
      RemoteLoginBarrier result = RemoteLoginBarrier.loadRemoteLoginBarrier(
         new Input(
            "password",
            new TextInputControl(
               300,
               SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.PRIMARY_SECURE_LOUDPROXYSTATE, input, null)),
               true,
               "",
               64,
               null
            )
         )
      );
      RemoteLoginBarrier request;
      if (output) {
         request = RemoteLoginBarrier.loadRemoteLoginBarrier(
            new ActionButton(
               new CommonButtonData(
                  SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.PRIMARY_READY_LOUDPROXYSTATE, input, null)), null, 150
               ),
               new DynamicCustomAction(new ResourceLocation("nlogin", "login/recover"), null)
            ),
            new ActionButton(
               new CommonButtonData(
                  SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.PRIMARY_OPEN_LOUDPROXYSTATE, input, null)), null, 150
               ),
               new DynamicCustomAction(new ResourceLocation("nlogin", "login/confirm"), null)
            )
         );
      } else {
         request = RemoteLoginBarrier.loadRemoteLoginBarrier(
            new ActionButton(
               new CommonButtonData(
                  SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.PRIMARY_OPEN_LOUDPROXYSTATE, input, null)), null, 200
               ),
               new DynamicCustomAction(new ResourceLocation("nlogin", "login/confirm"), null)
            )
         );
      }

      ActionButton response = new ActionButton(
         new CommonButtonData(
            SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.PRIMARY_LIVE_LOUDPROXYSTATE, input, null)), null, 150
         ),
         new DynamicCustomAction(new ResourceLocation("nlogin", "login/exit"), null)
      );
      this.updateUser(target, data, value, result, request, response, context);
   }

   public void saveUser(User target, String input, @Nullable ChainedPlayerContract output) {
      TextComponent context = SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.MAIN_ACTIVE_LOUDPROXYSTATE, input, null));
      RemoteLoginBarrier data = RemoteLoginBarrier.loadRemoteLoginBarrier(
         new PlainMessageDialogBody(
            new PlainMessage(
               SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.MAIN_PENDING_LOUDPROXYSTATE, input, null)), 300
            )
         )
      );
      RemoteLoginBarrier value = RemoteLoginBarrier.loadRemoteLoginBarrier(
         new Input(
            "password1",
            new TextInputControl(
               300,
               SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.MAIN_CURRENT_LOUDPROXYSTATE, input, null)),
               true,
               "",
               64,
               null
            )
         ),
         new Input(
            "password2",
            new TextInputControl(
               300,
               SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.MAIN_PRIMARY_LOUDPROXYSTATE, input, null)),
               true,
               "",
               64,
               null
            )
         )
      );
      RemoteLoginBarrier result = RemoteLoginBarrier.loadRemoteLoginBarrier(
         new ActionButton(
            new CommonButtonData(
               SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.MAIN_LOCAL_LOUDPROXYSTATE, input, null)), null, 200
            ),
            new DynamicCustomAction(new ResourceLocation("nlogin", "register/confirm"), null)
         )
      );
      ActionButton request = new ActionButton(
         new CommonButtonData(
            SafeLoginService.resolveTextComponent(CachedSettingsGateway.loadMessage(LoudProxyState.MAIN_REMOTE_LOUDPROXYSTATE, input, null)), null, 150
         ),
         new DynamicCustomAction(new ResourceLocation("nlogin", "register/exit"), null)
      );
      this.updateUser(target, context, data, value, result, request, output);
   }

   private void executeUser(User target) {
      switch (target.getEncoderState()) {
         case CONFIGURATION:
            target.sendPacketSilently(new WrapperConfigServerClearDialog());
            break;
         case PLAY:
            target.sendPacketSilently(new WrapperPlayServerClearDialog());
            break;
         default:
            throw new IllegalArgumentException("Unsupported encoder state! " + target.getEncoderState());
      }
   }

   private void updateUser(
      User target, TextComponent input, List<DialogBody> output, List<Input> context, List<ActionButton> data, ActionButton value, @Nullable ChainedPlayerContract result
   ) {
      CommonDialogData request = new CommonDialogData(input, null, true, false, DialogAction.CLOSE, output, context);
      MultiActionDialog response = new MultiActionDialog(request, data, value, 2);
      switch (target.getEncoderState()) {
         case CONFIGURATION:
            target.sendPacketSilently(new WrapperConfigServerShowDialog(response));
            break;
         case PLAY:
            target.sendPacketSilently(new WrapperPlayServerShowDialog(response));
            break;
         default:
            throw new IllegalArgumentException("Unsupported encoder state! " + target.getEncoderState());
      }

      Channel source = (Channel)target.getChannel();
      source.attr(DialogHandler.attributeKey).set(new DialogHandler(result, (byte)1, null));
   }

   public boolean validateState(User target, ChainedPlayerContract input) {
      if (Pbkdf2Linker.retrieveNoticeKind() != NoticeKind.ACTIVE_NOTICEKIND) {
         return false;
      }

      if (this.passwordStore.fetchLocalSettingsRepository().loadState()) {
         return false;
      }

      if (target.getClientVersion().isOlderThan(ClientVersion.V_1_21_6)) {
         return false;
      }

      if (this.passwordStore.resolveRootMessageHandler().handleClientVersion(target).isOlderThan(ClientVersion.V_1_21_6)) {
         return false;
      }

      Channel output = (Channel)target.getChannel();
      LoudPacketAdapter context = this.computeLoudPacketAdapter(target, output);
      if (context == null) {
         return false;
      }

      SpawnLookup data = context.loadSpawnLookup();
      boolean value = data.findStateForState();
      if (!value) {
         if (QuickPremiumOption.PRIVATE_QUICKPREMIUMOPTION.retrieveState() && data.fetchStateAndState()) {
            return false;
         }

         if (this.passwordStore.findIndirectPasswordResolver().handleLoudPremiumOption(data, target.getAddress().getAddress().getHostAddress())
            == LoudPremiumOption.CURRENT_LOUDPREMIUMOPTION) {
            return false;
         }

         SecondaryConnectionContract result = this.passwordStore.loadInternalAccountHandler().retrieveSecondaryConnectionContract();
         int request = !context.findState() && (result == null || !result.isState(target.getUUID())) ? 0 : 1;
         if (QuickPremiumOption.VERIFIED_QUICKPREMIUMOPTION.retrieveState() && request != 0) {
            return false;
         }
      }

      if (value) {
         this.saveUser(target, context.loadMessage(), input);
      } else {
         this.sendUser(target, context.loadMessage(), MessageCoordinator.verifyState(this.passwordStore, data), input);
      }

      output.eventLoop()
         .schedule(
            () -> {
               try {
                  if (!output.isActive()) {
                     return;
                  }

                  if (!output.hasAttr(DialogHandler.attributeKey)) {
                     return;
                  }

                  DialogHandler dataValue = (DialogHandler)output.attr(DialogHandler.attributeKey).get();
                  if (dataValue != null) {
                     if (dataValue.chainedPlayerContract != null) {
                        this.executeUser(target);
                        SettingsInterceptor.handleUser(
                           target,
                           CachedSettingsGateway.computeMessage(value ? LoudProxyState.ACTIVE_SECURE_LOUDPROXYSTATE : LoudProxyState.ACTIVE_SAFE_LOUDPROXYSTATE)
                        );
                     }

                     return;
                  }
               } finally {
                  input.resume(false);
               }
            },
            SpawnState.ACTIVE_PENDING_SPAWNSTATE.r(),
            TimeUnit.SECONDS
         );
      return true;
   }
}
