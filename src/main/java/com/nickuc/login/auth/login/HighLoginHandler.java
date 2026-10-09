package com.nickuc.login.auth.login;

import java.util.concurrent.TimeUnit;

public class HighLoginHandler {
   static {
      try {
         values[TimeUnit.MICROSECONDS.ordinal()] = 1;
      } catch (NoSuchFieldError value) {
      }

      try {
         values[TimeUnit.MILLISECONDS.ordinal()] = 2;
      } catch (NoSuchFieldError data) {
      }

      try {
         values[TimeUnit.SECONDS.ordinal()] = 3;
      } catch (NoSuchFieldError context) {
      }

      try {
         values[TimeUnit.MINUTES.ordinal()] = 4;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[TimeUnit.HOURS.ordinal()] = 5;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[TimeUnit.DAYS.ordinal()] = 6;
      } catch (NoSuchFieldError target) {
      }
   }
}
