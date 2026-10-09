package com.nickuc.login.model;

import com.nickuc.login.auth.login.BusyLoginBarrier;
import com.nickuc.login.notification.NoticeSender;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.updater.MessageKind;
import javax.annotation.Nullable;


public enum PrivateLoginOption {
   PRIVATE_LOGIN_OPTION(MessageKind.MESSAGE_KIND),
   ACTIVE_PRIVATELOGINOPTION(MessageKind.ACTIVE_MESSAGEKIND),
   PENDING_PRIVATELOGINOPTION(MessageKind.PENDING_MESSAGEKIND),
   CURRENT_PRIVATELOGINOPTION(MessageKind.CURRENT_MESSAGEKIND),
   PRIMARY_PRIVATELOGINOPTION(MessageKind.PRIMARY_MESSAGEKIND),
   MAIN_PRIVATELOGINOPTION(MessageKind.MAIN_MESSAGEKIND),
   LOCAL_PRIVATELOGINOPTION(MessageKind.LOCAL_MESSAGEKIND),
   REMOTE_PRIVATELOGINOPTION(MessageKind.REMOTE_MESSAGEKIND),
   CACHED_PRIVATELOGINOPTION(MessageKind.CACHED_MESSAGEKIND),
   STORED_PRIVATELOGINOPTION(MessageKind.STORED_MESSAGEKIND),
   VERIFIED_PRIVATELOGINOPTION(MessageKind.VERIFIED_MESSAGEKIND),
   AUTHENTICATED_PRIVATELOGINOPTION(MessageKind.AUTHENTICATED_MESSAGEKIND),
   SHARED_PRIVATELOGINOPTION(MessageKind.SHARED_MESSAGEKIND),
   PRIVATE_PRIVATELOGINOPTION(MessageKind.PRIVATE_MESSAGEKIND),
   INTERNAL_PRIVATELOGINOPTION(MessageKind.INTERNAL_MESSAGEKIND),
   UPSTREAM_PRIVATELOGINOPTION(MessageKind.UPSTREAM_MESSAGEKIND);

   private final MessageKind messageKind;

   public int findCount() {
      return this.ordinal();
   }

   public NoticeSender computeNoticeSender(VerifiedServerAdapter target) {
      return CachedSettingsGateway.createNoticeSender(this.messageKind, target);
   }

   PrivateLoginOption(MessageKind output) {
      this.messageKind = output;
   }

   public BusyLoginBarrier handleBusyLoginBarrier(VerifiedServerAdapter target) {
      return new BusyLoginBarrier(this, this.computeNoticeSender(target));
   }

   @Nullable
   public static PrivateLoginOption processPrivateLoginOption(int instance) {
      PrivateLoginOption[] target = values();
      return instance >= 0 && instance < target.length ? target[instance] : null;
   }
}
