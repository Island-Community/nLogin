package com.nickuc.login.auth.login;

import com.nickuc.login.account.CachedProxyCatalog;
import com.nickuc.login.account.LoudProxyState;
import com.nickuc.login.account.StrictMessageKind;
import com.nickuc.login.storage.session.SessionTable;

public class UpstreamLoginService extends SessionTable {
   public UpstreamLoginService(CachedProxyCatalog target) {
      super(target, StrictMessageKind.ACTIVE_STRICTMESSAGEKIND, LoudProxyState.PRIMARY_REMOTE_LOUDPROXYSTATE);
   }
}
