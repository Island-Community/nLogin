package com.nickuc.login.model;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;


public enum LoginOption {
   LOGIN_OPTION('d', instance -> TimeUnit.MILLISECONDS.toDays(instance) % Long.MAX_VALUE),
   ACTIVE_LOGINOPTION('h', instance -> TimeUnit.MILLISECONDS.toHours(instance) % 24L),
   PENDING_LOGINOPTION('m', instance -> TimeUnit.MILLISECONDS.toMinutes(instance) % 60L),
   CURRENT_LOGINOPTION('s', instance -> TimeUnit.MILLISECONDS.toSeconds(instance) % 60L);

   private final char marker;
   private final Function<Long, Long> function;

   LoginOption(char output, Function<Long, Long> context) {
      this.marker = output;
      this.function = context;
   }
}
