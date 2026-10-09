package com.nickuc.login.platform.command;

import com.nickuc.login.auth.login.AuthenticatedLoginService;
import org.bukkit.entity.Player;

public interface SilentCommandHandler {
   default void dispatchPlayer(Player target) {
      this.send(target, "");
   }

   void send(Player target, String input);

   static SilentCommandHandler resolveSilentCommandHandler() {
      return AuthenticatedLoginService.resolveSilentCommandHandler();
   }
}
