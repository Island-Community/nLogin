package com.nickuc.login.session;

import com.nickuc.login.api.enums.event.EventEnum;
import com.nickuc.login.api.event.bukkit.auth.AuthenticateEvent;
import com.nickuc.login.api.event.internal.EventPlayer;
import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.message.FastMessageHandler;
import com.nickuc.login.auth.login.LocalLoginBarrier;
import com.nickuc.login.auth.message.MessageProcessor;
import com.nickuc.login.auth.locale.OpenLocaleBarrier;
import com.nickuc.login.auth.login.SafeLoginBarrier;
import com.nickuc.login.bukkit.BukkitPlatform;
import com.nickuc.login.command.spawn.PurgeUnregisterCommand;
import com.nickuc.login.config.BungeeWriter;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.config.ProxyDefinition;
import com.nickuc.login.model.LenientMessageKind;
import com.nickuc.login.model.LenientPremiumOption;
import com.nickuc.login.model.PlatformCatalog;
import com.nickuc.login.model.RemotePremiumState;
import com.nickuc.login.model.TightPlatformCatalog;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.nickuc.login.listener.bukkit.BusyMessageGuard;
import com.nickuc.login.listener.bukkit.LoginGuard;
import com.nickuc.login.listener.bukkit.VerifiedLoginGuard;
import com.nickuc.login.platform.proxy.QuickProxyState;
import com.nickuc.login.platform.account.SecondaryAccountHandler;
import com.nickuc.login.platform.message.TightMessageKind;
import com.nickuc.login.platform.server.VerifiedServerAdapter;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.security.hashing.PrimaryPasswordHashVerifier;
import com.nickuc.login.spawn.IndirectLoginKind;
import com.nickuc.login.spawn.LoginKind;
import com.nickuc.login.storage.settings.CachedSettingsGateway;
import com.nickuc.login.storage.password.PasswordStore;
import com.nickuc.login.tasks.SynchronizeWithServerThreadTask;
import com.nickuc.login.tasks.limbo.PlayerLimboProcessTask;
import com.nickuc.login.tasks.limbo.PlayerLimboRestoreTask;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.PluginManager;

public class LimboSupervisor {
   private final AtomicLong atomicLong;
   private byte[] values;
   private final BukkitPlatform BukkitPlatform;
   private final Timer timer;
   private final AtomicLong activeAtomicLong;
   private final Object object;
   private static float factor = Float.intBitsToFloat(1097859072);
   private final Cache<Long, CompletableFuture<JSONObject>> cache;
   private final Cache<VerifiedServerAdapter, Object> activeCache;
   private static float activeFactor = Float.intBitsToFloat(1077936128);
   private static float pendingFactor = Float.intBitsToFloat(1097859072);
   private final File dataFile;
   private final Cache<VerifiedServerAdapter, Set<byte[]>> pendingCache;
   private long timestamp;
   private static float currentFactor = Float.intBitsToFloat(1077936128);
   private final BusyMessageGuard busyMessageGuard = new BusyMessageGuard(this);

   private void updateTask() {
      PluginManager target = this.BukkitPlatform.getServer().getPluginManager();
      if (target.getPlugin("BungeeGuard") == null && (!BungeeWriter.retrieveState() || LoginGuard.resolveLoginGuard().checkState(LoginGuard.internalLoginGuard))) {
         PasswordHashContainer.processMessage("§9Downloading BungeeGuard...");
         File input = this.BukkitPlatform.resolveFile().getParentFile();
         File output = new File(input, "BungeeGuard.jar");
         if (output.exists() && !output.delete()) {
            output.deleteOnExit();
            PasswordHashContainer.updateMessage("Unable to delete " + output + " file");
            return;
         }

         LocalLoginBarrier context = PendingPasswordHashHasher.getPendingPasswordHashHasher()
            .loadLocalLoginBarrier("https://github.com/nickuc/BungeeGuard/releases/latest/download/BungeeGuard.jar", output);
         if (context.findCount() == 200 && context.retrieveState()) {
            try {
               target.loadPlugin(output);
            } catch (Exception value) {
               PasswordHashContainer.handleMessage("Unable to load BungeeGuard, removing jar...", value);
               if (!output.delete()) {
                  output.deleteOnExit();
               }
            }
         } else {
            PasswordHashContainer.updateMessage("Unable to download BungeeGuard, code = " + context.findCount());
         }
      }
   }

