package com.nickuc.login.api.types;

import com.nickuc.login.api.enums.AccountType;
import com.nickuc.login.api.nLoginAPI;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

public interface AccountData {
    public Optional<Long> getId();

    @Nonnull
    public AccountType getType();

    @Nonnull
    public String getLastName();

    public Optional<UUID> getUniqueId();

    public Optional<UUID> getMojangId();

    public Optional<UUID> getBedrockId();

    public Optional<String> getHashedPassword();

    default public boolean comparePassword(String plainPassword) {
        return nLoginAPI.getApi().comparePassword(this, plainPassword);
    }

    @Nonnull
    default public String getLastAddress() {
        return this.getLastIP();
    }

    @Nonnull
    public String getLastIP();

    @Nonnull
    public Instant getLastLogin();

    @Nonnull
    public Instant getCreationDate();

    public Optional<String> getEmail();

    public Optional<String> getDiscordId();

    @Nonnull
    public Map<String, Object> getSettings();

    default public Optional<String> getLanguage() {
        Object value = this.getSettings().get("language");
        return Optional.ofNullable(value instanceof String ? (String)value : null);
    }
}

