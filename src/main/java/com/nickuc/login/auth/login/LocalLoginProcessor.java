package com.nickuc.login.auth.login;

import java.util.Map;
import java.util.concurrent.Callable;

public class LocalLoginProcessor {
   public static <T, V> T processObject(Map<T, V> instance, Callable<T> target) {
      Object input;
      do {
         input = target.call();
      } while (instance.containsKey(input));

      return (T)input;
   }
}