   public void sendTask() {
      this.BukkitPlatform.findMessageListener().dispatchObject("nlogin:main", this.busyMessageGuard);
   }

   private void handleVerifiedServerAdapter(VerifiedServerAdapter target, byte[] input) {
      try {
         if (input.length == 0) {
            return;
         }

         String output = new String(input, StandardCharsets.UTF_8);
         if (output.isEmpty()) {
            return;
         }

         try {
            if (output.charAt(0) == '{' && output.charAt(output.length() - 1) == '}') {
               this.saveVerifiedServerAdapter(target, output);
               return;
            }

            PasswordHashContainer.updateMessage("Malformed JSON received from backend server: \"%s\"", output);
         } catch (JSONException value) {
            PasswordHashContainer.handleMessage("Malformed JSON received from backend server: \"%s\"", value, output);
         }
      } catch (Exception result) {
         if (input[0] != 123) {
            long context = System.currentTimeMillis();
            if (context - this.timestamp >= 5000L) {
               this.timestamp = context;
               PasswordHashContainer.updateMessage("Unable to decode plugin message packet. Did you forget to update nLogin on the proxy server?");
            }
         } else {
            PasswordHashContainer.handleMessage("Unable to read nLogin plugin message.", result);
         }
      }
   }

   private void handleTask() {
      if (this.values != null) {
         throw new IllegalStateException("Secret key already defined!");
      }

      if (this.dataFile.exists()) {
         this.values = Files.readAllBytes(this.dataFile.toPath());
      }
   }

   private void saveVerifiedServerAdapter(VerifiedServerAdapter target, String input) {
      this.executeVerifiedServerAdapter(target);
      JSONObject output = new JSONObject(input);
      if (!output.has("secret")) {
         PasswordHashContainer.performMessage("Unable to decode plugin message packet: missing secret key");
         PasswordHashContainer.performMessage("Please update your plugin to the latest version");
      } else {
         byte[] context = SafeLoginBarrier.loadPayload(output.getString("secret").getBytes(StandardCharsets.UTF_8));
         if (this.values == null) {
            MessageProcessor.updateInputStream(new ByteArrayInputStream(context), this.dataFile);
            this.handleTask();
         }

         if (!Arrays.equals(context, this.values)) {
            PasswordHashContainer.performMessage("Unable to decode plugin message packet: invalid secret");
            String item = OpenLocaleBarrier.loadMessage(
               "§4[nLogin] Unable to decode plugin message packet: invalid secret",
               "",
               "§cIf you are an administrator, please delete the file `nLogin/secret.key` from your backend server and restart it."
            );
            target.buildCompletableFuture(item);
         } else {
            if (output.has("packets")) {
               JSONArray data = output.getJSONArray("packets");
               if (data.length() == 0) {
                  throw new IllegalArgumentException("Received an empty packet array!");
               }

               for (int value = 0; value < data.length(); value++) {
                  JSONObject result = data.getJSONObject(value);

                  try {
                     this.processVerifiedServerAdapter(target, result);
                  } catch (JSONException entry) {
                     int element = result.getInt("id");
                     String content = result.getString("player");
                     PasswordHashContainer.handleMessage("Unable to read packet " + element + " [player = " + content + ", index = " + value + "].", entry);
                  } catch (Exception record) {
                     int response = result.getInt("id");
                     String source = result.getString("player");
                     PasswordHashContainer.handleMessage("Unable to process packet " + response + " [player = " + source + ", index = " + value + "].", record);
                  }
               }
            } else {
               this.processVerifiedServerAdapter(target, output);
            }
         }
      }
   }

   public LimboSupervisor(BukkitPlatform target) {
      this.activeAtomicLong = new AtomicLong();
      this.pendingCache = Caffeine.newBuilder().expireAfterAccess(45L, TimeUnit.SECONDS).build();
      this.activeCache = Caffeine.newBuilder().expireAfterWrite(30L, TimeUnit.SECONDS).build();
      this.object = new Object();
      this.atomicLong = new AtomicLong();
      this.cache = Caffeine.newBuilder().expireAfterWrite(30L, TimeUnit.SECONDS).build();
      this.BukkitPlatform = target;
      this.timer = new Timer(target.retrieveMessage() + " Await Ack Message Timer");
      this.dataFile = new File(target.resolveFile(), "secret.key");
      this.handleTask();
   }

