package com.nickuc.login.storage.login;

import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.auth.login.RootLoginHandler;
import com.nickuc.login.auth.login.SafeLoginBarrier;
import com.nickuc.login.auth.settings.SettingsProcessor;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.LocalPremiumState;
import com.nickuc.login.model.RemotePremiumState;
import org.json.JSONObject;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.premium.Pbkdf2Linker;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;


public class LoginCollection {
   public static final String name = "nlogin";
   public static final String activeName = "addon";
   public static final int count = 8;
   public final PasswordStore passwordStore;
   private final SettingsProcessor settingsProcessor;
   public static final int activeCount = 128;
   public static final int pendingCount = 2048;
   public static final String pendingName = "nlogin:addon";

   public LoginCollection(PasswordStore target) {
      this.passwordStore = target;
      this.settingsProcessor = new SettingsProcessor(this);
   }

   public void sendVerifiedServerAdapter(VerifiedServerAdapter target, int input, Object... output) {
      if (output.length % 2 != 0) {
         throw new IllegalArgumentException("Content not in key and value format!");
      }

      JSONObject context = new JSONObject();
      context.put("id", input);
      JSONObject data = new JSONObject();

      for (int value = 0; value < output.length; value++) {
         Object result = output[value++];
         if (!(result instanceof String)) {
            throw new IllegalArgumentException("Key is not a string! " + result);
         }

         data.put((String)result, output[value]);
      }

      context.put("data", data);
      byte[] response = FastMessageHandler.buildPayload(targetValue -> targetValue.savePayload(context.toString().getBytes(StandardCharsets.UTF_8)));
      if (this.passwordStore.loadState() || !this.passwordStore.resolveRootMessageHandler().validateState(target, "nlogin:addon", response)) {
         target.performIndirectSessionHandler(this.passwordStore, RemotePremiumState.REMOTE_PREMIUM_STATE, "nlogin:addon", response);
      }
   }

   public void dispatchVerifiedServerAdapter(VerifiedServerAdapter target, String input, String output) {
      this.sendVerifiedServerAdapter(target, 1, "data", input, "checksum", output);
   }

   public void processVerifiedServerAdapter(VerifiedServerAdapter target, byte[] input, boolean output, boolean context) {
      if (input.length > 8) {
         PasswordHashContainer.performMessage(
            "[Addon] Received an RSA challenge larger than allowed from " + target.getName() + ". (" + input.length + " > " + 8 + ")"
         );
         this.executeVerifiedServerAdapter(target, LocalPremiumState.ACTIVE_LOCALPREMIUMSTATE);
      } else {
         KeyPair data = Pbkdf2Linker.resolveKeyPair();

         try {
            if (input.length > 0) {
               input = RootLoginHandler.handlePayload(input, data.getPrivate());
            }
         } catch (GeneralSecurityException request) {
            PasswordHashContainer.handleMessage("Unable to sign addon challenge for " + target.getName(), request);
            return;
         }

         JSONObject value = new JSONObject();
         value.put("user-registered", context);
         if (context) {
            value.put("require-sync", output);
         }

         JSONObject result = new JSONObject();
         result.put("key", SafeLoginBarrier.resolveMessage(data.getPublic().getEncoded()));
         result.put("signature", SafeLoginBarrier.resolveMessage(input));
         this.sendVerifiedServerAdapter(target, 0, "user-id", target.getUniqueId(), "max-allowed-data", 2048, "challenge", result, "client", value);
      }
   }

   public SettingsProcessor fetchSettingsProcessor() {
      return this.settingsProcessor;
   }

   public void executeVerifiedServerAdapter(VerifiedServerAdapter target, LocalPremiumState input) {
      this.sendVerifiedServerAdapter(target, 2, "code", input.ordinal());
   }

   public LoginCollection(PasswordStore target, SettingsProcessor input) {
      this.passwordStore = target;
      this.settingsProcessor = input;
   }
}
