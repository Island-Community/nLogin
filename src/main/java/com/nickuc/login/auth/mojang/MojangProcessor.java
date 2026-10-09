package com.nickuc.login.auth.mojang;

import com.nickuc.login.api.enums.AccountType;
import com.nickuc.login.api.types.AccountDataImpl;
import com.nickuc.login.premium.SpawnLookup;

public class MojangProcessor {
   public static AccountDataImpl from(SpawnLookup instance) {
      return new AccountDataImpl(
         instance.loadLong(),
         AccountType.convert(instance.loadSpawnOption()),
         instance.retrieveMessage(),
         instance.getUniqueId(),
         instance.getMojangId(),
         instance.getBedrockId(),
         instance.resolveMessage(),
         instance.findMessage(),
         instance.loadTime(),
         instance.findTime(),
         instance.fetchQuickDiscordHandler().loadMessage(),
         instance.fetchQuickDiscordHandler().getMessage(),
         instance.resolveCachedPasswordHashHasher().resolveTable()
      );
   }
}
