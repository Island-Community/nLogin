package com.nickuc.login.auth.login;

import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import java.util.UUID;
import javax.annotation.Nonnull;

public class ActiveLoginHandler implements DirectPacketAdapter<UUID> {
   public static final ActiveLoginHandler activeLoginHandler = new ActiveLoginHandler();

   public UUID resolveUniqueId(@Nonnull JSONObject target) {
      String input = RemoteLoginGate.pendingRemoteLoginGate.loadObject(target);
      return DeadLoginFlow.buildUniqueId(input);
   }

   public JSONObject handleJSONObject(@Nonnull UUID target) {
      return RemoteLoginGate.pendingRemoteLoginGate.buildJSONObject(DeadLoginFlow.processMessage(target));
   }

   @Override
   public Class<?> loadClass() {
      return UUID.class;
   }
}
