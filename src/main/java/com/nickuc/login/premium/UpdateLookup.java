package com.nickuc.login.premium;

import com.nickuc.login.auth.login.AuthenticatedLoginCheckpoint;
import com.nickuc.login.auth.login.LiveLoginProcessor;
import com.nickuc.login.auth.login.ReadyLoginGate;
import com.nickuc.login.auth.login.SafeLoginBarrier;
import com.nickuc.login.auth.login.SecondaryLoginCheckpoint;
import com.nickuc.login.auth.login.SecureLoginHandler;
import com.nickuc.login.auth.login.VerifiedLoginGate;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.PlatformCatalog;
import org.json.JSONException;
import org.json.JSONObject;
import com.nickuc.login.loader.LoaderBootstrap;
import com.nickuc.login.loader.MemClassLoader;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.session.LinkedSessionHandler;
import com.nickuc.login.platform.listener.PrivateListenerContract;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.security.hashing.PrimaryPasswordHashVerifier;
import com.nickuc.login.updater.AuthenticatedNoticeKind;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;


public class UpdateLookup {
   private final String name;
   private final LinkedPasswordHashVerifier linkedPasswordHashVerifier;
   private final IndirectSessionHandler<?> indirectSessionHandler;
   @Nullable
   private SecondaryLoginCheckpoint secondaryLoginCheckpoint;
   @Nullable
   private byte[] values;
   @Nullable
   private ReadyLoginGate readyLoginGate;
   @Nullable
   private LiveLoginProcessor liveLoginProcessor;
   @Nullable
   private String activeName;
   private final LowLoginResolver lowLoginResolver;
   public static final int count = 300;
   public static final int activeCount = 120;
   private static final List<String> entries = Arrays.asList(
      SafeLoginBarrier.computeMessage("aHR0cHM6Ly9hcGkubmlja3VjLmNvbQ=="), SafeLoginBarrier.computeMessage("aHR0cHM6Ly9hcGkubmlja3VjLm5ldA==")
   );
   private long timestamp;
   private final String pendingName;
   private AuthenticatedNoticeKind authenticatedNoticeKind;

   private synchronized void performState(boolean target) {
      if (target) {
         this.executeTask();
      }

      if (this.secondaryLoginCheckpoint == null) {
         throw new IllegalStateException("Raw remote response not set!");
      }

      this.linkedPasswordHashVerifier.handleVerifiedLoginGate(this.secondaryLoginCheckpoint);
      if (this.linkedPasswordHashVerifier.findState()) {
         try {
            File input = new File(this.indirectSessionHandler.fetchFile() + File.separator + "cache", this.indirectSessionHandler.retrieveMessage() + ".bin");
            String output = this.linkedPasswordHashVerifier.loadMessage();
            if (!input.exists() || output != null && !PlatformCatalog.PENDING_PLATFORMCATALOG.validateState(input, output)) {
               this.indirectSessionHandler.fetchParentSettingsLookup().findCachedLoginBarrier().saveTask();
            }
         } catch (Exception context) {
            PasswordHashContainer.performMessage("The cached update file could not be verified (integrity)");
         }
      }
   }

   public SecondaryLoginCheckpoint buildSecondaryLoginCheckpoint(PendingPasswordHashHasher target, String input, boolean output, byte[] context) {
      return this.resolveSecondaryLoginCheckpoint(target, input, output, (targetValue, inputValue) -> targetValue.handleVerifiedLoginGate(inputValue, context));
   }

   public UpdateLookup(IndirectSessionHandler<?> target, MemClassLoader input) {
      ClassLoader output = input.getParentLoader();
      InputStream context = output.getResourceAsStream(LoaderBootstrap.class.getPackage().getName().replace('.', '/') + "/plugin.jar");

      try {
         if (context == null) {
            throw new RuntimeException("Corrupted JAR!");
         }

         this.indirectSessionHandler = target;
         this.pendingName = PlatformCatalog.PENDING_PLATFORMCATALOG.loadMessage(context);
         this.name = PlatformCatalog.PENDING_PLATFORMCATALOG.buildMessage(target.fetchParentSettingsLookup().retrieveFile());
         this.lowLoginResolver = new LowLoginResolver(target, this);
         this.linkedPasswordHashVerifier = new LinkedPasswordHashVerifier(target, this);
         PrimaryPasswordHashVerifier data = target.fetchParentSettingsLookup().loadPrimaryPasswordHashVerifier();
         AuthenticatedNoticeKind value = AuthenticatedNoticeKind.loadAuthenticatedNoticeKind(
            data.resolveByte("version-channel", (byte)AuthenticatedNoticeKind.AUTHENTICATED_NOTICE_KIND.ordinal())
         );
         this.authenticatedNoticeKind = value != null ? value : AuthenticatedNoticeKind.AUTHENTICATED_NOTICE_KIND;
         InputStream result = output.getResourceAsStream("META-INF/LICENSE.ID");

         try {
            if (result != null) {
               BufferedReader request = new BufferedReader(new InputStreamReader(result, StandardCharsets.UTF_8));

               try {
                  this.activeName = request.readLine();
                  data.resolvePrimaryPasswordHashVerifier("license-id", this.activeName).sendTask();
               } finally {
                  if (Collections.singletonList(request).get(0) != null) {
                     request.close();
                  }
               }
            } else {
               this.activeName = data.loadMessage("license-id");
            }

            String parameter = data.loadMessage("server_id");
            String response = data.loadMessage("server_secret");
            if (parameter != null && response != null) {
               this.liveLoginProcessor = new LiveLoginProcessor(parameter, response);
            }
         } finally {
            if (Collections.singletonList(result).get(0) != null) {
               result.close();
            }
         }
      } finally {
         if (Collections.singletonList(context).get(0) != null) {
            context.close();
         }
      }
   }

