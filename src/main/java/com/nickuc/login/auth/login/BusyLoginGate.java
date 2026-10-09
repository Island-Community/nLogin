package com.nickuc.login.auth.login;

import com.nickuc.login.platform.server.VerifiedServerAdapter;

public class BusyLoginGate extends RemoteLoginCheckpoint {
   private final byte[] values;

   public BusyLoginGate(VerifiedServerAdapter target, byte[] input) {
      super(target);
      this.values = input;
   }
}
