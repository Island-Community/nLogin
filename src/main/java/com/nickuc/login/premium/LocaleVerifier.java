package com.nickuc.login.premium;

import com.nickuc.login.auth.login.DeadLoginFlow;
import com.nickuc.login.auth.login.IncomingLoginGate;
import com.nickuc.login.auth.login.LenientLoginFlow;
import com.nickuc.login.auth.login.LiveLoginProcessor;
import com.nickuc.login.auth.login.ReadyLoginGate;
import com.nickuc.login.auth.login.VerifiedLoginGate;
import com.nickuc.login.config.PasswordHashContainer;
import com.nickuc.login.model.ProxyState;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.json.JSONArray;
import org.json.JSONObject;
import com.nickuc.login.platform.session.IndirectSessionHandler;
import com.nickuc.login.security.hashing.PendingPasswordHashHasher;
import com.nickuc.login.session.MojangCoordinator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;

public class LocaleVerifier {
   private static volatile long timestamp;
   private static volatile long activeTimestamp;
   private static final AtomicInteger atomicInteger = new AtomicInteger();
   private static final Cache<String, LoginVerifier> cache = Caffeine.newBuilder().expireAfterWrite(10L, TimeUnit.MINUTES).build();
   private static final AtomicLong atomicLong = new AtomicLong();

   public static boolean verifyState(IndirectSessionHandler<?> instance, UUID target) {
      if (target.version() != 4) {
         return false;
      }

      ProxyState input = Pbkdf2Linker.findProxyState();
      switch (input) {
         case PENDING_PROXYSTATE:
            return false;
         case ACTIVE_PROXYSTATE:
            return true;
         case PROXY_STATE:
            LoginVerifier output = handleLoginVerifier(instance, target, false);
            return output == null || output.fetchUniqueId() != null;
         default:
            throw new IllegalArgumentException("Unsupported premium id action! " + input);
      }
   }

   public static boolean checkState(IndirectSessionHandler<?> instance, @Nullable LoginVerifier target, String input) {
      if (target == null) {
         target = handleLoginVerifier(instance, input, false);
      }

      return target == null || target.fetchUniqueId() != null;
   }

   @Nullable
   private static LoginVerifier resolveLoginVerifier(IndirectSessionHandler<?> instance, String target, String input, boolean output) {
      String context = input.toLowerCase(Locale.ENGLISH);
      LoginVerifier data = (LoginVerifier)cache.getIfPresent(context);
      if (data != null) {
         return data;
      }

      if (!output) {
         long value = System.currentTimeMillis();
         if (value - timestamp <= 15000L) {
            return null;
         }

         if (value - activeTimestamp > 1000L) {
            atomicInteger.set(0);
            activeTimestamp = value;
         }

         if (atomicInteger.incrementAndGet() >= 50) {
            if (value - atomicLong.getAndSet(value) > 300000L) {
               PasswordHashContainer.performMessage("Applying UUID query restriction: rate limit reached.");
            }

            timestamp = value;
            return null;
         }
      }

      UpdateLookup content = instance.retrieveUpdateLookup();
      ReadyLoginGate result = content.resolveReadyLoginGate();
      LiveLoginProcessor request = content.retrieveLiveLoginProcessor();
      byte response = 5;
      VerifiedLoginGate source = null;
      String entry = null;

      label96:
      for (int record = 0; record < 5; record++) {
         String item = target.replace("@server_id", request != null ? request.retrieveMessage() : "")
            .replace("@session_id", result != null ? Long.toString(result.retrieveTime()) : "")
            .replace("@platform", Integer.toString(instance.findIncomingLoginGate().retrieveSilentProxyState().ordinal()))
            .replace("@name_or_uuid", input)
            .replace("@index", Integer.toString(record));
         source = PendingPasswordHashHasher.getPendingPasswordHashHasher().processVerifiedLoginGate(item);
         switch (source.findCount()) {
            case 200:
               String element = source.loadMessage();
               if ("stop".equals(element)) {
                  return null;
               }

               entry = element;
               break label96;
            case 204:
            case 404:
               data = new LoginVerifier(null, null);
               break label96;
            default:
               if (record == 4) {
                  return null;
               }
         }
      }

      if (data == null && entry != null && !entry.isEmpty()) {
         if (entry.charAt(0) == '{' && entry.charAt(entry.length() - 1) == '}') {
            JSONObject payload = new JSONObject(entry);
            if (payload.has("id") && payload.has("name")) {
               String subject = payload.getString("id");
               String option = payload.getString("name");
               data = new LoginVerifier(DeadLoginFlow.buildUniqueId(subject), option);
            } else if (payload.has("uuid")) {
               String reference = payload.getString("uuid");
               data = new LoginVerifier(DeadLoginFlow.buildUniqueId(reference), null);
            }
         }

         if (data == null) {
            data = new LoginVerifier(null, null);
         }
      }

      if (data != null) {
         cache.put(context, data);
      } else {
         int holder = source != null ? source.findCount() : 0;
         switch (holder) {
            case 0:
               PasswordHashContainer.performMessage("Unable to contact Mojang servers for " + input + ": auth servers are down [got " + source.findCount() + "]");
               break;
            case 429:
               PasswordHashContainer.performMessage("Unable to contact Mojang servers for " + input + ": too many requests [got " + source.findCount() + "]");
               break;
            default:
               PasswordHashContainer.performMessage(
                  "Unable to contact Mojang servers for " + input + ": unexpected response code [got " + source.findCount() + "]"
               );
         }
      }

      return data;
   }

