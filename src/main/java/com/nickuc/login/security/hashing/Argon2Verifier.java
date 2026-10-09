package com.nickuc.login.security.hashing;

import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

public class Argon2Verifier extends PasswordHashVerifier {
   public Argon2Verifier() {
      super(Argon2Factory.create(Argon2Types.ARGON2i));
   }
}
