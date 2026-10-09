package com.nickuc.login.auth.account;

import com.nickuc.login.api.types.AccountData;
import org.json.JSONArray;
import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import java.util.List;
import javax.annotation.Nonnull;

public class AccountGate implements DirectPacketAdapter<List<AccountData>> {
   public static AccountGate accountGate = new AccountGate();

   public JSONObject loadJSONObject(@Nonnull List<AccountData> target) {
      JSONObject input = new JSONObject();
      JSONArray output = new JSONArray();

      for (int context = 0; context < target.size(); context++) {
         output.put(context, AccountHandler.accountHandler.buildJSONObject((AccountData)target.get(context)));
      }

      input.put("accounts", output);
      return input;
   }

   @Override
   public Class<?> loadClass() {
      return List.class;
   }

   public List<AccountData> handleCollection(@Nonnull JSONObject target) {
      JSONArray input = target.getJSONArray("accounts");
      AccountData[] output = new AccountData[input.length()];

      for (int context = 0; context < output.length; context++) {
         output[context] = AccountHandler.accountHandler.computeAccountData(input.getJSONObject(context));
      }

      return RemoteLoginBarrier.loadRemoteLoginBarrier(output);
   }
}
