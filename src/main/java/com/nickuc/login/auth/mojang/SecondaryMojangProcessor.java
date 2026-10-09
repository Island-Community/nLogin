package com.nickuc.login.auth.mojang;

import com.nickuc.login.api.enums.AccountType;
import com.nickuc.login.api.nLoginAPI.nLoginInternal;
import com.nickuc.login.api.types.Identity;
import java.util.UUID;

public abstract class SecondaryMojangProcessor implements nLoginInternal {
   public Identity createIdentityFromKnownName(String target) {
      if (target == null) {
         throw new IllegalArgumentException("Player name cannot be null!");
      } else if (target.isEmpty()) {
         throw new IllegalArgumentException("Player name cannot be empty!");
      } else {
         return new RemoteLoginProcessor(target);
      }
   }

   public Identity createIdentity(String target, UUID input, UUID output, AccountType context) {
      if (target == null) {
         throw new IllegalArgumentException("Player name cannot be null!");
      }

      if (target.isEmpty()) {
         throw new IllegalArgumentException("Player name cannot be empty!");
      }

      switch (context) {
         case PREMIUM:
            if (input == null) {
               throw new IllegalArgumentException("Mojang ID cannot be null!");
            }

            if (input.version() != 4) {
               throw new IllegalArgumentException(
                  "The Mojang ID provided is not valid! expected version = 4, received version = "
                     + input.version()
                     + ", username = "
                     + target
                     + ", uuid = "
                     + input
               );
            }
            break;
         case BEDROCK:
            if (output == null) {
               throw new IllegalArgumentException("Bedrock ID cannot be null!");
            }

            if (output.version() != 0) {
               throw new IllegalArgumentException(
                  "The Bedrock ID provided is not valid! expected version = 0, received version = "
                     + output.version()
                     + ", username = "
                     + target
                     + ", uuid = "
                     + output
               );
            }
      }

      return new MojangService(target, input, output);
   }
}
