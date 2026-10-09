package com.nickuc.login.protocol;

public interface ChainedSessionHandler {
   void sendPacket(Object target, Object... input);
}
