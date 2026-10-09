package com.nickuc.login.storage.floodgate;

import com.nickuc.login.auth.login.OpenLoginBarrier;
import com.nickuc.login.auth.login.SafeLoginCheckpoint;
import com.nickuc.login.auth.login.StoredLoginFlow;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.platform.session.NestedSessionHandler;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.premium.SettingsLookup;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.geysermc.floodgate.api.FloodgateApi;

public class FloodgateGateway implements NestedSessionHandler {
   public static FloodgateGateway floodgateGateway = new FloodgateGateway();

   @Override
   public void savePasswordStore(PasswordStore target, LocalSettingsRepository input, SharedListenerContract output) {
      if (target.b().canState("floodgate")) {
         String context = FloodgateApi.getInstance().getPlayerPrefix();
         if (context != null && context.isEmpty()) {
            handlePasswordStore(target);
         }
      }

      input.sendCount(1);
      PasswordHashContainer.performMessage("Conversion finished! " + this.retrieveMessage());
   }

   @Override
   public boolean retrieveState() {
      return false;
   }

   private static void handlePasswordStore(PasswordStore instance) {
      File target = instance.a().retrieveFile();
      StringBuilder input = new StringBuilder();

      try {
         OpenLoginBarrier output = StoredLoginFlow.loadOpenLoginBarrier(target);

         String context;
         try {
            while ((context = output.findMessage()) != null) {
               if (input.length() > 0) {
                  input.append("\n");
               }

               if (context.trim().startsWith("use-database-uuid: ")) {
                  input.append(context.replace("use-database-uuid: false", "use-database-uuid: true"));
               } else {
                  input.append(context);
               }
            }
         } finally {
            if (Collections.singletonList(output).get(0) != null) {
               output.close();
            }
         }
      } catch (IOException entry) {
         throw new RuntimeException("Unable to read " + target + " file.", entry);
      }

      try {
         if (target.delete()) {
            SafeLoginCheckpoint.isState(target, input.toString().split("\n"));
            instance.a().loadState();
            SettingsLookup.validateState(instance);
         }
      } catch (IOException response) {
         throw new RuntimeException("Unable to write " + target + " file.", response);
      }
   }

   @Override
   public String retrieveMessage() {
      return "nLogin104BedrockIDConverter";
   }

   @Override
   public boolean checkState(PasswordStore target, LocalSettingsRepository input, SharedListenerContract output) {
      return input.loadInteger() == null;
   }
}
