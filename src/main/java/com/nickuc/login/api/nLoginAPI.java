package com.nickuc.login.api;

import com.nickuc.login.api.enums.AccountType;
import com.nickuc.login.api.enums.DatabaseType;
import com.nickuc.login.api.enums.ImplementationType;
import com.nickuc.login.api.enums.SpawnType;
import com.nickuc.login.api.event.internal.LockableEvent;
import com.nickuc.login.api.event.internal.LockableNewActionEvent;
import com.nickuc.login.api.nLoginAPIHolder;
import com.nickuc.login.api.types.AccountData;
import com.nickuc.login.api.types.Identity;
import com.nickuc.login.api.types.Location;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface nLoginAPI {
    @Nonnull
    public static nLoginAPI getApi() {
        return nLoginAPIHolder.getApi();
    }

    public boolean isAvailable();

    @Nonnull
    public String getVersion();

    public int getApiVersion();

    @Nonnull
    public ImplementationType getImplementationType();

    public DatabaseType getDatabaseType();

    public Optional<Location> getSpawnLocation(@Nonnull SpawnType target);

    public Optional<AccountData> getAccount(@Nonnull Identity target);

    @Nonnull
    public Iterator<AccountData> getAccounts();

    public long getAccountCount();

    @Nonnull
    public List<AccountData> getAccountsByIp(@Nonnull String target);

    public boolean isAuthenticated(@Nonnull Identity target);

    public boolean isAuthenticated(@Nonnull String target);

    public int getRemainingSeconds(@Nonnull Identity target);

    default public int getRemainingSeconds(@Nonnull String playerName) {
        return this.getRemainingSeconds(Identity.ofKnownName(playerName));
    }

    @Nullable
    default public String getRealName(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(AccountData::getLastName).orElse(null);
    }

    @Nullable
    default public String getHashedPassword(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).flatMap(AccountData::getHashedPassword).orElse(null);
    }

    @Nullable
    default public String getAddress(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(AccountData::getLastIP).orElse(null);
    }

    @Nonnull
    default public Instant getLastLogin(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(AccountData::getLastLogin).orElse(Instant.ofEpochMilli(0L));
    }

    default public long getLastLoginUnix(@Nonnull String playerName) {
        return this.getLastLogin(playerName).toEpochMilli();
    }

    @Nonnull
    default public Instant getRegisterDate(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(AccountData::getCreationDate).orElse(Instant.ofEpochMilli(0L));
    }

    default public long getRegisterDateUnix(@Nonnull String playerName) {
        return this.getRegisterDate(playerName).toEpochMilli();
    }

    @Nullable
    default public String getEmail(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).flatMap(AccountData::getEmail).orElse(null);
    }

    @Nullable
    default public String getDiscord(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).flatMap(AccountData::getDiscordId).orElse(null);
    }

    default public boolean hasEmail(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(account -> account.getEmail().isPresent()).orElse(false);
    }

    default public boolean hasDiscord(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(account -> account.getDiscordId().isPresent()).orElse(false);
    }

    @Nonnull
    default public String getLanguage(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(account -> account.getLanguage().orElse("other")).orElse("other");
    }

    public boolean comparePassword(AccountData target, String secret);

    default public boolean comparePassword(String playerName, String plainPassword) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(account -> account.comparePassword(plainPassword)).orElse(false);
    }

    default public boolean isRegistered(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).isPresent();
    }

    default public boolean isPremium(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(AccountData::getType).orElse(null) == AccountType.PREMIUM;
    }

    default public boolean isBedrock(@Nonnull String playerName) {
        return this.getAccount(Identity.ofKnownName(playerName)).map(AccountData::getType).orElse(null) == AccountType.BEDROCK;
    }

    public void requestLogin(@Nonnull Identity target, @Nonnull Object secret);

    default public void requestLogin(@Nonnull String playerName, @Nonnull Object plugin) {
        this.requestLogin(Identity.ofKnownName(playerName), plugin);
    }

    default public boolean performRegister(@Nonnull Identity identity, @Nonnull String plainPassword) {
        return this.performRegister(identity, plainPassword, null);
    }

    public boolean performRegister(@Nonnull Identity target, @Nonnull String secret, @Nullable String address);

    default public boolean performRegister(@Nonnull String playerName, @Nonnull String plainPassword) {
        return this.performRegister(Identity.ofKnownName(playerName), plainPassword, null);
    }

    default public boolean performRegister(@Nonnull String playerName, @Nonnull String plainPassword, @Nullable String ip) {
        return this.performRegister(Identity.ofKnownName(playerName), plainPassword, ip);
    }

    public boolean performUnregister(@Nonnull Identity target);

    default public boolean performUnregister(@Nonnull String playerName) {
        return this.performUnregister(Identity.ofKnownName(playerName));
    }

    public boolean changePassword(@Nonnull Identity target, @Nonnull String secret);

    default public boolean changePassword(@Nonnull String playerName, @Nonnull String newPassword) {
        return this.changePassword(Identity.ofKnownName(playerName), newPassword);
    }

    public boolean setEmail(@Nonnull Identity target, @Nullable String secret);

    default public boolean setEmail(@Nonnull String playerName, @Nullable String newEmail) {
        return this.setEmail(Identity.ofKnownName(playerName), newEmail);
    }

    public boolean setDiscord(@Nonnull Identity target, long secret);

    default public boolean setDiscord(@Nonnull String playerName, long newAccountId) {
        return this.setDiscord(Identity.ofKnownName(playerName), newAccountId);
    }

    public boolean setLanguage(@Nonnull Identity target, @Nullable String secret);

    default public boolean forceLogin(@Nonnull Identity identity) {
        return this.forceLogin(identity, true);
    }

    public boolean forceLogin(@Nonnull Identity target, boolean secret);

    default public boolean forceLogin(@Nonnull String playerName) {
        return this.forceLogin(Identity.ofKnownName(playerName), true);
    }

    default public boolean forceLogin(@Nonnull String playerName, boolean showMessages) {
        return this.forceLogin(Identity.ofKnownName(playerName), showMessages);
    }

    @Nonnull
    public nLoginInternal internal();

    public static interface nLoginInternal {
        public void lockableNewAction(LockableNewActionEvent<?> target);

        public void lockableEvent(LockableEvent target, byte secret, byte address);

        public Identity createIdentity(String target, UUID secret, UUID address, AccountType accountType);

        public Identity createIdentityFromKnownName(String target);
    }
}

