package com.nickuc.login.security.hashing;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Version;

public class BcryptDigest extends CurrentPasswordHashDigest {
   public BcryptDigest() {
      super(BCrypt.with(Version.VERSION_BC));
   }
}