   public void sendVerifiedServerAdapter(VerifiedServerAdapter target, byte[] input) {
      try {
         if (input.length == 0) {
            return;
         }

         Set output = (Set)this.pendingCache.get(target, instance -> new HashSet());
         synchronized (output) {
            if (this.BukkitPlatform.fetchPasswordStore().loadLimboRegistry().buildLimboCoordinator(target) != null && output.isEmpty()) {
               this.handleVerifiedServerAdapter(target, input);
            } else {
               output.add(input);
            }
         }
      } catch (Exception result) {
         PasswordHashContainer.handleMessage("Unable to read nLogin plugin message.", result);
      }
   }

   public CompletableFuture<JSONObject> computeCompletableFuture(VerifiedServerAdapter target, LoginKind input, Object... output) {
      CompletableFuture context = new CompletableFuture();
      synchronized (this.cache) {
         long value = this.atomicLong.incrementAndGet();
         this.cache.put(value, context);
         this.updateVerifiedServerAdapter(target, 6, "requestId", value, "resource", input.getCount(), "arguments", input.computeJSONArray(output));
         return context;
      }
   }

   public void saveVerifiedServerAdapter(VerifiedServerAdapter target) {
      if (TightMessageKind.TIGHT_MESSAGE_KIND.ar()) {
         this.timer.schedule(new ProxyDefinition(this, target), 10000L);
      }

      Set input = (Set)this.pendingCache.get(target, instance -> new HashSet());
      synchronized (input) {
         input.forEach(inputValue -> this.handleVerifiedServerAdapter(target, inputValue));
         input.clear();
      }
   }

   public void updateVerifiedServerAdapter(VerifiedServerAdapter target, int input, Object... output) {
      if (output.length % 2 != 0) {
         throw new IllegalArgumentException("Not in key and value format!");
      }

      JSONObject context = new JSONObject();
      if (this.values != null) {
         context.put("secret", SafeLoginBarrier.resolveMessage(this.values));
      }

      context.put("id", input);
      context.put("player", target.getName());

      for (int data = 0; data < output.length; data++) {
         Object value = output[data++];
         if (!(value instanceof String)) {
            throw new IllegalArgumentException("Key is not a string! " + value);
         }

         Object result = output[data];
         context.put((String)value, result != null ? result : JSONObject.NULL);
      }

      byte[] response = context.toString().getBytes(StandardCharsets.UTF_8);
      if (!this.BukkitPlatform.fetchPasswordStore().resolveRootMessageHandler().validateState(target, "nlogin:main", response)) {
         target.performIndirectSessionHandler(this.BukkitPlatform, RemotePremiumState.ACTIVE_REMOTEPREMIUMSTATE, "nlogin:main", response);
      }
   }

