package com.nickuc.login.auth.login;

import com.nickuc.login.model.PlatformState;

public class PendingLoginCheckpoint {
   static {
      try {
         values[PlatformState.MAIN_PLATFORMSTATE.ordinal()] = 1;
      } catch (NoSuchFieldError source) {
      }

      try {
         values[PlatformState.PLATFORM_STATE.ordinal()] = 2;
      } catch (NoSuchFieldError response) {
      }

      try {
         values[PlatformState.ACTIVE_PLATFORMSTATE.ordinal()] = 3;
      } catch (NoSuchFieldError request) {
      }

      try {
         values[PlatformState.PENDING_PLATFORMSTATE.ordinal()] = 4;
      } catch (NoSuchFieldError result) {
      }

      try {
         values[PlatformState.CURRENT_PLATFORMSTATE.ordinal()] = 5;
      } catch (NoSuchFieldError value) {
      }

      try {
         values[PlatformState.PRIMARY_PLATFORMSTATE.ordinal()] = 6;
      } catch (NoSuchFieldError data) {
      }

      try {
         values[PlatformState.REMOTE_PLATFORMSTATE.ordinal()] = 7;
      } catch (NoSuchFieldError context) {
      }

      try {
         values[PlatformState.CACHED_PLATFORMSTATE.ordinal()] = 8;
      } catch (NoSuchFieldError output) {
      }

      try {
         values[PlatformState.LOCAL_PLATFORMSTATE.ordinal()] = 9;
      } catch (NoSuchFieldError input) {
      }

      try {
         values[PlatformState.STORED_PLATFORMSTATE.ordinal()] = 10;
      } catch (NoSuchFieldError target) {
      }
   }
}
