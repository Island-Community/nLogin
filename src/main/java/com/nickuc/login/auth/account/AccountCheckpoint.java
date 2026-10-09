package com.nickuc.login.auth.account;

import com.nickuc.login.api.enums.AccountType;
import org.json.JSONObject;
import com.nickuc.login.platform.packet.DirectPacketAdapter;
import javax.annotation.Nonnull;

public class AccountCheckpoint implements DirectPacketAdapter<AccountType> {
   public static final AccountCheckpoint accountCheckpoint = new AccountCheckpoint();

   public JSONObject processJSONObject(@Nonnull AccountType target) {
      JSONObject input = new JSONObject();
      input.put("value", target);
      return input;
   }

   public AccountType resolveAccountType(@Nonnull JSONObject target) {
      return (AccountType)target.getEnum(AccountType.class, "value");
   }

   @Override
   public Class<?> loadClass() {
      return AccountType.class;
   }
}
