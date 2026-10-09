package com.nickuc.login.auth.locale;

import com.nickuc.login.platform.connection.ConnectionContract;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PendingLocaleBarrier implements ConnectionContract {
   private final Set<String> players;

   @Override
   public boolean filter(String target, String input, Object... output) {
      if (!this.players.isEmpty()) {
         String context = input.toLowerCase(Locale.ENGLISH);
         return this.players.stream().anyMatch(context::contains);
      } else {
         return false;
      }
   }

   public PendingLocaleBarrier buildPendingLocaleBarrier(String target, String... input) {
      this.players.add(target.toLowerCase(Locale.ENGLISH));
      if (input.length > 0) {
         for (String value : input) {
            this.players.add(value.toLowerCase(Locale.ENGLISH));
         }
      }

      return this;
   }

   public PendingLocaleBarrier(Set<String> target, String... input) {
      if (!target.isEmpty()) {
         target = target.stream().map(String::toLowerCase).collect(Collectors.toSet());
      }

      this.players = target;
      if (input.length > 0) {
         for (String value : input) {
            this.players.add(value.toLowerCase(Locale.ENGLISH));
         }
      }
   }
}
