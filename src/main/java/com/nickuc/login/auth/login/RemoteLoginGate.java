package com.nickuc.login.auth.login;

import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import javax.annotation.Nonnull;


public class RemoteLoginGate<T> implements DirectPacketAdapter<T> {
   private final Class<T> value;
   private final Class<?> activeValue;
   public static RemoteLoginGate<Boolean> remoteLoginGate = new RemoteLoginGate<>(boolean.class, Boolean.class);
   public static RemoteLoginGate<Long> activeRemoteLoginGate = new RemoteLoginGate<>(long.class, Long.class);
   public static RemoteLoginGate<String> pendingRemoteLoginGate = new RemoteLoginGate<>(String.class, String.class);
   public static RemoteLoginGate<Integer> currentRemoteLoginGate = new RemoteLoginGate<>(int.class, Integer.class);

   @Override
   public T loadObject(@Nonnull JSONObject target) {
      Object input = target.get("value");
      if (this.value.isInstance(input)) {
         return this.value.cast(input);
      } else {
         throw new IllegalArgumentException("Cannot deserialize " + input.getClass() + ", expected " + this.value);
      }
   }

   @Override
   public JSONObject buildJSONObject(@Nonnull Object target) {
      JSONObject input = new JSONObject();
      input.put("value", target);
      return input;
   }

   @Override
   public Class<T> getClass() {
      return this.value;
   }

   @Override
   public Class<?> loadClass() {
      return this.activeValue;
   }

   public RemoteLoginGate(Class<?> target, Class<T> input) {
      this.activeValue = target;
      this.value = input;
   }
}