   public AuthenticatedNoticeKind retrieveAuthenticatedNoticeKind() {
      return this.authenticatedNoticeKind;
   }

   @Nullable
   public LiveLoginProcessor retrieveLiveLoginProcessor() {
      return this.liveLoginProcessor;
   }

   public synchronized void saveTask() {
      this.activeName = null;
      this.dispatchTask();
      PrimaryPasswordHashVerifier target = this.indirectSessionHandler.fetchParentSettingsLookup().loadPrimaryPasswordHashVerifier();
      target.createPrimaryPasswordHashVerifier("license-id");
      target.sendTask();
   }

   public synchronized void performTask() {
      this.liveLoginProcessor = null;
      PrimaryPasswordHashVerifier target = this.indirectSessionHandler.fetchParentSettingsLookup().loadPrimaryPasswordHashVerifier();
      target.createPrimaryPasswordHashVerifier("server_id");
      target.createPrimaryPasswordHashVerifier("server_secret");
      target.sendTask();
   }

   public String fetchMessage() {
      return this.name;
   }

   public synchronized boolean getState() {
      this.readyLoginGate = null;
      int target = this.indirectSessionHandler.getParentDiscordNotifier().resolveCount();
      String input = this.activeName != null && target != 0 ? this.activeName : "";
      String output = this.activeName != null && this.indirectSessionHandler.findIncomingLoginGate().retrieveSilentProxyState().loadState() && target == 1
         ? (String)this.indirectSessionHandler.buildObject(0)
         : "";
      Object[] context = new Object[]{
         "plugin",
         this.indirectSessionHandler.retrieveMessage(),
         "version",
         this.indirectSessionHandler.getMessage(),
         "api_major",
         6,
         "api_minor",
         2,
         "port",
         this.linkedPasswordHashVerifier.loadCount(),
         "loader_checksum",
         this.name,
         "checksum",
         this.pendingName,
         "server_id",
         this.liveLoginProcessor != null ? this.liveLoginProcessor.retrieveMessage() : "",
         "server_secret",
         this.liveLoginProcessor != null ? this.liveLoginProcessor.resolveMessage() : "",
         "platform_id",
         this.indirectSessionHandler.findIncomingLoginGate().retrieveSilentProxyState().ordinal(),
         "license_id",
         input,
         "license_mode",
         target,
         "license_addresses",
         output
      };
      SecondaryLoginCheckpoint data = this.buildSecondaryLoginCheckpoint(
         PendingPasswordHashHasher.getPendingPasswordHashHasher(), "/v%s/plugin/session", false, AuthenticatedLoginCheckpoint.createPayload(context)
      );
      int value = data.findCount();

      try {
         switch (value) {
            case 0:
            case 503:
               return false;
            case 200:
               JSONObject holder = new JSONObject(data.loadMessage());
               JSONObject reference = holder.getJSONObject("session");
               long subject = reference.getLong("id");
               String option = reference.getString("token");
               this.readyLoginGate = new ReadyLoginGate(subject, option);
               return true;
            case 201:
               JSONObject result = new JSONObject(data.loadMessage());
               JSONObject request = result.getJSONObject("server");
               String response = request.getString("id");
               String source = request.getString("secret");
               this.indirectSessionHandler
                  .fetchParentSettingsLookup()
                  .loadPrimaryPasswordHashVerifier()
                  .resolvePrimaryPasswordHashVerifier("server_id", response)
                  .resolvePrimaryPasswordHashVerifier("server_secret", source)
                  .sendTask();
               this.liveLoginProcessor = new LiveLoginProcessor(response, source);
               JSONObject entry = result.getJSONObject("session");
               long record = entry.getLong("id");
               String element = entry.getString("token");
               this.readyLoginGate = new ReadyLoginGate(record, element);
               return true;
            default:
               PasswordHashContainer.updateMessage("Unable to init session - the remote server sent an invalid response code (code = " + value + ")");
               return false;
         }
      } catch (JSONException content) {
         PasswordHashContainer.updateMessage(
            "Unable to init session - the remote server sent an invalid page (code = " + value + ", message = " + content.getLocalizedMessage() + ")"
         );
      } catch (Exception payload) {
         if (!(payload instanceof IllegalStateException) || !"zip file closed".equals(payload.getMessage())) {
            PasswordHashContainer.handleMessage("[nCore] Unexpected error (probably this is a bug!)", payload);
         }
      }

      return false;
   }

