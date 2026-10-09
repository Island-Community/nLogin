package com.nickuc.login.auth.mojang;

import com.nickuc.login.api.types.Identity;
import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import java.util.UUID;
import javax.annotation.Nonnull;

public class MojangBarrier implements DirectPacketAdapter<Identity> {
   public static final MojangBarrier mojangBarrier = new MojangBarrier();

   public JSONObject buildJSONObject(@Nonnull Identity target) {
      JSONObject input = new JSONObject();
      if (target instanceof MojangService) {
         MojangService output = (MojangService)target;
         input.put("type", 0);
         input.put("name", output.getName());
         UUID context = output.getMojangId();
         if (context != null) {
            input.put("mojangId", DeadLoginFlow.processMessage(context));
         }

         UUID data = output.getBedrockId();
         if (data != null) {
            input.put("bedrockId", DeadLoginFlow.processMessage(data));
         }
      } else {
         if (!(target instanceof RemoteLoginProcessor)) {
            throw new IllegalArgumentException("The provided value is not a supported class! " + target.getClass().getCanonicalName());
         }

         RemoteLoginProcessor value = (RemoteLoginProcessor)target;
         input.put("type", 1);
         input.put("knownName", value.getKnownName());
      }

      return input;
   }

   @Override
   public Class<?> loadClass() {
      return Identity.class;
   }

   public Identity processIdentity(@Nonnull JSONObject target) {
      int input = target.getInt("type");
      switch (input) {
         case 0:
            String value = target.getString("name");
            UUID context = target.has("mojangId") ? DeadLoginFlow.buildUniqueId(target.getString("mojangId")) : null;
            UUID data = target.has("bedrockId") ? DeadLoginFlow.buildUniqueId(target.getString("bedrockId")) : null;
            return new MojangService(value, context, data);
         case 1:
            String output = target.getString("knownName");
            return new RemoteLoginProcessor(output);
         default:
            throw new IllegalArgumentException("The provided type is not supported! " + input);
      }
   }
}
