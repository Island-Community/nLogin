package com.nickuc.login.platform.server;

public interface AuthenticatedServerAdapter extends OutgoingSenderAdapter {
   void performMessage(String target);
}
