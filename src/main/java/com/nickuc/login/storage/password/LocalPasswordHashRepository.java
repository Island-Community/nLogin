package com.nickuc.login.storage.password;

import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.login.PrimaryLoginHandler;
import com.nickuc.login.auth.login.PrimaryLoginService;
import com.nickuc.login.auth.update.UpdateCheckpoint;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.OutgoingSpawnState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.nickuc.login.platform.listener.SharedListenerContract;
import com.nickuc.login.premium.SpawnLookup;
import com.nickuc.login.security.hashing.CachedPasswordHashHasher;
import com.nickuc.login.spawn.PremiumOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class LocalPasswordHashRepository {
   private Timestamp resolveTimestamp(long target) {
      return new Timestamp(target <= 0L ? System.currentTimeMillis() : target);
   }

   private UUID resolveUniqueId(String target) {
      target = this.processMessage(target, true);
      return target != null && !target.equals("0") ? DeadLoginFlow.buildUniqueId(target.replace("-", "")) : null;
   }

   private long loadTime(Object target) {
      if (target instanceof String) {
         String input = (String)target;
         if (!input.isEmpty() && !"null".equalsIgnoreCase(input)) {
            try {
               return Long.parseLong(input);
            } catch (NumberFormatException context) {
               return System.currentTimeMillis();
            }
         } else {
            return System.currentTimeMillis();
         }
      } else if (target instanceof Number) {
         return ((Number)target).longValue();
      } else {
         return target instanceof Timestamp ? ((Timestamp)target).getTime() : System.currentTimeMillis();
      }
   }

   private void sendSpawnLookup(SpawnLookup target, String input) {
      byte output = 0;
      if (!input.isEmpty()) {
         if (input.length() >= 2 && !this.canState(input)) {
            try {
               input = new String(Base64.getDecoder().decode(input));
            } catch (Exception entry) {
            }
         }

         if (this.canState(input)) {
            try {
               JSONObject context = new JSONObject(input);
               JSONArray data = context.getJSONArray("settings");
               CachedPasswordHashHasher value = target.resolveCachedPasswordHashHasher();

               for (int result = 0; result < data.length(); result++) {
                  JSONObject request = data.getJSONObject(result);
                  String response = request.getString("key");
                  Object source = request.get("value");
                  value.sessions.put(response, source);
               }

               output = 1;
            } catch (JSONException record) {
               PasswordHashContainer.performMessage("Failed to decode settings from user " + target.retrieveMessage() + ": malformed JSON");
            } catch (Exception item) {
               PasswordHashContainer.handleMessage("Failed to decode settings from user " + target.retrieveMessage() + ".", item);
            }
         }
      }

      if (output == 0) {
         target.cachedPasswordHashHasher.enabled = true;
      } else {
         target.cachedPasswordHashHasher.dispatchTask();
      }
   }

   private Object[] resolveValues(Object[] target, OutgoingSpawnState... input) {
      if (target.length % 2 != 0) {
         throw new IllegalArgumentException("Need both column name and value for parameters!");
      }

      int output = 0;
      int context = input.length > 0 ? 1 : 0;
      HashSet data = context != 0 ? new HashSet<>(Arrays.asList(input)) : null;
      Object[] value = context != 0 ? new Object[(data.size() + 1) * 2] : (Object[])target.clone();

      for (byte result = 0; result < target.length; result += 2) {
         Object request = target[result];
         if (!(request instanceof OutgoingSpawnState)) {
            throw new IllegalArgumentException("Not a DatabaseColumns value!");
         }

         OutgoingSpawnState response = (OutgoingSpawnState)request;
         if (context == 0 || data.contains(response) || response == OutgoingSpawnState.OUTGOING_SPAWN_STATE) {
            value[output++] = response.getName();
            value[output++] = target[result + 1];
         }
      }

      return value;
   }

   private boolean canState(@Nonnull String target) {
      return target.length() >= 2 && target.charAt(0) == '{' && target.charAt(target.length() - 1) == '}';
   }

   public SpawnLookup processSpawnLookup(ResultSet target) {
      String input = null;

      try {
         long output = target.getLong(OutgoingSpawnState.OUTGOING_SPAWN_STATE.getName());
         input = this.processMessage(target.getString(OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName()), false);
         String data = this.processMessage(target.getString(OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName()), true);
         String value = this.processMessage(target.getString(OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName()), true);
         UUID result = this.resolveUniqueId(target.getString(OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName()));
         UUID request = this.resolveUniqueId(target.getString(OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName()));
         UUID response = this.resolveUniqueId(target.getString(OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE.getName()));
         byte source = 0;
         if (request != null && request.version() != 4) {
            request = null;
            source = 1;
         }

         long entry = this.loadTime(target.getObject(OutgoingSpawnState.CACHED_OUTGOINGSPAWNSTATE.getName()));
         long item = this.loadTime(target.getObject(OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName()));
         SpawnLookup content = new SpawnLookup(output, input, result, request, response, data, value, item, entry);
         content.quickDiscordHandler.activeName = this.processMessage(target.getString(OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE.getName()), true);
         content.quickDiscordHandler.name = this.processMessage(target.getString(OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE.getName()), true);
         String payload = this.processMessage(target.getString(OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE.getName()), true);
         content.cachedPasswordHashHasher.name = payload;
         if (payload != null) {
            this.sendSpawnLookup(content, payload);
         }

         if (source != 0) {
            content.performTask();
         }

         return content;
      } catch (SQLException holder) {
         PasswordHashContainer.handleMessage("Failed to build " + (input != null ? "the " + input + "'s " : "") + "account", holder);
      } catch (Exception reference) {
         PasswordHashContainer.handleMessage("Failed to build the " + input + "'s account (probably a bug)", reference);
      }

      return null;
   }

   @Nullable
   public PrimaryLoginHandler processPrimaryLoginHandler(SharedListenerContract target, String input, Object... output) {
      if (input == null || input.isEmpty()) {
         throw new IllegalArgumentException("Options cannot be null or empty!");
      }

      if (output.length == 0) {
         throw new IllegalArgumentException("Values cannot be empty!");
      }

      if (target == null) {
         throw new IllegalStateException("Database is not loaded to perform a build operation.");
      }

      long context = System.nanoTime();

      try {
         String value = String.format("SELECT * FROM `%s`", SpawnState.CURRENT_SPAWNSTATE.a(new Object[0])) + " " + input;
         return target.buildPrimaryLoginHandler(value, output);
      } catch (Exception entry) {
         PasswordHashContainer.handleMessage("Failed to build the account using \"" + input + "\" with values \"" + Arrays.toString(output) + "\"", entry);
      } finally {
         PrimaryLoginService.savePremiumOption(PremiumOption.CACHED_PREMIUMOPTION, context);
      }

      return null;
   }

   public boolean checkState(SharedListenerContract target, SpawnLookup input, OutgoingSpawnState... output) {
      synchronized (input.object) {
         String data = input.activeName;
         if (data == null) {
            throw new IllegalStateException("Account name is not loaded to perform an update operation.");
         }

         if (target == null) {
            throw new IllegalStateException("Database is not loaded to perform an update operation.");
         }

         long value = System.nanoTime();

         try {
            String request = input.cachedPasswordHashHasher.fetchMessage();
            String response = null;

            try {
               int source = 0;
               if (input.value != null) {
                  Object[] entry = this.resolveValues(
                     new Object[]{
                        OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE,
                        data,
                        OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE,
                        DeadLoginFlow.processMessage(input.pendingUniqueId),
                        OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE,
                        DeadLoginFlow.processMessage(input.activeUniqueId),
                        OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE,
                        DeadLoginFlow.processMessage(input.uniqueId),
                        OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE,
                        input.name,
                        OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE,
                        input.pendingName,
                        OutgoingSpawnState.CACHED_OUTGOINGSPAWNSTATE,
                        this.resolveTimestamp(input.timestamp),
                        OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE,
                        this.resolveTimestamp(input.activeTimestamp),
                        OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE,
                        input.quickDiscordHandler.activeName,
                        OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE,
                        input.quickDiscordHandler.name,
                        OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE,
                        request,
                        OutgoingSpawnState.OUTGOING_SPAWN_STATE,
                        input.value
                     },
                     output
                  );
                  String record = target.resolveDirectNoticeCatalog() != DirectNoticeCatalog.ACTIVE_DIRECTNOTICECATALOG
                        && target.resolveDirectNoticeCatalog() != DirectNoticeCatalog.DIRECT_NOTICE_CATALOG
                     ? ""
                     : "LIMIT 1";
                  source = UpdateCheckpoint.handleSecondaryUpdateCheckpoint(SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]), 1, record, entry).loadCount(target);
               }

               if (source == 0) {
                  Object[] account = new Object[]{
                     OutgoingSpawnState.ACTIVE_OUTGOINGSPAWNSTATE.getName(),
                     data,
                     OutgoingSpawnState.PENDING_OUTGOINGSPAWNSTATE.getName(),
                     DeadLoginFlow.processMessage(input.pendingUniqueId),
                     OutgoingSpawnState.CURRENT_OUTGOINGSPAWNSTATE.getName(),
                     DeadLoginFlow.processMessage(input.activeUniqueId),
                     OutgoingSpawnState.PRIMARY_OUTGOINGSPAWNSTATE.getName(),
                     DeadLoginFlow.processMessage(input.uniqueId),
                     OutgoingSpawnState.MAIN_OUTGOINGSPAWNSTATE.getName(),
                     input.name,
                     OutgoingSpawnState.LOCAL_OUTGOINGSPAWNSTATE.getName(),
                     input.pendingName,
                     OutgoingSpawnState.CACHED_OUTGOINGSPAWNSTATE.getName(),
                     this.resolveTimestamp(input.timestamp),
                     OutgoingSpawnState.REMOTE_OUTGOINGSPAWNSTATE.getName(),
                     this.resolveTimestamp(input.activeTimestamp),
                     OutgoingSpawnState.STORED_OUTGOINGSPAWNSTATE.getName(),
                     input.quickDiscordHandler.activeName,
                     OutgoingSpawnState.VERIFIED_OUTGOINGSPAWNSTATE.getName(),
                     input.quickDiscordHandler.name,
                     OutgoingSpawnState.AUTHENTICATED_OUTGOINGSPAWNSTATE.getName(),
                     request
                  };
                  input.value = UpdateCheckpoint.processSecondaryUpdateCheckpoint(SpawnState.CURRENT_SPAWNSTATE.a(new Object[0]), account).computeTime(target);
               }

               response = input.loadMessage(source > 0, request);
               input.cachedPasswordHashHasher.enabled = false;
               return true;
            } catch (SQLException message) {
               PasswordHashContainer.handleMessage(
                  "Unable to "
                     + (input.resolveStateForState() ? "update" : "insert")
                     + " "
                     + data
                     + " account's "
                     + input.hashCode()
                     + " [database modified manually?]",
                  message
               );
            } catch (Exception notice) {
               PasswordHashContainer.handleMessage(
                  "Unable to " + (input.resolveStateForState() ? "update" : "insert") + " " + data + " account's [corrupted data?]", notice
               );
            } finally {
               if (response == null) {
                  response = input.loadMessage(input.resolveStateForState(), request);
               }

               PasswordHashContainer.dispatchMessage(
                  "["
                     + target.resolveDirectNoticeCatalog().resolveMessage()
                     + "] [UPDATE]: \""
                     + response
                     + "\" (applied to "
                     + (output.length == 0 ? "all" : Arrays.stream(output).map(OutgoingSpawnState::getName).collect(Collectors.toList()))
                     + " columns)"
               );
            }
         } finally {
            PrimaryLoginService.savePremiumOption(PremiumOption.STORED_PREMIUMOPTION, value);
         }

         return false;
      }
   }

   private String processMessage(String target, boolean input) {
      return target == null || target.isEmpty() || input && target.equalsIgnoreCase("null") ? null : target;
   }
}