   public static boolean canState(@Nullable LoginVerifier instance, UUID target) {
      if (target.version() != 4) {
         return false;
      }

      ProxyState input = Pbkdf2Linker.findProxyState();
      switch (input) {
         case PENDING_PROXYSTATE:
            return false;
         case ACTIVE_PROXYSTATE:
            return true;
         case PROXY_STATE:
            return instance == null || target.equals(instance.fetchUniqueId());
         default:
            throw new IllegalArgumentException("Unsupported premium id action! " + input);
      }
   }

   public static VerifiedLoginGate loadVerifiedLoginGate(IndirectSessionHandler<?> instance, String target, String input, @Nullable String output) {
      PendingPasswordHashHasher context = PendingPasswordHashHasher.getPendingPasswordHashHasher();
      IncomingLoginGate data = instance.findIncomingLoginGate();
      context.updateMessage("User-Agent", data.retrieveMessageForMessage());
      context.updateMessage("Content-Type", "application/json");
      return context.processVerifiedLoginGate(
         String.format("%s/session/minecraft/hasJoined?username=%s&serverId=%s%s", MojangCoordinator.activeName, target, input, output == null ? "" : "&ip=" + output)
      );
   }

   @Nullable
   public static LoginVerifier handleLoginVerifier(IndirectSessionHandler<?> instance, UUID target, boolean input) {
      return resolveLoginVerifier(instance, "https://api.nickuc.com/v4/nlogin/uuid?uniqueId=@name_or_uuid&id=@index", DeadLoginFlow.processMessage(target), input);
   }

   @Nullable
   public static LoginVerifier handleLoginVerifier(IndirectSessionHandler<?> instance, String target, boolean input) {
      return resolveLoginVerifier(instance, "https://api.nickuc.com/v4/nlogin/uuid?username=@name_or_uuid&id=@index", target, input);
   }

   public static LoginVerifier computeLoginVerifier(String instance) {
      JSONObject target = new JSONObject(instance);
      UUID input = DeadLoginFlow.buildUniqueId(target.getString("id"));
      String output = target.getString("name");
      JSONArray context = target.getJSONArray("properties");
      ArrayList data = new ArrayList();

      for (int value = 0; value < context.length(); value++) {
         JSONObject result = context.getJSONObject(value);
         if (result.has("value") && result.has("signature")) {
            data.add(new LenientLoginFlow(result.getString("value"), result.getString("signature")));
            break;
         }
      }

      return new LoginVerifier(input, output, data.toArray(new LenientLoginFlow[0]));
   }
}
