package com.nickuc.login.auth.login;

import com.nickuc.login.model.PlatformCatalog;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;


public class LiveLoginProcessor {
   private final String name;
   private final String activeName;

   public String retrieveMessage() {
      return this.name;
   }

   public LiveLoginProcessor(String target, String input) {
      this.name = target;
      this.activeName = input;
   }

   public boolean hasState(JSONObject target, String input, byte[] output) {
      String context = target.getString("signature");
      String data = '{' + input.substring(80);
      byte[] value = data.getBytes(StandardCharsets.UTF_8);
      byte[] result = this.name.getBytes(StandardCharsets.UTF_8);
      byte[] request = this.activeName.getBytes(StandardCharsets.UTF_8);
      byte[] response = new byte[result.length + output.length + value.length + request.length];
      System.arraycopy(result, 0, response, 0, result.length);
      System.arraycopy(output, 0, response, result.length, output.length);
      System.arraycopy(value, 0, response, result.length + output.length, value.length);
      System.arraycopy(request, 0, response, result.length + output.length + value.length, request.length);
      return PlatformCatalog.PENDING_PLATFORMCATALOG.createMessage(response).equals(context);
   }

   public String resolveMessage() {
      return this.activeName;
   }
}
