package com.nickuc.login.auth.login;

import java.util.concurrent.TimeUnit;

public class LowLoginBarrier {
   static {
      try {
         values[TimeUnit.NANOSECONDS.ordinal()] = 1;
      } catch (NoSuchFieldError result) {
      }

      try {
         values[TimeUnit.MICROSECONDS.ordinal()] = 2;
      } catch (NoSuchFieldError value) {
      }

      try {
         values[TimeUnit.MILLISECONDS.ordinal()] = 3;
      } catch (NoSuchFieldError data) {
      }

      try {
         values[TimeUnit.SECONDS.ordinal()] = 4;
      } catch (NoSuchFieldError context) {
      }

      try {
         values[TimeUnit.MINUTES.ordinal()] = 5;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[TimeUnit.HOURS.ordinal()] = 6;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[TimeUnit.DAYS.ordinal()] = 7;
      } catch (NoSuchFieldError target) {
      }
   }
}
