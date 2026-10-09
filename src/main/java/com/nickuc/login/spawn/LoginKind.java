package com.nickuc.login.spawn;

import com.nickuc.login.api.nLoginAPI;
import com.nickuc.login.auth.account.AccountGate;
import com.nickuc.login.auth.account.AccountHandler;
import com.nickuc.login.auth.mojang.MojangBarrier;
import com.nickuc.login.auth.login.RemoteLoginGate;
import org.json.JSONArray;
import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import com.nickuc.login.storage.login.LoginArchive;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.annotation.Nullable;


public enum LoginKind {
   LOGIN_KIND(0, "getDatabaseType", LoginArchive.loginArchive),
   ACTIVE_LOGINKIND(1, "getAccount", true, AccountHandler.accountHandler, MojangBarrier.mojangBarrier),
   PENDING_LOGINKIND(2, "getAccountsByIp", AccountGate.accountGate, RemoteLoginGate.pendingRemoteLoginGate),
   CURRENT_LOGINKIND(13, "getAccountCount", RemoteLoginGate.currentRemoteLoginGate),
   PRIMARY_LOGINKIND(3, "isAuthenticated", RemoteLoginGate.currentRemoteLoginGate, MojangBarrier.mojangBarrier),
   MAIN_LOGINKIND(4, "getRemainingSeconds", RemoteLoginGate.currentRemoteLoginGate, MojangBarrier.mojangBarrier),
   LOCAL_LOGINKIND(5, "comparePassword", RemoteLoginGate.remoteLoginGate, AccountHandler.accountHandler, RemoteLoginGate.pendingRemoteLoginGate),
   REMOTE_LOGINKIND(
      6,
      "performRegister",
      RemoteLoginGate.remoteLoginGate,
      MojangBarrier.mojangBarrier,
      RemoteLoginGate.pendingRemoteLoginGate,
      RemoteLoginGate.pendingRemoteLoginGate
   ),
   CACHED_LOGINKIND(7, "performUnregister", RemoteLoginGate.remoteLoginGate, MojangBarrier.mojangBarrier),
   STORED_LOGINKIND(8, "changePassword", RemoteLoginGate.remoteLoginGate, MojangBarrier.mojangBarrier, RemoteLoginGate.pendingRemoteLoginGate),
   VERIFIED_LOGINKIND(9, "setEmail", RemoteLoginGate.remoteLoginGate, MojangBarrier.mojangBarrier, RemoteLoginGate.pendingRemoteLoginGate),
   AUTHENTICATED_LOGINKIND(10, "setDiscord", RemoteLoginGate.remoteLoginGate, MojangBarrier.mojangBarrier, RemoteLoginGate.activeRemoteLoginGate),
   SHARED_LOGINKIND(12, "setLanguage", RemoteLoginGate.remoteLoginGate, MojangBarrier.mojangBarrier, RemoteLoginGate.pendingRemoteLoginGate),
   PRIVATE_LOGINKIND(11, "forceLogin", RemoteLoginGate.remoteLoginGate, MojangBarrier.mojangBarrier, RemoteLoginGate.remoteLoginGate);

   private final int count;
   private final Method method;
   private final boolean enabled;
   private final DirectPacketAdapter<Object> directPacketAdapter;
   private final DirectPacketAdapter<Object>[] values;

   LoginKind(int output, String context, boolean data, DirectPacketAdapter<?> value, DirectPacketAdapter<?>... result) {
      this.count = output;
      this.enabled = data;
      this.directPacketAdapter = value;
      this.values = result;
      Class[] request = Arrays.stream(result).map(DirectPacketAdapter::loadClass).toArray(Class[]::new);

      try {
         this.method = nLoginAPI.class.getMethod(context, request);
      } catch (NoSuchMethodException source) {
         throw new RuntimeException(source);
      }
   }

   LoginKind(int output, String context, DirectPacketAdapter<?> data, DirectPacketAdapter<?>... value) {
      this(output, context, false, data, value);
   }

   @Nullable
   public static LoginKind handleLoginKind(int instance) {
      for (LoginKind context : values()) {
         if (instance == context.count) {
            return context;
         }
      }

      return null;
   }

   public DirectPacketAdapter<Object> findDirectPacketAdapter() {
      return this.directPacketAdapter;
   }

   public Object computeObject(nLoginAPI target, Object... input) {
      return this.method.invoke(target, input);
   }

   public DirectPacketAdapter<Object>[] fetchValues() {
      return this.values;
   }

   public JSONArray computeJSONArray(Object... target) {
      JSONArray input = new JSONArray();

      for (int output = 0; output < this.values.length; output++) {
         input.put(output, this.values[output].buildJSONObject(target[output]));
      }

      return input;
   }

   public Object[] processValues(JSONArray target) {
      Object[] input = new Object[target.length()];

      for (int output = 0; output < input.length; output++) {
         JSONObject context = target.getJSONObject(output);
         input[output] = this.values[output].loadObject(context);
      }

      return input;
   }

   public static void load() {
      for (LoginKind output : values()) {
         output.saveTask();
      }
   }

   private void saveTask() {
   }

   public boolean findState() {
      return this.enabled;
   }

   public int getCount() {
      return this.count;
   }
}
