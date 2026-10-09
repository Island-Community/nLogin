package com.nickuc.login.storage.redis;

import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.PremiumState;
import com.nickuc.login.model.QuickMessageKind;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.function.Consumer;


public class RedisSource implements Consumer<String> {
   private final PasswordStore passwordStore;

   public void executeMessage(String target) {
      try {
         if (target.charAt(0) == '{' && target.charAt(target.length() - 1) == '}') {
            JSONObject input = new JSONObject(target);
            this.handleCount(input.getInt("id"), input.getJSONObject("data"));
            return;
         }

         PasswordHashContainer.updateMessage("Malformed JSON received from Redis: \"%s\"", target);
      } catch (JSONException output) {
         PasswordHashContainer.handleMessage("Malformed JSON received from Redis: \"%s\"", output, target);
      }
   }

   private void handleCount(int target, JSONObject input) {
      switch (target) {
         case 0:
            String value = input.getString("player");
            String result = input.getString("ip");
            PremiumState request = (PremiumState)input.getEnum(PremiumState.class, "mode");
            PasswordGateway.dispatchMessage(value, result, request);
            break;
         case 1:
            String output = input.getString("player");
            String context = input.getString("ip");
            QuickMessageKind data = (QuickMessageKind)input.getEnum(QuickMessageKind.class, "mode");
            PasswordGateway.handleMessage(output, context, data);
      }
   }

   private RedisSource(PasswordStore target) {
      this.passwordStore = target;
   }
}
