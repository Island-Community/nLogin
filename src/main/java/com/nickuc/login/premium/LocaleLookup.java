package com.nickuc.login.premium;

import com.nickuc.login.auth.login.AuthenticatedLoginCheckpoint;
import com.nickuc.login.auth.login.CachedLoginBarrier;
import com.nickuc.login.auth.login.ChildLoginCheckpoint;
import com.nickuc.login.auth.login.IncomingLoginGate;
import com.nickuc.login.auth.login.LiveLoginCheckpoint;
import com.nickuc.login.auth.login.LiveLoginProcessor;
import com.nickuc.login.auth.login.LocalLoginBarrier;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.ReadyLoginGate;
import com.nickuc.login.auth.login.SecondaryLoginCheckpoint;
import com.nickuc.login.auth.login.VerifiedLoginGate;
import com.nickuc.login.command.PasswordHashCommand;
import com.nickuc.login.config.PasswordHashContainer;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Throwables;
import org.json.JSONException;
import org.json.JSONObject;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.platform.sender.OutgoingSenderAdapter;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.security.hashing.VerifiedPasswordHashHasher;
import com.nickuc.login.updater.AuthenticatedNoticeKind;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LocaleLookup extends PasswordHashCommand<IndirectSessionHandler<?>> {
   private static final Cache<String, Long> cache = Caffeine.newBuilder().expireAfterWrite(1L, TimeUnit.MINUTES).build();

   private static String createMessage(Class<?> instance) {
      if (instance.getClassLoader() == null) {
         return "§b(" + System.getProperty("java.runtime.version") + ", native)";
      }

      try {
         return "§e(" + MessageProcessor.handleFile(instance).toPath() + ")";
      } catch (Exception input) {
         return "";
      }
   }

   @Override
   public void saveOutgoingSenderAdapter(OutgoingSenderAdapter target, String input, String[] output) {
      if (target instanceof VerifiedServerAdapter) {
         target.dispatchMessage("§cThis command can only be executed in the console.");
      } else if (output.length == 0) {
         UpdateLookup option = this.indirectSessionHandler.retrieveUpdateLookup();
         LiveLoginProcessor setting = option.retrieveLiveLoginProcessor();
         ReadyLoginGate property = option.resolveReadyLoginGate();
         IncomingLoginGate account = this.indirectSessionHandler.findIncomingLoginGate();
         target.dispatchMessage("");
         StringBuilder switchState = new StringBuilder()
            .append(" §eRunning §f")
            .append(this.indirectSessionHandler.retrieveMessage())
            .append(" v")
            .append(this.indirectSessionHandler.getMessage())
            .append(" §b")
            .append(option.getMessage());
         this.indirectSessionHandler.getObject();
         target.dispatchMessage(switchState.append(false ? " §c(compromised)" : "").toString());
         if (this.indirectSessionHandler.retrieveUpdateLookup().getCount() == 9) {
            target.dispatchMessage(" §6⭐ Premium version");
         }

         target.dispatchMessage("");
         target.dispatchMessage("  §7Implementation version: §fv26.07");
         target.dispatchMessage("  §7Identifier: " + (setting != null ? "§f" + setting.retrieveMessage() : "§cUnavailable"));
         target.dispatchMessage("  §7Session ID: " + (property != null ? "§f" + OpenLocaleBarrier.resolveMessage(property.retrieveTime()) : "§cUnavailable"));
         target.dispatchMessage("  §7Platform: §f" + account.retrieveSilentProxyState().getName() + " " + account.getMessage());
         target.dispatchMessage("");
      } else {
         String context = output[0].toLowerCase(Locale.ENGLISH);
         switch (context) {
            case "unlink":
               UpdateLookup session = this.indirectSessionHandler.retrieveUpdateLookup();
               session.saveTask();
               session.processTask();
               target.dispatchMessage("§cLicense successfully unlinked.");
               CachedLoginBarrier channel = this.indirectSessionHandler.fetchParentSettingsLookup().findCachedLoginBarrier();
               channel.dispatchState(true);
               channel.saveConsumer(targetValue -> {
                  if (!targetValue) {
                     target.dispatchMessage("§ePlease download the JAR from our website to finish the license removal.");
                  }
               });
               break;
            case "class":
               if (output.length != 2) {
                  target.dispatchMessage("§7Insufficient arguments.");
               } else {
                  String packet = output[1].replace("/", ".");
                  Class connection = ChildLoginCheckpoint.loadClass(packet);
                  if (connection != null) {
                     target.dispatchMessage("");
                     target.dispatchMessage("§7Loader tree:");
                     ClassLoader backend = connection.getClassLoader();
                     target.dispatchMessage("§7 - §f" + connection.getCanonicalName() + " " + createMessage(connection));
                     if (backend != null) {
                        byte state = 3;

                        do {
                           target.dispatchMessage(
                              "§7"
                                 + OpenLocaleBarrier.buildMessage(" ", state)
                                 + "- §f"
                                 + backend.getClass().getCanonicalName()
                                 + " "
                                 + createMessage(backend.getClass())
                           );
                           state += 2;
                        } while ((backend = backend.getParent()) != null);
                     }

                     target.dispatchMessage("");
                  } else {
                     target.dispatchMessage("§cThe class does not exist.");
                  }
               }
               break;
            case "dump":
            case "support":
               if (!this.hasState(target, "dump", 60)) {
                  target.dispatchMessage("§cYou must wait a while to run this command again.");
               } else {
                  UpdateLookup event = this.indirectSessionHandler.retrieveUpdateLookup();
                  target.dispatchMessage("§7Creating support code...");
                  JSONObject identity = new JSONObject();
                  identity.put("plugin", this.indirectSessionHandler.toString());
                  identity.put("version", "26.07");
                  JSONObject proxy = new JSONObject();
                  event.retrieveLowLoginResolver().performJSONObject(proxy);
                  identity.put("extra", proxy);
                  SecondaryLoginCheckpoint spawn = event.fetchSecondaryLoginCheckpoint();
                  int task = spawn != null && spawn.fetchState() ? 1 : 0;

                  try {
                     identity.put("response", task != 0 ? new JSONObject(spawn.loadMessage()) : JSONObject.NULL);
                  } catch (JSONException subject) {
                     identity.put("response", task != 0 ? spawn.loadMessage() : JSONObject.NULL);
                  }

                  JSONObject job = VerifiedPasswordHashHasher.resolveJSONObject(false);
                  identity.put("settings", job);
                  PendingPasswordHashHasher future = PendingPasswordHashHasher.getPendingPasswordHashHasher();
                  byte[] activeInput = identity.toString().getBytes(StandardCharsets.UTF_8);
                  byte[] activeContext = AuthenticatedLoginCheckpoint.createPayload(future, activeInput);
                  SecondaryLoginCheckpoint payload = event.buildSecondaryLoginCheckpoint(future, "/paste", false, activeContext);
                  switch (payload.findCount()) {
                     case 200:
                        target.dispatchMessage("§aSupport code successfully created: §b" + payload.loadMessage());
                        return;
                     case 429:
                        target.dispatchMessage("§cUnable to create the support code: too many requests");
                        return;
                     default:
                        Throwable activeData = payload.resolveThrowable();
                        target.dispatchMessage(
                           "§cUnable to create the support code: [" + payload.findCount() + "] " + (activeData != null ? " " + activeData.getMessage() : "")
                        );
                  }
               }
               break;
            case "version":
               UpdateLookup notice = this.indirectSessionHandler.retrieveUpdateLookup();
               LinkedPasswordHashVerifier profile = notice.loadLinkedPasswordHashVerifier();
               String client = profile.findMessage();
               String position = notice.getMessage();
               if (!profile.findState() && position.equals(client)) {
                  target.dispatchMessage(
                     "§aThis server is running the latest version of "
                        + this.indirectSessionHandler.retrieveMessage()
                        + " (channel: "
                        + notice.retrieveAuthenticatedNoticeKind()
                        + ")."
                  );
               } else {
                  AuthenticatedNoticeKind action = profile.fetchAuthenticatedNoticeKind();
                  target.dispatchMessage(
                     "§aA "
                        + action.getName()
                        + " version of "
                        + this.indirectSessionHandler.retrieveMessage()
                        + " is available ("
                        + this.indirectSessionHandler.getMessage()
                        + " "
                        + position
                        + " » "
                        + profile.getMessage()
                        + " "
                        + client
                        + ", channel: "
                        + notice.retrieveAuthenticatedNoticeKind()
                        + ")."
                  );
               }
               break;
            case "download":
               if (!this.hasState(target, "download", 40)) {
                  target.dispatchMessage("§cYou must wait a while to run this command again.");
               } else {
                  CachedLoginBarrier message = this.indirectSessionHandler.fetchParentSettingsLookup().findCachedLoginBarrier();
                  message.dispatchState(true);
                  message.saveConsumer(
                     targetValue -> target.dispatchMessage(targetValue ? "§aLatest version has been downloaded." : "§cUnable to download the latest version.")
                  );
               }
               break;
            case "refresh":
               if (!this.hasState(target, "global", 10)) {
                  target.dispatchMessage("§cYou must wait a while to run this command again.");
               } else {
                  this.indirectSessionHandler.retrieveUpdateLookup().processTask();
                  SecondaryLoginCheckpoint argument = this.indirectSessionHandler.retrieveUpdateLookup().fetchSecondaryLoginCheckpoint();
                  target.dispatchMessage(
                     argument != null && argument.fetchState() ? "§aRemote information successfully updated." : "§cRemote information could not be updated."
                  );
               }
               break;
            case "debug":
               int parameter = !PasswordHashContainer.findState() ? 1 : 0;
               this.indirectSessionHandler
                  .fetchParentSettingsLookup()
                  .loadPrimaryPasswordHashVerifier()
                  .createPrimaryPasswordHashVerifier("debug", (boolean)parameter);
               PasswordHashContainer.saveState((boolean)parameter);
               target.dispatchMessage(parameter != 0 ? "§aDebug mode enabled." : "§cDebug mode disabled.");
               break;
            case "url":
               if (output.length != 2) {
                  target.dispatchMessage("§cUsage: /" + input + " " + context + " <url>");
               } else {
                  String attribute = output[1];
                  LiveLoginCheckpoint player = new LiveLoginCheckpoint();
                  VerifiedLoginGate server = PendingPasswordHashHasher.getPendingPasswordHashHasher().processVerifiedLoginGate(attribute);
                  String location = server.loadMessage();
                  if (location != null) {
                     target.dispatchMessage("§7[" + server.findCount() + "] " + location);
                     target.dispatchMessage("§eRequest took " + player.fetchMessage() + "s");
                  } else {
                     Throwable status = server.resolveThrowable();
                     if (status != null) {
                        target.dispatchMessage("§c" + Throwables.getStackTraceAsString(status));
                     }

                     target.dispatchMessage("§cUnable to connect to " + attribute + ".");
                  }
               }
               break;
            case "dependency":
               if (output.length != 3) {
                  target.dispatchMessage("§cUsage: /" + input + " " + context + " <url> <file>");
               } else {
                  String result = output[1];
                  String request = "https://";
                  if (result.startsWith(request) && result.length() > request.length()) {
                     String response = result.substring(request.length());
                     String[] source = response.split("/");
                     if (source.length >= 2) {
                        response = source[0];
                     } else if (response.charAt(response.length() - 1) == '/') {
                        response = response.substring(0, response.length() - 1);
                     }

                     String entry = "nickuc.";
                     if (!response.endsWith(entry.concat("com")) && !response.endsWith(entry.concat("net"))) {
                        target.dispatchMessage("§cThe url entered is not from a trusted source. Aborting...");
                     } else {
                        File record = new File(this.indirectSessionHandler.resolveFile().getParentFile(), output[2]);
                        if (record.exists() && !record.delete()) {
                           target.dispatchMessage("§cThe file exists and could not be deleted.");
                        } else {
                           LiveLoginCheckpoint item = new LiveLoginCheckpoint();
                           LocalLoginBarrier element = PendingPasswordHashHasher.getPendingPasswordHashHasher().loadLocalLoginBarrier(result, record);
                           if (element.retrieveState()) {
                              long content = element.fetchTime();
                              target.dispatchMessage("§7[" + element.findCount() + "] Length: " + OpenLocaleBarrier.resolveMessage(content) + " bytes");
                              double holder = item.computeRatio(TimeUnit.SECONDS);
                              target.dispatchMessage(
                                 "§eDownload took "
                                    + OpenLocaleBarrier.resolveMessage(holder, 2)
                                    + "s (≃"
                                    + OpenLocaleBarrier.processMessage(content / holder)
                                    + " bytes/s)"
                              );
                           } else {
                              Throwable activeOutput = element.resolveThrowable();
                              if (activeOutput != null) {
                                 target.dispatchMessage("§c" + Throwables.getStackTraceAsString(activeOutput));
                              }

                              target.dispatchMessage("§cUnable to download from " + result + ".");
                           }
                        }
                     }
                  } else {
                     target.dispatchMessage("§cThe url provided is invalid.");
                  }
               }
               break;
            default:
               target.dispatchMessage("§cUnknown subcommand.");
         }
      }
   }

   public LocaleLookup(IndirectSessionHandler<?> target) {
      super(target.retrieveMessage().toLowerCase(Locale.ENGLISH) + "c");
      this.performNames(
         target.retrieveMessage().toLowerCase(Locale.ENGLISH)
            + target.findIncomingLoginGate().retrieveSilentProxyState().resolveMessage().toLowerCase(Locale.ENGLISH)
            + "c"
      );
      this.getPasswordHashCommand();
      this.computePasswordHashCommand("Core command for " + target.retrieveMessage());
   }

   private boolean hasState(OutgoingSenderAdapter target, String input, int output) {
      String context = target.getName() + input;
      Long data = (Long)cache.getIfPresent(context);
      long value = System.currentTimeMillis();
      if (data != null && value - data <= output * 1000L) {
         return false;
      }

      cache.put(context, value);
      return true;
   }
}
