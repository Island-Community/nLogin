package com.nickuc.login.auth.login;



public class FastLoginBarrier<T, V> {
   private final T object;
   private final V activeObject;

   public V loadObject() {
      return this.activeObject;
   }

   private FastLoginBarrier(T target, V input) {
      this.object = (T)target;
      this.activeObject = (V)input;
   }

   public T findObject() {
      return this.object;
   }

   public static <T, V> FastLoginBarrier<T, V> createFastLoginBarrier(T instance, V target) {
      return new FastLoginBarrier<>((T)instance, (V)target);
   }
}
