package com.nickuc.login.auth.settings;

import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LocalPremiumState;
import com.nickuc.login.model.OutgoingSpawnState;
import com.nickuc.login.model.TightPlatformCatalog;
import org.json.JSONObject;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.Pbkdf2Linker;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.security.hashing.CachedPasswordHashHasher;
import com.nickuc.login.session.LimboCoordinator;
import com.nickuc.login.session.LimboRegistry;
import com.nickuc.login.storage.login.LoginCollection;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.tasks.LoginMainQueueTask;
import java.nio.charset.StandardCharsets;


public class SettingsProcessor {
   private final LoginCollection loginCollection;

   public boolean canState(VerifiedServerAdapter target, byte[] input) {
      try {
         if (Pbkdf2Linker.resolveKeyPair() == null) {
            return true;
         }

         if (input.length == 0) {
            return true;
         }

         String output = new String(input, StandardCharsets.UTF_8);
         if (output.length() > 2 && output.charAt(0) == '{' && output.charAt(output.length() - 1) == '}') {
            this.executeVerifiedServerAdapter(target, new JSONObject(output));
         }

         return true;
      } catch (Exception context) {
         PasswordHashContainer.handleMessage("Unable to read addon plugin message", context);
         return true;
      }
   }

   public SettingsProcessor(LoginCollection target) {
      this.loginCollection = target;
   }

   private void executeVerifiedServerAdapter(VerifiedServerAdapter target, JSONObject input) {
      if (input.has("id") && input.has("data")) {
         PasswordStore output = this.loginCollection.passwordStore;
         LimboRegistry context = output.loadLimboRegistry();
         LimboCoordinator data = context.loadLimboCoordinator(target);
         int value = input.getInt("id");
         JSONObject result = input.getJSONObject("data");
         switch (value) {
            case 0:
               if (!data.isState(LenientMessageKind.ACTIVE_SHARED_LENIENTMESSAGEKIND)) {
                  byte[] item = SafeLoginBarrier.loadPayload(result.getString("challenge").getBytes(StandardCharsets.UTF_8));
                  data.updateLenientMessageKind(LenientMessageKind.ACTIVE_SHARED_LENIENTMESSAGEKIND, item);
                  SpawnLookup content = data.loadSpawnLookup();
                  output.processLinkedSessionHandler(true).buildStrictCommandHandler(() -> {
                     if (LoginMainQueueTask.verifyState(target) && data.loadTightPlatformCatalog().isState(TightPlatformCatalog.CURRENT_TIGHTPLATFORMCATALOG)) {
                        boolean dataValue = content.findStateForState();
                        int valueValue = !content.resolveCachedPasswordHashHasher().hasState("addon.data") ? 1 : 0;
                        this.loginCollection.processVerifiedServerAdapter(target, item, (boolean)valueValue, !dataValue);
                     }
                  });
               }
               break;
            case 1:
               if (context.canState(target) && data.isState(LenientMessageKind.ACTIVE_SHARED_LENIENTMESSAGEKIND)) {
                  String record = result.getString("data");
                  if (record.length() > 2048) {
                     PasswordHashContainer.performMessage(
                        "[Addon] Received encrypted content larger than allowed from " + target.getName() + " (" + record.length() + " > " + 2048 + ")"
                     );
                     this.loginCollection.executeVerifiedServerAdapter(target, LocalPremiumState.PENDING_LOCALPREMIUMSTATE);
                  } else {
                     String element = result.getString("checksum");
                     if (element.length() > 128) {
                        PasswordHashContainer.performMessage(
                           "[Addon] Received checksum larger than allowed from " + target.getName() + " (" + element.length() + " > " + 128 + ")"
                        );
                        this.loginCollection.executeVerifiedServerAdapter(target, LocalPremiumState.CURRENT_LOCALPREMIUMSTATE);
                     } else {
                        SpawnLookup payload = data.loadSpawnLookup();
                        CachedPasswordHashHasher entry = payload.resolveCachedPasswordHashHasher();
                        entry.updateMessage("addon.data", record);
                        entry.updateMessage("addon.checksum", element);
                        output.processLinkedSessionHandler(true)
                           .buildStrictCommandHandler(
                              () -> output.findIndirectPasswordResolver().isState(payload, OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE)
                           );
                     }
                  }
               }
               break;
            case 2:
               if (data.isState(LenientMessageKind.ACTIVE_SHARED_LENIENTMESSAGEKIND)) {
                  SpawnLookup request = data.loadSpawnLookup();
                  String response = request.resolveCachedPasswordHashHasher().handleObject("addon.data");
                  String source = request.resolveCachedPasswordHashHasher().handleObject("addon.checksum");
                  if (response != null && source != null) {
                     this.loginCollection.dispatchVerifiedServerAdapter(target, response, source);
                  }
               }
         }
      }
   }
}