   public LinkedPasswordHashVerifier loadLinkedPasswordHashVerifier() {
      return this.linkedPasswordHashVerifier;
   }

   public SecondaryLoginCheckpoint createSecondaryLoginCheckpoint(PendingPasswordHashHasher target, String input, boolean output) {
      return this.resolveSecondaryLoginCheckpoint(target, input, output, PendingPasswordHashHasher::processVerifiedLoginGate);
   }

   @Nullable
   public SecondaryLoginCheckpoint fetchSecondaryLoginCheckpoint() {
      return this.secondaryLoginCheckpoint;
   }

   public String findMessage() {
      return this.pendingName;
   }

   public synchronized void executeTask() {
      if (this.readyLoginGate == null && !this.getState()) {
         this.secondaryLoginCheckpoint = SecondaryLoginCheckpoint.getSecondaryLoginCheckpoint();
      } else {
         byte[] target = new byte[4];
         SecureLoginHandler.retrieveRandom().nextBytes(target);
         this.values = target;
         PendingPasswordHashHasher input = PendingPasswordHashHasher.getPendingPasswordHashHasher();
         input.updateMessage("Accept", "application/json");
         input.updateMessage("Content-Type", "application/json");
         input.updateMessage("X-Signature", SafeLoginBarrier.resolveMessage(target));
         this.secondaryLoginCheckpoint = this.createSecondaryLoginCheckpoint(
            input, "/v%s/plugin/refresh?channel=" + this.authenticatedNoticeKind.ordinal(), true
         );
      }
   }

   @Nullable
   public String resolveMessage() {
      return this.activeName;
   }

   private SecondaryLoginCheckpoint resolveSecondaryLoginCheckpoint(PendingPasswordHashHasher target, String input, boolean output, PrivateListenerContract context) {
      if (output) {
         if (this.readyLoginGate == null) {
            throw new IllegalStateException("Session not started!");
         }

         target.sendMessage("X-Session-ID", this.readyLoginGate.retrieveTime());
         target.updateMessage("X-Session-Token", this.readyLoginGate.findMessage());
      }

      List data = this.linkedPasswordHashVerifier.buildCollection(entries);
      int value = data.size();
      String result = !data.equals(entries) && value > 1 ? (String)data.get((int)(Math.random() * value)) : (String)data.get(0);
      String request = String.format(input, 6);
      VerifiedLoginGate response = context.doRequest(target, result + request);
      if (response.fetchState()) {
         return new SecondaryLoginCheckpoint(response.loadPayload(), response.findCount(), result);
      }

      boolean source = entries.contains(result);
      if (value > 1) {
         for (String record : data) {
            if (!record.equals(result)) {
               if (!source) {
                  source = entries.contains(record);
               }

               response = context.doRequest(target, record + request);
               if (response.fetchState()) {
                  return new SecondaryLoginCheckpoint(response.loadPayload(), response.findCount(), record);
               }
            }
         }
      }

      if (!source) {
         for (String payload : entries) {
            response = context.doRequest(target, payload + request);
            if (response.fetchState()) {
               return new SecondaryLoginCheckpoint(response.loadPayload(), response.findCount(), payload);
            }
         }
      }

      return SecondaryLoginCheckpoint.getSecondaryLoginCheckpoint();
   }

   @Nullable
   public byte[] fetchPayload() {
      return this.values;
   }

   public int getCount() {
      return this.linkedPasswordHashVerifier.getCount();
   }

   @Nullable
   public ReadyLoginGate resolveReadyLoginGate() {
      return this.readyLoginGate;
   }

   public synchronized void dispatchTask() {
      this.readyLoginGate = null;
   }

   public synchronized void processTask() {
      this.performState(true);
   }

   public String getMessage() {
      return this.pendingName.substring(0, 6);
   }

   public void dispatchLinkedSessionHandler(LinkedSessionHandler target) {
      this.timestamp = System.currentTimeMillis();
      this.performState(false);
      target.processStrictCommandHandler(() -> {
         try {
            int targetValue = this.linkedPasswordHashVerifier.buildObject("rfrequency", 120);
            if (targetValue < 5 || targetValue > 3600) {
               targetValue = 120;
            }

            long input = System.currentTimeMillis();
            if (input - this.timestamp >= targetValue * 1000L) {
               this.timestamp = input;
               this.performState(true);
            }
         } catch (Throwable context) {
            if (!(context instanceof IllegalStateException) || !"zip file closed".equals(context.getMessage())) {
               PasswordHashContainer.handleMessage("[nCore] Unexpected error (probably this is a bug!)", context);
            }
         }
      }, 1L, 1L, TimeUnit.SECONDS);
   }

   public LowLoginResolver retrieveLowLoginResolver() {
      return this.lowLoginResolver;
   }

   public void updateAuthenticatedNoticeKind(AuthenticatedNoticeKind target) {
      this.authenticatedNoticeKind = target;
      this.indirectSessionHandler
         .fetchParentSettingsLookup()
         .loadPrimaryPasswordHashVerifier()
         .handlePrimaryPasswordHashVerifier("version-channel", (byte)target.ordinal())
         .sendTask();
   }
}
