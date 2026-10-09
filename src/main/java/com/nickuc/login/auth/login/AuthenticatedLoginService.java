package com.nickuc.login.auth.login;

import com.nickuc.login.platform.command.SilentCommandHandler;
import com.nickuc.login.proxy.BungeeLink;

public class AuthenticatedLoginService {
   private static SilentCommandHandler silentCommandHandler;

   public static SilentCommandHandler resolveSilentCommandHandler() {
      return silentCommandHandler;
   }

   static {
      try {
         silentCommandHandler = new BungeeLink();
      } catch (ReflectiveOperationException target) {
      }

      if (silentCommandHandler == null) {
         silentCommandHandler = (instance, targetValue) -> {};
      }
   }
}
