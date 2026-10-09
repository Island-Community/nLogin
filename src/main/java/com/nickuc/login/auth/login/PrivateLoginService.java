package com.nickuc.login.auth.login;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.storage.session.SharedSessionTable;

public class PrivateLoginService extends SharedSessionTable {
   public PrivateLoginService(CachedProxyCatalog target) {
      super(target, StrictMessageKind.STRICT_MESSAGE_KIND, LoudProxyState.PENDING_SAFE_LOUDPROXYSTATE);
   }
}
