package com.nickuc.login.proxy;

import com.nickuc.login.protocol.ChainedSessionHandler;
import com.nickuc.login.security.hashing.PasswordHashDigest;
import net.md_5.bungee.protocol.DefinedPacket;

public class BungeeForwarder implements ChainedSessionHandler {
   public BungeeForwarder(PasswordHashDigest target) {
      this.passwordHashDigest = target;
   }

   @Override
   public void sendPacket(Object target, Object... input) {
      if (PasswordHashDigest.computeProxiedPlayer(this.passwordHashDigest).isConnected()) {
         PasswordHashDigest.computeProxiedPlayer(this.passwordHashDigest).unsafe().sendPacket((DefinedPacket)target);

         for (Object value : input) {
            PasswordHashDigest.computeProxiedPlayer(this.passwordHashDigest).unsafe().sendPacket((DefinedPacket)value);
         }
      }
   }
}
