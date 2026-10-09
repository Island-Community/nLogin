package com.nickuc.login.security.hashing;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Version;

public class BusyBcryptDigest extends CurrentPasswordHashDigest {
   public BusyBcryptDigest() {
      super(BCrypt.with(Version.VERSION_2Y));
   }
}
