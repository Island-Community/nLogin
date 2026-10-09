package com.nickuc.login.command;

import com.nickuc.login.platform.connection.ConnectionContract;
import com.nickuc.login.premium.IndirectPasswordHashVerifier;
import java.util.Locale;


public final class ForceSetupCommand implements ConnectionContract {
   private final IndirectPasswordHashVerifier indirectPasswordHashVerifier;

   private ForceSetupCommand(IndirectPasswordHashVerifier target) {
      this.indirectPasswordHashVerifier = target;
   }

   @Override
   public boolean filter(String target, String input, Object... output) {
      int context = input.indexOf("issued server command: ");
      if (context > -1) {
         context += "issued server command: ".length();
         if (input.length() - context > 1) {
            String[] data = input.substring(context).split(" ");
            if (data.length > 1) {
               String value = data[0].toLowerCase(Locale.ENGLISH);
               return this.indirectPasswordHashVerifier.resolveSet().stream().anyMatch(value::equals);
            }
         }
      }

      return false;
   }
}
