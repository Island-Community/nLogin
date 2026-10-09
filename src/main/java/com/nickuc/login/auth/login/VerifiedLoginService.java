package com.nickuc.login.auth.login;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.storage.session.SessionTable;

public class VerifiedLoginService extends SessionTable {
   public VerifiedLoginService(CachedProxyCatalog target) {
      super(target, StrictMessageKind.STRICT_MESSAGE_KIND, LoudProxyState.PENDING_FAST_LOUDPROXYSTATE);
   }
}
