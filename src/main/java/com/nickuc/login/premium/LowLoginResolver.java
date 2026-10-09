package com.nickuc.login.premium;

import com.nickuc.login.auth.login.AuthenticatedLoginCheckpoint;
import com.nickuc.login.auth.login.LiveLoginProcessor;
import com.nickuc.login.auth.locale.LocaleFlow;
import com.nickuc.login.auth.login.PrivateLoginCheckpoint;
import com.nickuc.login.auth.login.ReadyLoginGate;
import com.nickuc.login.auth.login.SecondaryLoginCheckpoint;
import com.nickuc.login.auth.sha.Sha256Barrier;
import com.nickuc.login.auth.login.StoredLoginBarrier;
import com.nickuc.login.config.PasswordHashContainer;
import org.json.JSONArray;
import org.json.JSONObject;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


public class LowLoginResolver {
   private final UpdateLookup updateLookup;
   private final IndirectSessionHandler<?> indirectSessionHandler;
   private final JSONObject jSONObject = new JSONObject();
   private long timestamp;

   public void updateTask() {
      this.indirectSessionHandler.processLinkedSessionHandler(true).processStrictCommandHandler(() -> {
         try {
            if (this.updateLookup.resolveReadyLoginGate() == null) {
               return;
            }

            int target = this.updateLookup.loadLinkedPasswordHashVerifier().buildObject("mfrequency", 300);
            if (target < 5 || target > 3600) {
               target = 300;
            }

            long input = System.currentTimeMillis();
            if (input - this.timestamp < target * 1000L) {
               return;
            }

            this.timestamp = input;
            PendingPasswordHashHasher context = PendingPasswordHashHasher.getPendingPasswordHashHasher();
            context.updateMessage("Accept", "application/json");
            context.updateMessage("Content-Type", "application/json");
            byte[] data = AuthenticatedLoginCheckpoint.createPayload(context, this.getPayload());
            SecondaryLoginCheckpoint value = this.updateLookup.buildSecondaryLoginCheckpoint(context, "/v%s/plugin/send", true, data);
            int result = value.findCount();
            if (result != 200 && VerifiedPasswordHashHasher.as()) {
               PasswordHashContainer.updateMessage("Unable to send server's data (code = " + result + ")");
            }
         } catch (Throwable request) {
            if (!(request instanceof IllegalStateException) || !"zip file closed".equals(request.getMessage())) {
               PasswordHashContainer.handleMessage("[nCore] Unexpected error (probably this is a bug!)", request);
            }
         }
      }, 0L, 1L, TimeUnit.SECONDS);
   }

   public LowLoginResolver processLowLoginResolver(String target, Object input) {
      JSONObject output;
      if (!this.jSONObject.has("extra")) {
         this.jSONObject.put("extra", output = new JSONObject());
      } else {
         output = this.jSONObject.getJSONObject("extra");
      }

      output.put(target, input);
      return this;
   }

   public LowLoginResolver resolveLowLoginResolver(String target, String input) {
      return this.processLowLoginResolver(target, input);
   }

   private byte[] getPayload() {
      this.performJSONObject(this.jSONObject);
      this.processLowLoginResolver("settings", VerifiedPasswordHashHasher.resolveJSONObject(true));
      return this.jSONObject.toString(0).getBytes(StandardCharsets.UTF_8);
   }

   private String fetchMessage() {
      try {
         Path target = Paths.get("/proc/cpuinfo");
         if (!target.toFile().exists()) {
            return "unsupported";
         }

         String input;
         try {
            Stream output = Files.lines(target);

            try {
               input = output.filter(instance -> instance.startsWith("model name")).map(instance -> instance.replaceAll(".*: ", "")).findFirst().orElse("unknown");
            } catch (Throwable result) {
               if (output != null) {
                  try {
                     output.close();
                  } catch (Throwable value) {
                     result.addSuppressed(value);
                  }
               }

               throw result;
            }

            if (output != null) {
               output.close();
            }
         } catch (Throwable request) {
            input = "error " + request.getMessage();
         }

         return input.length() > 2048 ? input.substring(0, 2048) : input;
      } catch (Throwable response) {
         return "error " + response.getMessage();
      }
   }

   public LowLoginResolver(IndirectSessionHandler<?> target, UpdateLookup input) {
      this.indirectSessionHandler = target;
      this.updateLookup = input;
   }

   public void performJSONObject(JSONObject target) {
      LinkedPasswordHashVerifier input = this.updateLookup.loadLinkedPasswordHashVerifier();
      LiveLoginProcessor output = this.updateLookup.retrieveLiveLoginProcessor();
      ReadyLoginGate context = this.updateLookup.resolveReadyLoginGate();
      JSONObject data = new JSONObject();
      data.put("sessionId", context != null ? context.retrieveTime() : "?");
      data.put("serverId", output != null ? output.retrieveMessage() : "?");
      data.put("serverIp", input.fetchMessage());
      data.put("plugin", this.indirectSessionHandler.toString());
      data.put("version", this.indirectSessionHandler.getMessage());
      data.put("versionChannel", this.updateLookup.retrieveAuthenticatedNoticeKind().getName());
      this.indirectSessionHandler.getObject();
      String reference;
      if (false) {
         StringBuilder switchSelector = new StringBuilder().append("false ");
         this.indirectSessionHandler.getObject();
         reference = switchSelector.append("").toString();
      } else {
         reference = "true";
      }

      data.put("integrity", reference);
      data.put("checksum", this.updateLookup.findMessage());
      data.put("file", this.updateLookup.fetchMessage() + " " + this.indirectSessionHandler.fetchParentSettingsLookup().retrieveFile().getAbsolutePath());
      data.put("uptime", LocaleFlow.computeMessage(System.currentTimeMillis(), StoredLoginBarrier.timestamp));
      int value = this.updateLookup.getCount();
      String result = this.updateLookup.resolveMessage();
      if (result != null) {
         data.put("licenseId", result);
      }

      if (value != 1) {
         data.put("licenseStatus", this.updateLookup.getCount());
      }

      long request = Runtime.getRuntime().maxMemory();
      JSONObject source = new JSONObject();
      source.put("javaVersion", System.getProperty("java.version", "?"));
      source.put("osName", System.getProperty("os.name", "?"));
      source.put("osArch", System.getProperty("os.arch", "?"));
      source.put("osVersion", System.getProperty("os.version", "?"));
      source.put("coreCount", Runtime.getRuntime().availableProcessors());
      source.put("cpuName", this.fetchMessage());
      source.put("ramMax", request == Long.MAX_VALUE ? -1 : PrivateLoginCheckpoint.computeMessage(request));
      source.put("ramUsed", PrivateLoginCheckpoint.computeMessage(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
      JSONObject entry = new JSONObject();
      entry.put("platformVersion", this.indirectSessionHandler.findIncomingLoginGate().toString());
      Sha256Barrier[] record = this.indirectSessionHandler.retrieveParentAccountHandler().retrieveValues();
      entry.put("pluginCount", record.length);
      JSONArray item = new JSONArray(record.length);

      for (int element = 0; element < record.length; element++) {
         Sha256Barrier content = record[element];
         JSONObject payload = new JSONObject();
         payload.put("name", content.getName());
         payload.put("version", content.getVersion());
         payload.put("authors", content.retrieveCollection());
         Path holder = content.fetchPath();
         if (holder != null) {
            payload.put("path", holder.toString());
         }

         item.put(element, payload);
      }

      entry.put("pluginList", item);
      target.put("plugin", data);
      target.put("system", source);
      target.put("server", entry);
   }
}
