package com.nickuc.login.security.hashing;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Version;

public class BcryptVerifier extends CurrentPasswordHashDigest {
   public BcryptVerifier() {
      super(BCrypt.with(Version.VERSION_2A));
   }
}
