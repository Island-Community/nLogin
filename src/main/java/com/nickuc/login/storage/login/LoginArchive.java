package com.nickuc.login.storage.login;

import com.nickuc.login.api.enums.DatabaseType;
import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import javax.annotation.Nonnull;

public class LoginArchive implements DirectPacketAdapter<DatabaseType> {
   public static final LoginArchive loginArchive = new LoginArchive();

   public DatabaseType buildDatabaseType(@Nonnull JSONObject target) {
      return (DatabaseType)target.getEnum(DatabaseType.class, "value");
   }

   public JSONObject createJSONObject(@Nonnull DatabaseType target) {
      JSONObject input = new JSONObject();
      input.put("value", target);
      return input;
   }

   @Override
   public Class<?> loadClass() {
      return DatabaseType.class;
   }
}
