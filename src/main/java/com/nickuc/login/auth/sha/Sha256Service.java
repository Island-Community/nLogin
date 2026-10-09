package com.nickuc.login.auth.sha;

import com.nickuc.login.security.hashing.DeadPasswordHashHasher;

public final class Sha256Service extends DeadPasswordHashHasher {
   public Sha256Service() {
      super("SHA-512", "SHA512");
   }
}
