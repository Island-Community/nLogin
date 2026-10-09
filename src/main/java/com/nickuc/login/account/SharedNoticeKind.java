package com.nickuc.login.account;

import com.nickuc.login.premium.Pbkdf2Linker;
import java.util.List;
import java.util.regex.Pattern;


public enum SharedNoticeKind {
   SHARED_NOTICE_KIND(true),
   ACTIVE_SHAREDNOTICEKIND(true),
   PENDING_SHAREDNOTICEKIND(false),
   CURRENT_SHAREDNOTICEKIND(false);

   private final boolean enabled;

   SharedNoticeKind(boolean output) {
      this.enabled = output;
   }

   public boolean loadState() {
      return this.enabled;
   }

   public static SharedNoticeKind createSharedNoticeKind(String instance) {
      String target = instance.toLowerCase();
      boolean input = checkState(Pbkdf2Linker.loadCollection(), target);
      boolean output = checkState(Pbkdf2Linker.retrieveCollection(), target);
      if (input && output) {
         return CURRENT_SHAREDNOTICEKIND;
      } else if (input) {
         return SHARED_NOTICE_KIND;
      } else {
         return output ? ACTIVE_SHAREDNOTICEKIND : PENDING_SHAREDNOTICEKIND;
      }
   }

   private static boolean checkState(List<Pattern> instance, String target) {
      for (Pattern output : instance) {
         if (output.matcher(target).matches()) {
            return true;
         }
      }

      return false;
   }
}
