package com.nickuc.login.auth.login;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RemoteLoginBarrier<T> extends AbstractList<T> {
   public static final List<Integer> entries = new RemoteLoginBarrier<>(new ArrayList<>());
   public static final List<?> activeEntries = new RemoteLoginBarrier(new ArrayList<>());
   private final List<T> pendingEntries;
   public static final List<String> currentEntries = new RemoteLoginBarrier<>(new ArrayList<>());

   public T processObject(int target) {
      return this.pendingEntries.get(target);
   }

   @Override
   public int size() {
      return this.pendingEntries.size();
   }

   @SafeVarargs
   public static <T> RemoteLoginBarrier<T> loadRemoteLoginBarrier(T... instance) {
      List target = Arrays.asList(instance);
      return new RemoteLoginBarrier<>(target);
   }

   private RemoteLoginBarrier(List<T> target) {
      this.pendingEntries = target;
   }

   @Override
   public boolean equals(Object target) {
      return target instanceof List ? target.equals(this.pendingEntries) : false;
   }

   @Override
   public boolean contains(Object target) {
      return this.pendingEntries.contains(target);
   }

   public static <T> RemoteLoginBarrier<T> createRemoteLoginBarrier(List<T> instance) {
      return new RemoteLoginBarrier<>(new ArrayList<>(instance));
   }
}
