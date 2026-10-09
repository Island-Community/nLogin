package com.nickuc.login.platform.server;

import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.premium.FloodgateResolver;
import com.nickuc.login.premium.SettingsLinker;

public interface ServerAdapter {
   boolean retrieveState();

   SettingsLinker resolveSettingsLinker();

   void performTask();

   nLoginAPI loadNLoginAPI();

   InternalAccountHandler getInternalAccountHandler();

   void executeTask();

   FloodgateResolver fetchFloodgateResolver();
}
