package com.nickuc.login.auth.login;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.storage.session.PendingSessionDao;

public class OpenLoginService extends PendingSessionDao {
   public OpenLoginService(CachedProxyCatalog target) {
      super(target, StrictMessageKind.ACTIVE_STRICTMESSAGEKIND, LoudProxyState.PRIMARY_LOCAL_LOUDPROXYSTATE);
   }
}
