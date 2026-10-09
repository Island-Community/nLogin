package com.nickuc.login.platform.connection;

import com.nickuc.login.auth.login.ChainedLoginFlow;
import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.model.RemotePremiumState;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public interface TightConnectionContract {
   static byte[] createPayload(byte[] instance, boolean target) {
      byte[] input;
      if (target) {
         byte[] output = ChainedLoginFlow.loadPayload(instance);
         input = new byte[1 + output.length];
         input[0] = 1;
         System.arraycopy(output, 0, input, 1, output.length);
      } else {
         input = new byte[1 + instance.length];
         input[0] = 0;
         System.arraycopy(instance, 0, input, 1, instance.length);
      }

      return input;
   }

   default boolean checkState(RemotePremiumState target, Object input, byte[] output) {
      return this.checkState(null, target, input, output);
   }

   static byte[] loadPayload(JSONObject instance, boolean target) {
      byte[] input = FastMessageHandler.buildPayload(targetValue -> targetValue.savePayload(instance.toString().getBytes(StandardCharsets.UTF_8)));
      return createPayload(input, target);
   }

   void processObject(Object target);

   void dispatchObject(Object target, Object input);

   boolean checkState(VerifiedServerAdapter target, RemotePremiumState input, Object output, byte[] context);

   static byte[] loadPayload(byte[] instance) {
      boolean target = instance[0] == 1;
      byte[] input = new byte[instance.length - 1];
      System.arraycopy(instance, 1, input, 0, input.length);
      if (target) {
         input = ChainedLoginFlow.resolvePayload(input);
      }

      return input;
   }

   void updateObject(Object target);

   static JSONObject processJSONObject(byte[] instance) {
      byte[] target = loadPayload(instance);
      String input = new String(target, StandardCharsets.UTF_8);
      return new JSONObject(input);
   }
}
