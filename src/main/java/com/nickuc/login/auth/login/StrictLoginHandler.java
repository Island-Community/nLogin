package com.nickuc.login.auth.login;



public class StrictLoginHandler<T> {
   private final T object;

   public T resolveObject() {
      return this.object;
   }

   private StrictLoginHandler(T target) {
      this.object = (T)target;
   }
}
