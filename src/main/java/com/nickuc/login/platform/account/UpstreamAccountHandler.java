package com.nickuc.login.platform.account;

import com.nickuc.login.storage.password.PasswordStore;

public interface UpstreamAccountHandler extends LoudPlayerContract {
   boolean at();

   default boolean loadState() {
      return true;
   }

   boolean canState(PasswordStore target);
}
