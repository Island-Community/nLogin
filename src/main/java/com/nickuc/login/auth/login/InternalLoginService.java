package com.nickuc.login.auth.login;

import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.model.SecondaryPlatformCatalog;

public class InternalLoginService {
   static {
      try {
         values[SecondaryPlatformCatalog.ACTIVE_SECONDARYPLATFORMCATALOG.ordinal()] = 1;
      } catch (NoSuchFieldError context) {
      }

      try {
         values[SecondaryPlatformCatalog.PENDING_SECONDARYPLATFORMCATALOG.ordinal()] = 2;
      } catch (NoSuchFieldError output) {
      }

      activeValues = new int[StrictMessageKind.values().length];

      try {
         activeValues[StrictMessageKind.STRICT_MESSAGE_KIND.ordinal()] = 1;
      } catch (NoSuchFieldError input) {
      }

      try {
         activeValues[StrictMessageKind.ACTIVE_STRICTMESSAGEKIND.ordinal()] = 2;
      } catch (NoSuchFieldError target) {
      }
   }
}
