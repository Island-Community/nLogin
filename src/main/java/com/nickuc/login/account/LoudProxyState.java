package com.nickuc.login.account;

import com.nickuc.login.auth.login.BusyLoginProcessor;
import com.nickuc.login.auth.login.SecureLoginGate;
import com.nickuc.login.platform.session.SessionHandler;
import java.util.Collections;

public enum LoudProxyState implements SessionHandler {
   LOUD_PROXY_STATE("pending.login"),
   ACTIVE_LOUDPROXYSTATE("pending.register"),
   PENDING_LOUDPROXYSTATE("pending.action-bar.singular"),
   CURRENT_LOUDPROXYSTATE("pending.action-bar.plural"),
   PRIMARY_LOUDPROXYSTATE("pending.action-bar.singular-type"),
   MAIN_LOUDPROXYSTATE("pending.action-bar.plural-type"),
   LOCAL_LOUDPROXYSTATE("success.login"),
   REMOTE_LOUDPROXYSTATE("success.register"),
   CACHED_LOUDPROXYSTATE("success.session"),
   STORED_LOUDPROXYSTATE("success.premium"),
   VERIFIED_LOUDPROXYSTATE("success.bedrock"),
   AUTHENTICATED_LOUDPROXYSTATE("success.change-password"),
   SHARED_LOUDPROXYSTATE("success.reload"),
   PRIVATE_LOUDPROXYSTATE("success.kick.unregister"),
   INTERNAL_LOUDPROXYSTATE("success.kick.marked-offline"),
   UPSTREAM_LOUDPROXYSTATE("success.kick.challenge-premium"),
   INCOMING_LOUDPROXYSTATE("error.not-registered"),
   OUTGOING_LOUDPROXYSTATE("error.offline"),
   SECONDARY_LOUDPROXYSTATE("error.address-limit"),
   DIRECT_LOUDPROXYSTATE(new String[]{"error.database"}, false, "§cSorry, this database operation could not be completed. Check the console for more details."),
   LINKED_LOUDPROXYSTATE("error.command.usage"),
   ROOT_LOUDPROXYSTATE("error.command.no-permission"),
   TOP_LOUDPROXYSTATE("error.command.player-required"),
   FAST_LOUDPROXYSTATE("error.command.console-required"),
   SAFE_LOUDPROXYSTATE("error.command.account-collision", true),
   SECURE_LOUDPROXYSTATE("error.command.arguments.password"),
   OPEN_LOUDPROXYSTATE("error.command.arguments.current-password"),
   READY_LOUDPROXYSTATE("error.command.arguments.new-password"),
   LIVE_LOUDPROXYSTATE("error.already.login"),
   ACTIVE_PENDING_LOUDPROXYSTATE("error.already.login-other"),
   ACTIVE_CURRENT_LOUDPROXYSTATE("error.already.registered"),
   ACTIVE_PRIMARY_LOUDPROXYSTATE("error.already.premium"),
   ACTIVE_MAIN_LOUDPROXYSTATE("error.password.same-as-current"),
   ACTIVE_LOCAL_LOUDPROXYSTATE("error.password.too-large"),
   ACTIVE_REMOTE_LOUDPROXYSTATE("error.password.too-small"),
   ACTIVE_CACHED_LOUDPROXYSTATE("error.password.not-secure"),
   ACTIVE_STORED_LOUDPROXYSTATE("error.password.do-not-match"),
   ACTIVE_VERIFIED_LOUDPROXYSTATE("error.password.require-confirmation"),
   ACTIVE_AUTHENTICATED_LOUDPROXYSTATE("error.password.incorrect"),
   ACTIVE_SHARED_LOUDPROXYSTATE("error.kick.incorrect-password"),
   ACTIVE_PRIVATE_LOUDPROXYSTATE("error.kick.wait-server-start"),
   ACTIVE_INTERNAL_LOUDPROXYSTATE("error.kick.account-blocked"),
   ACTIVE_UPSTREAM_LOUDPROXYSTATE("error.kick.account-unregistered"),
   ACTIVE_INCOMING_LOUDPROXYSTATE("error.kick.invalid-nickname"),
   ACTIVE_OUTGOING_LOUDPROXYSTATE("error.kick.bedrock-nickname"),
   ACTIVE_SECONDARY_LOUDPROXYSTATE("error.kick.bedrock-unlinked"),
   ACTIVE_DIRECT_LOUDPROXYSTATE("error.kick.transferred-nickname"),
   ACTIVE_LINKED_LOUDPROXYSTATE("error.kick.already-online"),
   ACTIVE_ROOT_LOUDPROXYSTATE("error.kick.unknown-hostname"),
   ACTIVE_TOP_LOUDPROXYSTATE("error.kick.premium-not-logged"),
   ACTIVE_FAST_LOUDPROXYSTATE("error.kick.premium-challenge"),
   ACTIVE_SAFE_LOUDPROXYSTATE("error.kick.timeout.login"),
   ACTIVE_SECURE_LOUDPROXYSTATE("error.kick.timeout.register"),
   ACTIVE_OPEN_LOUDPROXYSTATE("notification.all-viewed"),
   ACTIVE_READY_LOUDPROXYSTATE("notification.premium-question", true),
   ACTIVE_LIVE_LOUDPROXYSTATE("notification.premium-transfer-question", true),
   PENDING_ACTIVE_LOUDPROXYSTATE("notification.premium-transfer-confirmation", true),
   PENDING_CURRENT_LOUDPROXYSTATE("notification.password-update", true),
   PENDING_PRIMARY_LOUDPROXYSTATE("2fa.not-registered"),
   PENDING_MAIN_LOUDPROXYSTATE("2fa.already-registered"),
   PENDING_LOCAL_LOUDPROXYSTATE("2fa.register-limit"),
   PENDING_REMOTE_LOUDPROXYSTATE("2fa.unregistered"),
   PENDING_CACHED_LOUDPROXYSTATE("2fa.incorrect-code"),
   PENDING_STORED_LOUDPROXYSTATE("2fa.rate-limit"),
   PENDING_VERIFIED_LOUDPROXYSTATE("2fa.confirmed-account"),
   PENDING_AUTHENTICATED_LOUDPROXYSTATE("2fa.confirmation-message-sent"),
   PENDING_SHARED_LOUDPROXYSTATE("2fa.command-delay"),
   PENDING_PRIVATE_LOUDPROXYSTATE("2fa.help-command", true),
   PENDING_INTERNAL_LOUDPROXYSTATE("2fa.recover-command", true),
   PENDING_UPSTREAM_LOUDPROXYSTATE("2fa.switch.enabled"),
   PENDING_INCOMING_LOUDPROXYSTATE("2fa.switch.disabled"),
   PENDING_OUTGOING_LOUDPROXYSTATE("2fa.recover.not-equal"),
   PENDING_SECONDARY_LOUDPROXYSTATE("2fa.recover.message-sent"),
   PENDING_DIRECT_LOUDPROXYSTATE("2fa.recover.change-password"),
   PENDING_LINKED_LOUDPROXYSTATE("2fa.discord.account-linked"),
   PENDING_ROOT_LOUDPROXYSTATE("2fa.discord.confirm-account", true),
   PENDING_TOP_LOUDPROXYSTATE("2fa.discord.warn-message", true),
   PENDING_FAST_LOUDPROXYSTATE("2fa.discord.discord-required", true),
   PENDING_SAFE_LOUDPROXYSTATE("2fa.discord.address-changed", true),
   PENDING_SECURE_LOUDPROXYSTATE("2fa.discord.dm.configure.too-recent.title"),
   PENDING_OPEN_LOUDPROXYSTATE("2fa.discord.dm.configure.too-recent.description"),
   PENDING_READY_LOUDPROXYSTATE("2fa.discord.dm.configure.too-recent.image"),
   PENDING_LIVE_LOUDPROXYSTATE("2fa.discord.dm.configure.too-recent.color"),
   CURRENT_ACTIVE_LOUDPROXYSTATE("2fa.discord.dm.configure.invalid.title"),
   CURRENT_PENDING_LOUDPROXYSTATE("2fa.discord.dm.configure.invalid.description"),
   CURRENT_PRIMARY_LOUDPROXYSTATE("2fa.discord.dm.configure.invalid.image"),
   CURRENT_MAIN_LOUDPROXYSTATE("2fa.discord.dm.configure.invalid.color"),
   CURRENT_LOCAL_LOUDPROXYSTATE("2fa.discord.dm.configure.success.title"),
   CURRENT_REMOTE_LOUDPROXYSTATE("2fa.discord.dm.configure.success.description"),
   CURRENT_CACHED_LOUDPROXYSTATE("2fa.discord.dm.configure.success.image"),
   CURRENT_STORED_LOUDPROXYSTATE("2fa.discord.dm.configure.success.color"),
   CURRENT_VERIFIED_LOUDPROXYSTATE("2fa.discord.dm.verify.title"),
   CURRENT_AUTHENTICATED_LOUDPROXYSTATE("2fa.discord.dm.verify.description"),
   CURRENT_SHARED_LOUDPROXYSTATE("2fa.discord.dm.verify.image"),
   CURRENT_PRIVATE_LOUDPROXYSTATE("2fa.discord.dm.verify.color"),
   CURRENT_INTERNAL_LOUDPROXYSTATE("2fa.discord.dm.verify.allow"),
   CURRENT_UPSTREAM_LOUDPROXYSTATE("2fa.discord.dm.verify.deny"),
   CURRENT_INCOMING_LOUDPROXYSTATE("2fa.discord.dm.recover.title"),
   CURRENT_OUTGOING_LOUDPROXYSTATE("2fa.discord.dm.recover.description"),
   CURRENT_SECONDARY_LOUDPROXYSTATE("2fa.discord.dm.recover.image"),
   CURRENT_DIRECT_LOUDPROXYSTATE("2fa.discord.dm.recover.color"),
   CURRENT_LINKED_LOUDPROXYSTATE("2fa.discord.dm.recover.allow"),
   CURRENT_ROOT_LOUDPROXYSTATE("2fa.discord.dm.recover.deny"),
   CURRENT_TOP_LOUDPROXYSTATE("2fa.discord.dm.new-password.title"),
   CURRENT_FAST_LOUDPROXYSTATE("2fa.discord.dm.new-password.description"),
   CURRENT_SAFE_LOUDPROXYSTATE("2fa.discord.dm.new-password.image"),
   CURRENT_SECURE_LOUDPROXYSTATE("2fa.discord.dm.new-password.color"),
   CURRENT_OPEN_LOUDPROXYSTATE("2fa.discord.dm.password-changed.title"),
   CURRENT_READY_LOUDPROXYSTATE("2fa.discord.dm.password-changed.description"),
   CURRENT_LIVE_LOUDPROXYSTATE("2fa.discord.dm.password-changed.image"),
   PRIMARY_ACTIVE_LOUDPROXYSTATE("2fa.discord.dm.password-changed.color"),
   PRIMARY_PENDING_LOUDPROXYSTATE("2fa.email.invalid-email"),
   PRIMARY_CURRENT_LOUDPROXYSTATE("2fa.email.not-allowed"),
   PRIMARY_MAIN_LOUDPROXYSTATE("2fa.email.server-email"),
   PRIMARY_LOCAL_LOUDPROXYSTATE("2fa.email.warn-message", true),
   PRIMARY_REMOTE_LOUDPROXYSTATE("2fa.email.email-required", true),
   PRIMARY_CACHED_LOUDPROXYSTATE("2fa.email.address-changed", true),
   PRIMARY_STORED_LOUDPROXYSTATE(
      new String[]{"2fa.email.smtp.configure.subject", "2fa.email.stmp.configure.subject", "2fa.email.stmp.email-confirmation.subject"}
   ),
   PRIMARY_VERIFIED_LOUDPROXYSTATE(
      new String[]{"2fa.email.smtp.configure.content", "2fa.email.stmp.configure.content", "2fa.email.stmp.email-confirmation.content"}, true
   ),
   PRIMARY_AUTHENTICATED_LOUDPROXYSTATE(new String[]{"2fa.email.smtp.verify.subject", "2fa.email.stmp.verify.subject"}),
   PRIMARY_SHARED_LOUDPROXYSTATE(new String[]{"2fa.email.smtp.verify.content", "2fa.email.stmp.verify.content"}, true),
   PRIMARY_PRIVATE_LOUDPROXYSTATE(new String[]{"2fa.email.smtp.recover.subject", "2fa.email.stmp.recover.subject", "2fa.email.stmp.new-password.subject"}),
   PRIMARY_INTERNAL_LOUDPROXYSTATE(
      new String[]{"2fa.email.smtp.recover.content", "2fa.email.stmp.recover.content", "2fa.email.stmp.new-password.content"}, true
   ),
   PRIMARY_UPSTREAM_LOUDPROXYSTATE(new String[]{"2fa.email.smtp.new-password.subject", "2fa.email.stmp.new-password.subject"}),
   PRIMARY_INCOMING_LOUDPROXYSTATE(new String[]{"2fa.email.smtp.new-password.content", "2fa.email.stmp.new-password.content"}, true),
   PRIMARY_OUTGOING_LOUDPROXYSTATE(new String[]{"2fa.email.smtp.password-changed.subject", "2fa.email.stmp.password-changed.subject"}),
   PRIMARY_SECONDARY_LOUDPROXYSTATE(new String[]{"2fa.email.smtp.password-changed.content", "2fa.email.stmp.password-changed.content"}, true),
   PRIMARY_DIRECT_LOUDPROXYSTATE("bedrock.forms.login.title"),
   PRIMARY_LINKED_LOUDPROXYSTATE("bedrock.forms.login.content", true),
   PRIMARY_ROOT_LOUDPROXYSTATE("bedrock.forms.register.title"),
   PRIMARY_TOP_LOUDPROXYSTATE("bedrock.forms.register.content", true),
   PRIMARY_FAST_LOUDPROXYSTATE("dialogs.login.title"),
   PRIMARY_SAFE_LOUDPROXYSTATE("dialogs.login.body", true),
   PRIMARY_SECURE_LOUDPROXYSTATE("dialogs.login.inputs.password"),
   PRIMARY_OPEN_LOUDPROXYSTATE("dialogs.login.buttons.login"),
   PRIMARY_READY_LOUDPROXYSTATE("dialogs.login.buttons.recover"),
   PRIMARY_LIVE_LOUDPROXYSTATE("dialogs.login.buttons.cancel"),
   MAIN_ACTIVE_LOUDPROXYSTATE("dialogs.register.title"),
   MAIN_PENDING_LOUDPROXYSTATE("dialogs.register.body", true),
   MAIN_CURRENT_LOUDPROXYSTATE("dialogs.register.inputs.password1"),
   MAIN_PRIMARY_LOUDPROXYSTATE("dialogs.register.inputs.password2"),
   MAIN_LOCAL_LOUDPROXYSTATE("dialogs.register.buttons.register"),
   MAIN_REMOTE_LOUDPROXYSTATE("dialogs.register.buttons.cancel");

