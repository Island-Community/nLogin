package com.nickuc.login.spawn;

import com.nickuc.login.api.enums.SpawnType;
import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import javax.annotation.Nonnull;

public class SpawnLocator implements DirectPacketAdapter<SpawnType> {
   public static final SpawnLocator spawnLocator = new SpawnLocator();

   @Override
   public Class<?> loadClass() {
      return SpawnType.class;
   }

   public JSONObject resolveJSONObject(@Nonnull SpawnType target) {
      JSONObject input = new JSONObject();
      input.put("value", target);
      return input;
   }

   public SpawnType resolveSpawnType(@Nonnull JSONObject target) {
      return (SpawnType)target.getEnum(SpawnType.class, "value");
   }
}
