package com.nickuc.login.security.hashing;

import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

public class Argon2Provider extends PasswordHashVerifier {
   public Argon2Provider() {
      super(Argon2Factory.create(Argon2Types.ARGON2id));
   }
}
