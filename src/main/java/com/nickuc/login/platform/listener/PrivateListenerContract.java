package com.nickuc.login.platform.listener;

import com.nickuc.login.auth.login.VerifiedLoginGate;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;

@FunctionalInterface
public interface PrivateListenerContract {
   VerifiedLoginGate doRequest(PendingPasswordHashHasher target, String input);
}
