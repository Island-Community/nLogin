package com.nickuc.login.api.types;

import com.nickuc.login.api.enums.AccountType;
import com.nickuc.login.api.nLoginAPI;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface Identity {
    @Nonnull
    public static Identity ofKnownName(@Nonnull String knownName) {
        return nLoginAPI.getApi().internal().createIdentityFromKnownName(knownName);
    }

    public static Identity ofOffline(@Nonnull String name) {
        return nLoginAPI.getApi().internal().createIdentity(name, null, null, AccountType.OFFLINE);
    }

    public static Identity ofPremium(@Nonnull String name, @Nonnull UUID mojangId) {
        return nLoginAPI.getApi().internal().createIdentity(name, mojangId, null, AccountType.PREMIUM);
    }

    public static Identity ofBedrock(@Nonnull String name, @Nonnull UUID bedrockId) {
        return nLoginAPI.getApi().internal().createIdentity(name, null, bedrockId, AccountType.BEDROCK);
    }
}

