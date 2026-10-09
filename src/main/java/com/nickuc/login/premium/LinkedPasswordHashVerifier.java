package com.nickuc.login.premium;

import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.auth.login.LiveLoginProcessor;
import com.nickuc.login.auth.login.RemoteLoginBarrier;
import com.nickuc.login.auth.login.VerifiedLoginGate;
import com.nickuc.login.config.PasswordHashContainer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.security.hashing.PrimaryPasswordHashVerifier;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import com.nickuc.login.updater.AuthenticatedNoticeKind;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;


public class LinkedPasswordHashVerifier {
   private byte mask;
   private final IndirectSessionHandler<?> indirectSessionHandler;
   private byte activeMask;
   private boolean enabled;
   private final UpdateLookup updateLookup;
   private final HashMap<String, Object> sessions = new HashMap<>();

   public void handleVerifiedLoginGate(VerifiedLoginGate target) {
      if (target != null && target.fetchState() && target.loadMessage() != null) {
         byte event = 0;
         int output = target.findCount();

         try {
            switch (output) {
               case 200:
                  String context = target.loadMessage();
                  JSONObject data = new JSONObject(context);
                  this.sessions.clear();
                  this.sessions.put("address", data.getString("address"));
                  LiveLoginProcessor value = this.updateLookup.retrieveLiveLoginProcessor();
                  if (value == null) {
                     throw new IllegalStateException();
                  }

                  byte[] result = this.updateLookup.fetchPayload();
                  if (value.hasState(data, context, result)) {
                     PrimaryPasswordHashVerifier request = this.indirectSessionHandler.fetchParentSettingsLookup().loadPrimaryPasswordHashVerifier();
                     byte[] response = request.createPayload("signature");
                     if (response == null || !Arrays.equals(result, response)) {
                        request.computePrimaryPasswordHashVerifier("signature", result)
                           .computePrimaryPasswordHashVerifier(
                              "signature-update", FastMessageHandler.buildPayload(instance -> instance.handleTime(System.currentTimeMillis()))
                           )
                           .sendTask();
                     }

                     if (data.has("license")) {
                        JSONObject source = data.getJSONObject("license");

                        for (String record : source.keySet()) {
                           this.sessions.put("license." + record, source.get(record));
                        }

                        if (this.getCount() == 0) {
                           this.updateLookup.saveTask();
                        }
                     }

                     if (data.has("servers")) {
                        JSONArray packet = data.getJSONArray("servers");
                        String[] account = new String[packet.length()];

                        for (int profile = 0; profile < packet.length(); profile++) {
                           account[profile] = "https://" + packet.getString(profile);
                        }

                        this.sessions.put("servers", RemoteLoginBarrier.loadRemoteLoginBarrier(account));
                     }

                     JSONObject session = data.getJSONObject("plugin");
                     if (session.has("force_update")) {
                        this.sessions.put("plugin.force_update", session.getBoolean("force_update"));
                     }

                     JSONObject player = session.getJSONObject("latest");

                     for (String item : player.keySet()) {
                        this.sessions.put("plugin.latest." + item, player.get(item));
                     }

                     if (session.has("extra")) {
                        JSONObject connection = session.getJSONObject("extra");

                        for (String element : connection.keySet()) {
                           this.sessions.put("plugin.extra." + element, connection.get(element));
                        }
                     }

                     if (this.mask > 0) {
                        PasswordHashContainer.processMessage("§aConnection re-established with remote server");
                     }

                     this.mask = 0;
                     this.activeMask = 0;
                     this.enabled = true;
                     event = 1;
                  }
                  break;
               case 401:
                  this.updateLookup.performTask();
               case 403:
                  if (this.updateLookup.getState()) {
                     this.updateLookup.processTask();
                     event = 1;
                  }
                  break;
               case 503:
                  if (VerifiedPasswordHashHasher.as() || this.activeMask++ == 1) {
                     PasswordHashContainer.performMessage("The remote server is under maintenance (code = " + output + ")");
                  }
                  break;
               default:
                  if (VerifiedPasswordHashHasher.as() || this.activeMask++ == 1) {
                     PasswordHashContainer.updateMessage("The remote server sent an invalid response code (code = " + output + ")");
                  }
            }
         } catch (JSONException parameter) {
            PasswordHashContainer.updateMessage("The remote server sent an invalid page (code = " + output + ", message = " + parameter.getLocalizedMessage() + ")");
         } catch (Exception argument) {
            if (!(argument instanceof IllegalStateException) || !"zip file closed".equals(argument.getMessage())) {
               PasswordHashContainer.handleMessage("[nCore] Unexpected error (probably this is a bug!)", argument);
            }
         } finally {
            if (event == 0) {
               this.sendTask();
            }
         }
      } else {
         try {
            VerifiedLoginGate input = PendingPasswordHashHasher.getPendingPasswordHashHasher().processVerifiedLoginGate("https://checkip.amazonaws.com/");
            if (input.fetchState()) {
               if (VerifiedPasswordHashHasher.as() || this.activeMask++ == 1) {
                  PasswordHashContainer.updateMessage(
                     "Unable to connect to the api - remote server is unavailable (code = " + (target != null ? target.findCount() : 0) + ")"
                  );
               }
            } else if (VerifiedPasswordHashHasher.as() || this.activeMask++ == 1) {
               PasswordHashContainer.updateMessage("Unable to connect to the api - internet unavailable or HTTPS port closed (this likely is a hosting error!)");
            }
         } finally {
            this.sendTask();
         }
      }
   }