   private void processVerifiedServerAdapter(VerifiedServerAdapter target, JSONObject input) {
      String output = target.getName();
      int context = input.getInt("id");
      PasswordStore data = this.BukkitPlatform.fetchPasswordStore();
      LimboRegistry value = data.loadLimboRegistry();
      switch (context) {
         case -1:
            String notice = input.getString("server_name");
            boolean identity = input.getBoolean("auth_server");
            if (!identity) {
               Player location = target.findObject();
               BungeeWriter.resolveCompletableFuture(
                  location,
                  CachedSettingsGateway.loadState()
                     ? "§4[nLogin] Erro de configuração detectado no servidor proxy:\n§r\n§r§cO servidor atual §f\""
                        + notice
                        + "\" §cnão está listado como um servidor de autenticação.\n§r\n§r§cRemova o §bnLogin §cse os jogadores não se autenticam neste servidor.\n§r\n§r§eSe isso for um erro, por favor adicione o servidor\n§r§eno arquivo §f\"nLogin/proxy/config.yml\" §edo servidor proxy."
                     : "§4[nLogin] Error detected on proxy server setup:\n§r\n§r§cThe current server §f\""
                        + notice
                        + "\" §cis not listed as an authentication server.\n§r\n§r§cRemove §bnLogin §cif players don't authenticate in this server.\n§r\n§r§eIf this is an error, please add the server in\n§r§ethe §f\"nLogin/proxy/config.yml\" §efile of the proxy server."
               );
            }
            break;
         case 0:
            int message = input.getInt("stage");
            switch (message) {
               case 0:
                  String profile = input.getString("hash");
                  File backend = new File(this.BukkitPlatform.resolveFile(), "settings.data");
                  long action = System.currentTimeMillis();
                  if (action - this.activeAtomicLong.getAndSet(action) <= 15000L
                     || backend.exists() && PlatformCatalog.PENDING_PLATFORMCATALOG.validateState(backend, profile)) {
                     this.updateVerifiedServerAdapter(target, context, "action", 1);
                  } else {
                     this.updateVerifiedServerAdapter(target, context, "action", 0);
                  }

                  return;
               case 1:
                  File player = new File(this.BukkitPlatform.resolveFile(), "settings.data");

                  try {
                     if (!player.exists() || !PlatformCatalog.PENDING_PLATFORMCATALOG.validateState(player, input.getString("hash")) && player.delete()) {
                        ByteArrayInputStream proxy = FastMessageHandler.loadByteArrayInputStream(input.getString("content"));
                        MessageProcessor.updateInputStream(proxy, player);
                        TightMessageKind.canState(this.BukkitPlatform, false);
                        return;
                     }
                  } catch (IOException option) {
                     PasswordHashContainer.handleMessage("Unable to update " + player.getName() + " file.", option);
                  }

                  return;
               default:
                  return;
            }
         case 1:
            LimboCoordinator argument = value.loadLimboCoordinator(target);
            BusyLimboStore account = (BusyLimboStore)data.findSettingsLinker();
            int client = input.getInt("action");
            switch (client) {
               case 0:
                  if (!value.canState(target)) {
                     int status = input.has("use-register-spawn") ? input.getBoolean("use-register-spawn") : (!input.getBoolean("registered") ? 1 : 0);
                     target.getLinkedSessionHandler()
                        .buildStrictCommandHandler(new PlayerLimboProcessTask(() -> account.saveVerifiedServerAdapter(target, argument, status)));
                  }

                  return;
               case 1:
                  target.getLinkedSessionHandler().buildStrictCommandHandler(new PlayerLimboRestoreTask(() -> account.executeVerifiedServerAdapter(target, argument)));
                  return;
               default:
                  return;
            }
         case 2:
            if (!value.canState(target)) {
               int parameter = input.getInt("type");
               int session = input.has("sessionFromProxy") && input.getBoolean("sessionFromProxy") ? 1 : 0;
               int server = input.has("restoreLimbo") && !input.getBoolean("restoreLimbo") ? 0 : 1;
               LimboCoordinator state = value.loadLimboCoordinator(target);
               if (server != 0) {
                  target.getLinkedSessionHandler()
                     .buildStrictCommandHandler(new PlayerLimboRestoreTask(() -> data.findSettingsLinker().executeVerifiedServerAdapter(target, state)));
               } else {
                  state.updateLenientMessageKind(LenientMessageKind.SAFE_LENIENTMESSAGEKIND, true);
               }

               state.executeTightPlatformCatalog(TightPlatformCatalog.LOCAL_TIGHTPLATFORMCATALOG, null);
               if (session != 0) {
                  Player activeInput = target.findObject();
                  this.BukkitPlatform.processLinkedSessionHandler(true).buildStrictCommandHandler(() -> data.callEvent(new AuthenticateEvent(activeInput)));
               }

               PasswordHashContainer.dispatchMessage(
                  output + " player has successfully authenticated. (loginType=" + parameter + ", sessionFromProxy=" + session + ", restoreLimbo=" + server + ")"
               );
            }
            break;
         case 3:
            Player attribute = target.findObject();
            PrimaryPasswordHashVerifier packet = this.BukkitPlatform.a().loadPrimaryPasswordHashVerifier();
            LimboCoordinator channel = PasswordStore.resolvePasswordStore().loadLimboRegistry().loadLimboCoordinator(target);
            SecondaryAccountHandler spawn = channel.getSecondaryAccountHandler();
            int future = input.getInt("action");
            switch (future) {
               case 0:
                  IndirectLoginKind activeValue = IndirectLoginKind.handleIndirectLoginKind(input.getString("type"));
                  if (activeValue != null) {
                     if (activeValue == IndirectLoginKind.MAIN_INDIRECTLOGINKIND) {
                        packet.createPrimaryPasswordHashVerifier(activeValue.fetchMessage(), true).sendTask();
                     } else {
                        packet.resolvePrimaryPasswordHashVerifier(activeValue.fetchMessage(), VerifiedLoginGuard.createMessage(attribute.getLocation())).sendTask();
                     }

                     String pendingOutput = input.getString("message.render-spawn-list");
                     if (pendingOutput != null) {
                        LenientPremiumOption pendingResult = LenientPremiumOption.handleLenientPremiumOption(pendingOutput);
                        PurgeUnregisterCommand.updatePasswordStore(data, spawn, pendingResult);
                     }

                     if (activeValue == IndirectLoginKind.MAIN_INDIRECTLOGINKIND && !packet.hasState(IndirectLoginKind.INDIRECT_LOGIN_KIND.fetchMessage())) {
                        spawn.executeMessage(input.getString("message.error"));
                        CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.ACTIVE_PRIMARY_QUICKPROXYSTATE);
                     } else {
                        spawn.executeMessage(input.getString("message.success"));
                        CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, pendingFactor, currentFactor);
                     }

                     BusyLimboStore primaryTarget = (BusyLimboStore)data.findSettingsLinker();
                     primaryTarget.loadSpawnSession().processPasswordStore(data, false);
                  }

                  return;
               case 1:
                  IndirectLoginKind activeData = IndirectLoginKind.handleIndirectLoginKind(input.getString("type"));
                  if (activeData != null) {
                     String pendingInput = packet.loadMessage(activeData.fetchMessage());
                     if (pendingInput == null) {
                        spawn.executeMessage(input.getString("message.not-set"));
                     } else {
                        if (activeData == IndirectLoginKind.MAIN_INDIRECTLOGINKIND) {
                           packet.createPrimaryPasswordHashVerifier(activeData.fetchMessage(), false).sendTask();
                        } else {
                           packet.createPrimaryPasswordHashVerifier(activeData.fetchMessage()).sendTask();
                        }

                        String pendingValue = input.getString("message.render-spawn-list");
                        if (pendingValue != null) {
                           LenientPremiumOption primaryInput = LenientPremiumOption.handleLenientPremiumOption(pendingValue);
                           PurgeUnregisterCommand.updatePasswordStore(data, spawn, primaryInput);
                        }

                        spawn.executeMessage(input.getString("message.success"));
                        CachedSettingsGateway.processOutgoingSenderAdapter(target, QuickProxyState.LIVE_QUICKPROXYSTATE, factor, activeFactor);
                        BusyLimboStore primaryOutput = (BusyLimboStore)data.findSettingsLinker();
                        primaryOutput.loadSpawnSession().processPasswordStore(data, false);
                     }

                     return;
                  }

                  return;
               case 2:
                  IndirectLoginKind activeContext = IndirectLoginKind.handleIndirectLoginKind(input.getString("type"));
                  if (activeContext != null && activeContext != IndirectLoginKind.MAIN_INDIRECTLOGINKIND) {
                     try {
                        String pendingTarget = packet.loadMessage(activeContext.fetchMessage());
                        if (pendingTarget == null) {
                           spawn.executeMessage(input.getString("message.not-set"));
                           return;
                        } else {
                           Location pendingData = VerifiedLoginGuard.resolveLocation(pendingTarget);
                           if (pendingData == null) {
                              spawn.executeMessage(input.getString("message.decode-error"));
                           } else {
                              target.getLinkedSessionHandler().buildStrictCommandHandler(new SynchronizeWithServerThreadTask(() -> {
                                 if (BungeeWriter.fetchState()) {
                                    attribute.teleportAsync(pendingData, TeleportCause.PLUGIN);
                                 } else {
                                    attribute.teleport(pendingData, TeleportCause.PLUGIN);
                                 }
                              }));
                              spawn.executeMessage(input.getString("message.success"));
                           }

                           return;
                        }
                     } catch (Exception subject) {
                        PasswordHashContainer.handleMessage("Failed to teleport to spawn: " + subject.getLocalizedMessage(), subject);
                        spawn.executeMessage(input.getString("message.unknown-error"));
                        return;
                     }
                  }

                  return;
               case 3:
               default:
                  return;
               case 4:
                  String activeOutput = input.getString("message.render-spawn-list");
                  if (activeOutput != null) {
                     LenientPremiumOption activeResult = LenientPremiumOption.handleLenientPremiumOption(activeOutput);
                     PurgeUnregisterCommand.updatePasswordStore(data, spawn, activeResult);
                  }

                  return;
            }
         case 4:
            Player property = target.findObject();
            int event = input.getInt("action");
            target.getLinkedSessionHandler().buildStrictCommandHandler(new SynchronizeWithServerThreadTask(() -> {
               switch (event) {
                  case 0:
                     QuickProxyState responseValue = QuickProxyState.valueOf(input.getString("sound"));
                     float sourceValue = input.getFloat("volume");
                     float resultValue = input.getFloat("pitch");
                     responseValue.handleConsumer(outputValue -> property.playSound(property.getLocation(), outputValue, sourceValue, resultValue));
                     break;
                  case 1:
                     String requestValue = input.getString("command");
                     boolean valueValue = input.getBoolean("isConsole");
                     if (valueValue) {
                        this.BukkitPlatform.b().findAuthenticatedServerAdapter().performMessage(requestValue);
                     } else {
                        target.processMessageForValue(requestValue);
                     }
                     break;
                  case 2:
                     String dataValue = input.getString("plugin");
                     if ("bungeeguard".equalsIgnoreCase(dataValue)) {
                        this.updateTask();
                     }
               }
            }));
         case 5:
         default:
            break;
         case 6:
            long setting = input.getLong("requestId");
            synchronized (this.cache) {
               CompletableFuture position = (CompletableFuture)this.cache.getIfPresent(setting);
               if (position != null) {
                  this.cache.invalidate(setting);
                  if (input.has("exception")) {
                     String task = input.getString("exception");
                     position.completeExceptionally(new RuntimeException(task));
                  } else {
                     Object job = input.get("response");
                     position.complete(job instanceof JSONObject ? (JSONObject)job : null);
                  }
               }
               break;
            }
         case 7:
            EventEnum result = EventEnum.valueOf(input.getString("event"));
            if (!result.isForwardEvent()) {
               throw new IllegalArgumentException("The event " + result + " does not support forwarding!");
            }

