package com.nickuc.login.platform.account;

import com.nickuc.login.storage.login.LoginCollection;

public interface InternalAccountHandler extends IncomingAccountHandler {
   void processTask();

   void executeTask();

   LoginCollection getLoginCollection();

   SecondaryConnectionContract retrieveSecondaryConnectionContract();
}
