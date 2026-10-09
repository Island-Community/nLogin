package com.nickuc.login.model;

import com.nickuc.login.auth.sha.IncomingSha256Barrier;
import com.nickuc.login.auth.sha.Sha256Gate;
import com.nickuc.login.auth.sha.Sha256Processor;
import com.nickuc.login.auth.sha.Sha256Service;
import com.nickuc.login.auth.login.SharedLoginGate;
import com.nickuc.login.platform.player.IndirectPlayerContract;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.security.hashing.Argon2Provider;
import com.nickuc.login.security.hashing.Argon2Verifier;
import com.nickuc.login.security.hashing.BcryptDigest;
import com.nickuc.login.security.hashing.BcryptVerifier;
import com.nickuc.login.security.hashing.BusyBcryptDigest;
import com.nickuc.login.security.hashing.IncomingArgon2Verifier;
import com.nickuc.login.security.hashing.PasswordVerifier;
import com.nickuc.login.security.hashing.Pbkdf2Hasher;
import com.nickuc.login.security.hashing.PendingPasswordHashDigest;
import com.nickuc.login.security.hashing.Sha256Hasher;
import com.nickuc.login.security.hashing.Sha256Provider;
import com.nickuc.login.storage.sha.Sha256Collection;
import java.util.Locale;
import javax.annotation.Nullable;


public enum UpstreamSpawnState {
   UPSTREAM_SPAWN_STATE(Argon2Verifier.class),
   ACTIVE_UPSTREAMSPAWNSTATE(IncomingArgon2Verifier.class),
   PENDING_UPSTREAMSPAWNSTATE(Argon2Provider.class),
   CURRENT_UPSTREAMSPAWNSTATE(Pbkdf2Hasher.class),
   PRIMARY_UPSTREAMSPAWNSTATE(BcryptVerifier.class),
   MAIN_UPSTREAMSPAWNSTATE(BusyBcryptDigest.class),
   LOCAL_UPSTREAMSPAWNSTATE(Sha256Service.class),
   REMOTE_UPSTREAMSPAWNSTATE(Sha256Hasher.class),
   CACHED_UPSTREAMSPAWNSTATE(PendingPasswordHashDigest.class),
   STORED_UPSTREAMSPAWNSTATE(false, BcryptDigest.class),
   VERIFIED_UPSTREAMSPAWNSTATE(false, IncomingSha256Barrier.class),
   AUTHENTICATED_UPSTREAMSPAWNSTATE(false, Sha256Provider.class),
   SHARED_UPSTREAMSPAWNSTATE(false, PasswordVerifier.class),
   PRIVATE_UPSTREAMSPAWNSTATE(false, Sha256Processor.class),
   INTERNAL_UPSTREAMSPAWNSTATE(false, Sha256Collection.class),
   UPSTREAM_UPSTREAMSPAWNSTATE(false, Sha256Gate.class);

   private final boolean enabled;
   private final Class<? extends IndirectPlayerContract> value;
   private IndirectPlayerContract indirectPlayerContract;

   @Nullable
   public static UpstreamSpawnState createUpstreamSpawnState(String instance) {
      if (instance == null) {
         return null;
      }

      String[] target = instance.split("\\$");
      UpstreamSpawnState input = null;
      return target.length > 1
            && (input = loadUpstreamSpawnState(target[1])) == null
            && (input = loadUpstreamSpawnState(target[0])) == null
            && instance.endsWith("$LOCKLOGIN")
         ? AUTHENTICATED_UPSTREAMSPAWNSTATE
         : input;
   }

