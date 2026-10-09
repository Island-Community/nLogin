package com.nickuc.login.auth.account;

import com.nickuc.login.api.enums.AccountType;
import com.nickuc.login.api.types.AccountData;
import com.nickuc.login.api.types.AccountDataImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class AccountHandler implements DirectPacketAdapter<AccountData> {
   private final Constructor<?> constructor;
   public static AccountHandler accountHandler = new AccountHandler();
   private final Field[] values = AccountDataImpl.class.getDeclaredFields();

   @Override
   public Class<?> loadClass() {
      return AccountData.class;
   }

   public AccountHandler() {
      for (Field context : this.values) {
         context.setAccessible(true);
      }

      try {
         Class[] value = Arrays.stream(this.values).map(Field::getType).toArray(Class[]::new);
         this.constructor = AccountDataImpl.class.getConstructor(value);
      } catch (NoSuchMethodException data) {
         throw new RuntimeException("Unable to find constructor for " + AccountDataImpl.class.getCanonicalName() + " class!", data);
      }
   }

   public JSONObject buildJSONObject(@Nonnull AccountData target) {
      if (!(target instanceof AccountDataImpl)) {
         throw new IllegalArgumentException(
            "The provided value is not an instance of " + AccountDataImpl.class.getCanonicalName() + " class!" + target.getClass().getCanonicalName()
         );
      }

      JSONObject input = new JSONObject();
      JSONArray output = new JSONArray();

      try {
         for (int context = 0; context < this.values.length; context++) {
            JSONObject data = new JSONObject();
            output.put(context, data);
            Object value = this.values[context].get(target);
            if (value != null) {
               byte result;
               if (value instanceof String) {
                  result = 0;
               } else if (value instanceof UUID) {
                  result = 1;
               } else if (value instanceof Boolean) {
                  result = 2;
               } else if (value instanceof Long) {
                  result = 3;
               } else if (value instanceof Map) {
                  result = 4;
               } else {
                  if (!(value instanceof AccountType)) {
                     throw new IllegalArgumentException("Unsupported value type! " + value.getClass().getCanonicalName() + " " + value);
                  }

                  result = 5;
               }

               if (result == 1) {
                  data.put("value", value.toString());
               } else {
                  data.put("value", value);
               }

               data.put("type", result);
            }
         }
      } catch (ReflectiveOperationException request) {
         throw new RuntimeException("Unable to encode " + AccountData.class.getCanonicalName() + " class!", request);
      }

      input.put("fields", output);
      return input;
   }

   public AccountData computeAccountData(@Nonnull JSONObject target) {
      JSONArray input = target.getJSONArray("fields");
      Object[] output = new Object[input.length()];

      for (int context = 0; context < output.length; context++) {
         JSONObject data = input.getJSONObject(context);
         if (data.has("value")) {
            int value = data.getInt("type");
            switch (value) {
               case 0:
               case 2:
                  output[context] = data.get("value");
                  break;
               case 1:
                  output[context] = UUID.fromString((String)data.get("value"));
                  break;
               case 3:
                  output[context] = data.getLong("value");
                  break;
               case 4:
                  output[context] = data.getJSONObject("value").toMap();
                  break;
               case 5:
                  output[context] = data.getEnum(AccountType.class, "value");
            }
         }
      }

      try {
         return (AccountData)this.constructor.newInstance(output);
      } catch (ReflectiveOperationException result) {
         throw new RuntimeException("Unable to decode " + AccountData.class.getCanonicalName() + " class!", result);
      }
   }
}