   public final BusyLoginProcessor busyLoginProcessor;
   public final boolean enabled;
   public final Object object;

   @Override
   public SecureLoginGate loadSecureLoginGate() {
      throw new UnsupportedOperationException();
   }

   LoudProxyState(String output) {
      this(new String[]{output});
   }

   LoudProxyState(String[] output, boolean context, Object data) {
      this.busyLoginProcessor = BusyLoginProcessor.handleBusyLoginProcessor(output);
      this.enabled = context;
      if (data != null) {
         this.object = data;
      } else {
         String value = this.busyLoginProcessor.fetchNames()[0];
         this.object = context
            ? Collections.singletonList("§cThis message list (with key \"" + value + "\") was not found.")
            : "§cThis message (with key \"" + value + "\") was not found.";
      }
   }

   LoudProxyState(String[] output) {
      this.busyLoginProcessor = BusyLoginProcessor.handleBusyLoginProcessor(output);
      this.enabled = false;
      this.object = "§cThis message (with key \"" + output[0] + "\") was not found.";
   }

   @Override
   public Object getObject() {
      return this.object;
   }

   @Override
   public BusyLoginProcessor retrieveBusyLoginProcessor() {
      return this.busyLoginProcessor;
   }

   @Override
   public int fetchCount() {
      return this.ordinal();
   }

   LoudProxyState(String output, boolean context) {
      this(new String[]{output}, context, null);
   }

   LoudProxyState(String[] output, boolean context) {
      this(output, context, null);
   }
}
