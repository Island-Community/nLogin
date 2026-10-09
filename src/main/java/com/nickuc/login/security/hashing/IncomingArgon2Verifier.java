package com.nickuc.login.security.hashing;

import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

public class IncomingArgon2Verifier extends PasswordHashVerifier {
   public IncomingArgon2Verifier() {
      super(Argon2Factory.create(Argon2Types.ARGON2d));
   }
}
