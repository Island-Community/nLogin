package com.nickuc.login.auth.login;

import com.nickuc.login.platform.server.VerifiedServerAdapter;


public abstract class RemoteLoginCheckpoint {
   public final VerifiedServerAdapter verifiedServerAdapter;

   public RemoteLoginCheckpoint(VerifiedServerAdapter target) {
      this.verifiedServerAdapter = target;
   }
}
