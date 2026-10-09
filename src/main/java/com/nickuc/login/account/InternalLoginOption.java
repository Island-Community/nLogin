package com.nickuc.login.account;

import com.nickuc.login.command.CompletionAdminCommand;
import com.nickuc.login.command.LocaleHandler;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.command.auth.LowPasswordHandler;
import com.nickuc.login.command.auth.ParentPasswordAction;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.notification.SafeNoticeRenderer;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.BedrockResolver;
import com.nickuc.login.premium.LowLocaleVerifier;
import com.nickuc.login.security.hashing.PasswordDigest;
import com.nickuc.login.security.hashing.QuickPasswordDigest;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.MessageCoordinator;
import com.nickuc.login.storage.locale.LocaleCollection;
import com.nickuc.login.storage.password.PasswordStore;


public enum InternalLoginOption {
   INTERNAL_LOGIN_OPTION("nlogin", LowLocaleVerifier.class, true, true),
   ACTIVE_INTERNALLOGINOPTION("changepass", LowPasswordHandler.class, true, true),
   PENDING_INTERNALLOGINOPTION("discord", CompletionAdminCommand.class, true, true),
   CURRENT_INTERNALLOGINOPTION("email", QuickPasswordDigest.class, true, true),
   PRIMARY_INTERNALLOGINOPTION("login", ParentPasswordAction.class, true, false),
   MAIN_INTERNALLOGINOPTION("offline", SafeNoticeRenderer.class, true, true),
   LOCAL_INTERNALLOGINOPTION("premium", BedrockResolver.class, true, true),
   REMOTE_INTERNALLOGINOPTION("recover", MessageCoordinator.class, true, false),
   CACHED_INTERNALLOGINOPTION("register", LocaleHandler.class, true, false),
   STORED_INTERNALLOGINOPTION("unregister", PasswordDigest.class, false, false);

   private final String name;
   private final PasswordHashCommand<PasswordStore> passwordHashCommand;
   private final boolean enabled;
   private final boolean activeEnabled;

   public String findMessage() {
      return this.name;
   }

   InternalLoginOption(String output, Class<? extends PasswordHashCommand<PasswordStore>> context, boolean data, boolean value) {
      this.name = output;
      this.enabled = data;
      this.activeEnabled = value;

      PasswordHashCommand result;
      try {
         result = (PasswordHashCommand)context.getConstructor(InternalLoginOption.class).newInstance(this);
      } catch (ReflectiveOperationException response) {
         PasswordHashContainer.handleMessage("Failed to build command '" + this.name() + "'.", response);
         result = null;
      }

      this.passwordHashCommand = result;
   }

   public void performVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, String... output) {
      if (!(this.passwordHashCommand instanceof LocaleCollection)) {
         throw new IllegalStateException("The command " + this + " cannot be spoofed!");
      }

      input.updateLenientMessageKind(LenientMessageKind.VERIFIED_LENIENTMESSAGEKIND, System.currentTimeMillis());
      LocaleCollection context = (LocaleCollection)this.passwordHashCommand;
      context.performOutgoingSenderAdapter(target, context.loadMessage(), output);
   }

   public boolean retrieveState() {
      return this.activeEnabled;
   }

   public PasswordHashCommand<PasswordStore> fetchPasswordHashCommand() {
      return this.passwordHashCommand;
   }

   public void performVerifiedServerAdapter(VerifiedServerAdapter target, LimboCoordinator input, String output, String... context) {
      if (!(this.passwordHashCommand instanceof LocaleCollection)) {
         throw new IllegalStateException("The command " + this + " cannot be spoofed!");
      }

      input.updateLenientMessageKind(LenientMessageKind.VERIFIED_LENIENTMESSAGEKIND, System.currentTimeMillis());
      LocaleCollection data = (LocaleCollection)this.passwordHashCommand;
      data.dispatchOutgoingSenderAdapter(target, output, context);
   }

   public boolean resolveState() {
      return this.enabled;
   }
}
