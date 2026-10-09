package com.nickuc.login.platform.account;

import com.nickuc.login.storage.password.PasswordStore;

public interface IncomingAccountHandler {
   void processPasswordStore(PasswordStore target, boolean input);
}
