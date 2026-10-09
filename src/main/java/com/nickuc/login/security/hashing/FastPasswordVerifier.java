package com.nickuc.login.security.hashing;

import com.nickuc.login.auth.login.PrimaryLoginService;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.UpstreamSpawnState;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.spawn.PremiumOption;
import com.nickuc.login.storage.password.PasswordStore;

public class FastPasswordVerifier {
   public static boolean verifyState(PasswordStore instance, String target, String input, SpawnLookup output) {
      if (target != null && input != null) {
         long context = System.nanoTime();
         UpstreamSpawnState value = Pbkdf2Linker.loadUpstreamSpawnState();
         byte result = 0;

         try {
            byte request = 0;
            UpstreamSpawnState response = output.findUpstreamSpawnState();
            if (response == null) {
               PasswordHashContainer.performMessage(
                  "Unable to identify " + output.retrieveMessage() + " account password algorithm! stored password = \"" + input + "\""
               );
            } else {
               request = response.loadIndirectPlayerContract().verifyState(target, input);
            }

            if (request != 0) {
               result = (byte)(response == value && !response.loadIndirectPlayerContract().canState(input) ? 0 : 1);
            }

            return (boolean)request;
         } finally {
            PrimaryLoginService.savePremiumOption(PremiumOption.AUTHENTICATED_PREMIUMOPTION, context);
            if (result != 0) {
               PasswordHashContainer.dispatchMessage("Updating " + output.retrieveMessage() + " password hash to " + value.name());
               instance.findIndirectPasswordResolver().validateState(output, target);
            }
         }
      } else {
         return false;
      }
   }
}
