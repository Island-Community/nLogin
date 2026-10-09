package com.nickuc.login.storage.locale;

import com.nickuc.login.account.InternalLoginOption;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.api.enums.event.CommandType;
import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.event.internal.CancellableEvent;
import com.nickuc.login.command.LocaleHandler;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.command.auth.LowPasswordHandler;
import com.nickuc.login.command.auth.ParentPasswordAction;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.TightPlatformCatalog;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.security.hashing.PasswordDigest;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class LocaleCollection extends PasswordHashCommand<PasswordStore> {
   public final InternalLoginOption internalLoginOption;

   @Override
   public boolean verifyState(OutgoingSenderAdapter target, String input, String[] output, boolean context) {
      return !context || super.verifyState(target, input, output, true);
   }

   @Override
   public final List<String> computeCollection(OutgoingSenderAdapter target, String input, String[] output) {
      return this.buildCollection(target, input, output);
   }

   public List<String> buildCollection(OutgoingSenderAdapter target, String input, String[] output) {
      return null;
   }

   public LocaleCollection(InternalLoginOption target) {
      super(target.findMessage());
      this.internalLoginOption = target;
   }

   public abstract void executeOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output);

   @Override
   public final void saveOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (target instanceof VerifiedServerAdapter) {
         VerifiedServerAdapter context = (VerifiedServerAdapter)target;
         LimboRegistry data = this.indirectSessionHandler.loadLimboRegistry();
         LimboCoordinator value = data.loadLimboCoordinator(context);
         Long result = value.loadObject(LenientMessageKind.VERIFIED_LENIENTMESSAGEKIND);
         if (result == null || System.currentTimeMillis() - result > 2500L) {
            return;
         }

         String[] request = value.loadObject(LenientMessageKind.STORED_LENIENTMESSAGEKIND);
         if (request != null) {
            output = request;
         }

         TightPlatformCatalog response = value.loadTightPlatformCatalog();
         if (response.canState(TightPlatformCatalog.PENDING_TIGHTPLATFORMCATALOG)
            || (response == TightPlatformCatalog.PENDING_TIGHTPLATFORMCATALOG || response == TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG)
               && !this.internalLoginOption.retrieveState()) {
            return;
         }

         if (!super.verifyState(target, input, output, false)) {
            CachedSettingsGateway.executeOutgoingSenderAdapter(target, LoudProxyState.ROOT_LOUDPROXYSTATE);
            return;
         }

         CommandType source = null;
         byte entry = 0;
         if (this instanceof ParentPasswordAction) {
            source = CommandType.LOGIN;
            entry = 1;
         } else if (this instanceof LocaleHandler) {
            source = CommandType.REGISTER;
            entry = 2;
         } else if (this instanceof LowPasswordHandler) {
            source = CommandType.CHANGE_PASSWORD;
            entry = 2;
         } else if (this instanceof PasswordDigest) {
            source = CommandType.UNREGISTER;
            entry = 1;
         }

         int record = response != TightPlatformCatalog.MAIN_TIGHTPLATFORMCATALOG && !this.internalLoginOption.resolveState() && !data.canState(context) ? 1 : 0;
         if (source != null) {
            String[] item = output.length <= entry ? new String[0] : Arrays.copyOfRange(output, entry, output.length);
            CancellableEvent element = this.indirectSessionHandler.loadObject(EventEnum.PRE_COMMAND_EXECUTE, context, source, input, item);
            element.setCancelled((boolean)record);
            record = !this.indirectSessionHandler.callEvent(element) ? 1 : 0;
         }

         if (record != 0) {
            return;
         }
      }

      this.executeOutgoingSenderAdapter(target, input.toLowerCase(Locale.ENGLISH), output);
   }
}
