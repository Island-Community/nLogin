package com.nickuc.login.command;

import com.nickuc.login.platform.connection.ConnectionContract;
import com.nickuc.login.premium.IndirectPasswordHashVerifier;
import java.util.Locale;


public final class LinkSpawnCommand implements ConnectionContract {
   private final IndirectPasswordHashVerifier indirectPasswordHashVerifier;

   @Override
   public boolean filter(String target, String input, Object... output) {
      if (output.length == 2 && input.contains("executed command")) {
         String context = '/' + (String)output[1];
         String data = context.split(" ")[0].toLowerCase(Locale.ENGLISH);
         return this.indirectPasswordHashVerifier.resolveSet().stream().anyMatch(data::equals);
      } else {
         return false;
      }
   }

   private LinkSpawnCommand(IndirectPasswordHashVerifier target) {
      this.indirectPasswordHashVerifier = target;
   }
}
