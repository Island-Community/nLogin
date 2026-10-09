package com.nickuc.login.auth.login;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.storage.session.SharedSessionTable;

public class SharedLoginService extends SharedSessionTable {
   public SharedLoginService(CachedProxyCatalog target) {
      super(target, StrictMessageKind.ACTIVE_STRICTMESSAGEKIND, LoudProxyState.PRIMARY_CACHED_LOUDPROXYSTATE);
   }
}