   @Nullable
   public <T> T handleObject(String target) {
      return this.buildObject(target, null);
   }

   public String fetchMessage() {
      return this.sessions.getOrDefault("address", "127.0.0.1") + ":" + this.loadCount();
   }

   public String retrieveMessage() {
      String target = (String)this.sessions.getOrDefault("plugin.latest.signature-url", "");
      if (target != null && !target.isEmpty()) {
         return target;
      }

      AuthenticatedNoticeKind input = this.fetchAuthenticatedNoticeKind();
      return "https://repo.nickuc.com/signature?name=" + this.indirectSessionHandler.retrieveMessage() + "&channel=" + input.getName();
   }

   @Nullable
   public String getMessage() {
      return (String)this.sessions.get("plugin.latest.version");
   }

   public int getCount() {
      return 1;
   }

   @Nullable
   public String loadMessage() {
      return (String)this.sessions.get("plugin.latest.checksum");
   }

   @Override
   public String toString() {
      return "RemoteResponse(data="
         + this.sessions
         + ", remote="
         + this.updateLookup
         + ", connectionAvailable="
         + this.resolveState()
         + ", errorMessageSent="
         + this.activeMask
         + ", failedRefresh="
         + this.mask
         + ")";
   }

   public int loadCount() {
      return (Integer)this.indirectSessionHandler.buildObject(1);
   }

   public List<String> buildCollection(List<String> target) {
      return (List<String>)this.sessions.getOrDefault("servers", target);
   }

   public AuthenticatedNoticeKind fetchAuthenticatedNoticeKind() {
      return AuthenticatedNoticeKind.loadAuthenticatedNoticeKind(
         (Integer)this.sessions.getOrDefault("plugin.latest.type", AuthenticatedNoticeKind.AUTHENTICATED_NOTICE_KIND.ordinal())
      );
   }

   public <T> T buildObject(String target, T input) {
      Object output = this.sessions.get("plugin.extra." + target);
      return (T)(output != null ? output : input);
   }

   private void sendTask() {
      int target = this.updateLookup.loadLinkedPasswordHashVerifier().buildObject("rfrequency", 120);
      int input = 960 / target;
      int output = input * 4;
      int context = this.buildObject("allowed-fails", input);
      if (context < 0 || context > output) {
         context = input;
      }

      if (this.mask <= context && ++this.mask >= context) {
         this.enabled = false;
      }
   }

   public String findMessage() {
      return (String)this.sessions.getOrDefault("plugin.latest.build", "??????");
   }

   public boolean resolveState() {
      return this.enabled;
   }

   public boolean loadState() {
      return (Boolean)this.sessions.getOrDefault("plugin.force_update", false);
   }

   public boolean findState() {
      if (this.loadState()) {
         return true;
      }

      String target = this.getMessage();
      return target != null ? !this.indirectSessionHandler.getMessage().equals(target) : this.activeMask >= 3;
   }

   public String resolveMessage() {
      String target = (String)this.sessions.getOrDefault("plugin.latest.url", "");
      if (target != null && !target.isEmpty()) {
         return target;
      }

      AuthenticatedNoticeKind input = this.fetchAuthenticatedNoticeKind();
      return "https://repo.nickuc.com/download?name=" + this.indirectSessionHandler.retrieveMessage() + "&channel=" + input.getName();
   }

   public LinkedPasswordHashVerifier(IndirectSessionHandler<?> target, UpdateLookup input) {
      this.indirectSessionHandler = target;
      this.updateLookup = input;
   }
}
