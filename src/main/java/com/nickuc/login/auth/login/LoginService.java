package com.nickuc.login.auth.login;

import com.nickuc.login.account.SharedNoticeKind;
import com.nickuc.login.model.IncomingSpawnState;

public class LoginService {
   static {
      try {
         values[IncomingSpawnState.INCOMING_SPAWN_STATE.ordinal()] = 1;
      } catch (NoSuchFieldError request) {
      }

      try {
         values[IncomingSpawnState.ACTIVE_INCOMINGSPAWNSTATE.ordinal()] = 2;
      } catch (NoSuchFieldError result) {
      }

      try {
         values[IncomingSpawnState.PENDING_INCOMINGSPAWNSTATE.ordinal()] = 3;
      } catch (NoSuchFieldError value) {
      }

      try {
         values[IncomingSpawnState.CURRENT_INCOMINGSPAWNSTATE.ordinal()] = 4;
      } catch (NoSuchFieldError data) {
      }

      activeValues = new int[SharedNoticeKind.values().length];

      try {
         activeValues[SharedNoticeKind.SHARED_NOTICE_KIND.ordinal()] = 1;
      } catch (NoSuchFieldError context) {
      }

      try {
         activeValues[SharedNoticeKind.ACTIVE_SHAREDNOTICEKIND.ordinal()] = 2;
      } catch (NoSuchFieldError output) {
      }

      try {
         activeValues[SharedNoticeKind.PENDING_SHAREDNOTICEKIND.ordinal()] = 3;
      } catch (NoSuchFieldError input) {
      }

      try {
         activeValues[SharedNoticeKind.CURRENT_SHAREDNOTICEKIND.ordinal()] = 4;
      } catch (NoSuchFieldError target) {
      }
   }
}
