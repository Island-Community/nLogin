package com.nickuc.login.security.hashing;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class MainPasswordHashVerifier<T> extends AbstractSet<T> {
   private final Set<T> players;
   public static final Set<String> activePlayers = new MainPasswordHashVerifier<>(new HashSet<>());
   public static final Set<Integer> pendingPlayers = new MainPasswordHashVerifier<>(new HashSet<>());
   public static final Set<?> currentPlayers = new MainPasswordHashVerifier(new HashSet<>());

   @Override
   public int size() {
      return this.players.size();
   }

   private MainPasswordHashVerifier(Set<T> target) {
      this.players = target;
   }

   @SafeVarargs
   public static <T> MainPasswordHashVerifier<T> resolveMainPasswordHashVerifier(T... instance) {
      HashSet target = new HashSet<>(Arrays.asList(instance));
      return new MainPasswordHashVerifier<>(target);
   }

   public static <T> MainPasswordHashVerifier<T> processMainPasswordHashVerifier(Set<T> instance) {
      return new MainPasswordHashVerifier<>(instance);
   }

   @Override
   public Iterator<T> iterator() {
      return this.players.iterator();
   }

   @Override
   public boolean contains(Object target) {
      return this.players.contains(target);
   }

   @Override
   public boolean equals(Object target) {
      return target instanceof Set ? target.equals(this.players) : false;
   }
}
