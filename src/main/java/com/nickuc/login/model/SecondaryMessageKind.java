package com.nickuc.login.model;

public enum SecondaryMessageKind {
   SECONDARY_MESSAGE_KIND("ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
   ACTIVE_SECONDARYMESSAGEKIND("abcdefghijklmnopqrstuvwxyz"),
   PENDING_SECONDARYMESSAGEKIND("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"),
   CURRENT_SECONDARYMESSAGEKIND("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"),
   PRIMARY_SECONDARYMESSAGEKIND("1234567890"),
   MAIN_SECONDARYMESSAGEKIND("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890");

   private final char[] values;

   SecondaryMessageKind(String output) {
      this.values = output.toCharArray();
   }
}