   public IndirectPlayerContract loadIndirectPlayerContract() {
      switch (this) {
         case UPSTREAM_SPAWN_STATE:
         case ACTIVE_UPSTREAMSPAWNSTATE:
         case PENDING_UPSTREAMSPAWNSTATE:
            if (!Pbkdf2Linker.retrieveState()) {
               throw new UnsupportedOperationException(this + " is unavailable for your current system");
            }
         case PRIMARY_UPSTREAMSPAWNSTATE:
            if (Pbkdf2Linker.fetchState()) {
               return SharedLoginGate.fetchSharedLoginGate();
            }
         case MAIN_UPSTREAMSPAWNSTATE:
            if (Pbkdf2Linker.fetchState()) {
               throw new UnsupportedOperationException(this + " is unavailable for your current system");
            }
         case CURRENT_UPSTREAMSPAWNSTATE:
         default:
            if (this.indirectPlayerContract != null) {
               return this.indirectPlayerContract;
            } else {
               try {
                  return this.indirectPlayerContract = this.value.getConstructor().newInstance();
               } catch (ReflectiveOperationException input) {
                  throw new RuntimeException("Failed to instantiate " + this + " implementation", input);
               }
            }
      }
   }

   public Class<? extends IndirectPlayerContract> loadClass() {
      return this.value;
   }

   @Nullable
   private static UpstreamSpawnState loadUpstreamSpawnState(String instance) {
      switch (instance.toUpperCase(Locale.ENGLISH)) {
         case "2Y":
            return MAIN_UPSTREAMSPAWNSTATE;
         case "2":
         case "2A":
            return PRIMARY_UPSTREAMSPAWNSTATE;
         case "G":
            return VERIFIED_UPSTREAMSPAWNSTATE;
         case "PBKDF2":
            return CURRENT_UPSTREAMSPAWNSTATE;
         case "ARGON2I":
            return UPSTREAM_SPAWN_STATE;
         case "ARGON2D":
            return ACTIVE_UPSTREAMSPAWNSTATE;
         case "ARGON2ID":
            return PENDING_UPSTREAMSPAWNSTATE;
         case "SHA256":
            return REMOTE_UPSTREAMSPAWNSTATE;
         case "SHA512":
            return LOCAL_UPSTREAMSPAWNSTATE;
         case "MD5":
            return CACHED_UPSTREAMSPAWNSTATE;
         case "SHA":
            return PRIVATE_UPSTREAMSPAWNSTATE;
         case "CRAZYLOGIN":
            return UPSTREAM_UPSTREAMSPAWNSTATE;
         case "BARONESS":
            return SHARED_UPSTREAMSPAWNSTATE;
         case "PBKDF2_SHA256":
            return INTERNAL_UPSTREAMSPAWNSTATE;
         default:
            return null;
      }
   }

   public String computeMessage(String target) {
      if (!this.enabled) {
         throw new UnsupportedOperationException("Algorithm " + this + " is not supported!");
      }

      switch (this) {
         case UPSTREAM_SPAWN_STATE:
         case ACTIVE_UPSTREAMSPAWNSTATE:
         case PENDING_UPSTREAMSPAWNSTATE:
         case CURRENT_UPSTREAMSPAWNSTATE:
         case PRIMARY_UPSTREAMSPAWNSTATE:
         case MAIN_UPSTREAMSPAWNSTATE:
         case LOCAL_UPSTREAMSPAWNSTATE:
         case REMOTE_UPSTREAMSPAWNSTATE:
         case CACHED_UPSTREAMSPAWNSTATE:
            return this.loadIndirectPlayerContract().computeMessage(target);
         default:
            throw new UnsupportedOperationException("Algorithm " + this + " is not implemented!");
      }
   }

   public boolean getState() {
      return this.enabled;
   }

   UpstreamSpawnState(Class<? extends IndirectPlayerContract> output) {
      this(true, output);
   }

   public static UpstreamSpawnState createUpstreamSpawnStateForUpstreamSpawnState(String instance) {
      switch (instance = instance.toUpperCase(Locale.ENGLISH)) {
         case "BCRYPT":
            return PRIMARY_UPSTREAMSPAWNSTATE;
         case "ARGON2":
            return UPSTREAM_SPAWN_STATE;
         default:
            return valueOf(instance);
      }
   }

   UpstreamSpawnState(boolean output, Class<? extends IndirectPlayerContract> context) {
      this.enabled = output;
      this.value = context;
   }
}