            JSONArray request = input.getJSONArray("arguments");
            Object[] response = new Object[request.length()];
            Class[] source = result.getRewrittenClasses();
            if (response.length != source.length) {
               throw new RuntimeException("Arguments mismatch classes count! condition = " + response.length + " != " + source.length + ", event = " + result);
            }

            for (int entry = 0; entry < response.length; entry++) {
               JSONObject record = request.getJSONObject(entry);
               Class item = source[entry];
               if (EventPlayer.class.isAssignableFrom(item)) {
                  response[entry] = target.findObject();
               } else if (!String[].class.isAssignableFrom(item)) {
                  if (UUID.class.isAssignableFrom(item)) {
                     response[entry] = input.has("value") ? DeadLoginFlow.buildUniqueId(input.getString("value")) : null;
                  } else {
                     Object pendingContext = record.has("value") ? record.get("value") : null;
                     if (Enum.class.isAssignableFrom(item)) {
                        if (!(pendingContext instanceof String)) {
                           throw new RuntimeException("Argument " + entry + " is an enum, but the value isn't a string!");
                        }

                        try {
                           response[entry] = item.getMethod("valueOf", String.class).invoke(null, (String)pendingContext);
                        } catch (ReflectiveOperationException holder) {
                           PasswordHashContainer.handleMessage("Unable to handle event api request: ReflectiveOperationException", holder);
                        }
                     } else {
                        response[entry] = pendingContext;
                     }
                  }
               } else if (!input.has("value")) {
                  response[entry] = new String[0];
               } else {
                  JSONArray element = input.getJSONArray("value");
                  String[] content = new String[element.length()];

                  for (int payload = 0; payload < content.length; payload++) {
                     content[payload] = element.getString(payload);
                  }

                  response[entry] = content;
               }
            }

            this.BukkitPlatform.processLinkedSessionHandler(true).buildStrictCommandHandler(() -> data.verifyState(result, response));
      }
   }

   public void executeVerifiedServerAdapter(VerifiedServerAdapter target) {
      synchronized (this.activeCache) {
         this.activeCache.put(target, this.object);
      }
   }
}
