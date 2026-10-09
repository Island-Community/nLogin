package com.nickuc.login.security.hashing;

public final class Sha256Hasher extends DeadPasswordHashHasher {
   public Sha256Hasher() {
      super("SHA-256", "SHA256");
   }
}
