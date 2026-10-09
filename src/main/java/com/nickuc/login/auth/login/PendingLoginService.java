package com.nickuc.login.auth.login;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.storage.session.PendingSessionDao;

public class PendingLoginService extends PendingSessionDao {
   public PendingLoginService(CachedProxyCatalog target) {
      super(target, StrictMessageKind.STRICT_MESSAGE_KIND, LoudProxyState.PENDING_TOP_LOUDPROXYSTATE);
   }
}
