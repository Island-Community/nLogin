package com.nickuc.login.api.event.bungee.twofactor;

import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeEvent;
import java.util.UUID;
import javax.annotation.Nullable;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TwoFactorAddEvent
extends BungeeEvent
implements EventWithPlayer {
    private final TwoFactorType type;
    private final ProxiedPlayer player;
    private final UUID playerId;
    private final String playerName;
    private final String account;

    public TwoFactorAddEvent(TwoFactorType type, ProxiedPlayer player, UUID playerId, String playerName, String account) {
        this.type = type;
        this.player = player;
        this.playerId = playerId;
        this.playerName = playerName;
        this.account = account;
    }

    public TwoFactorType getType() {
        return this.type;
    }

    @Nullable
    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getAccount() {
        return this.account;
    }

    public String toString() {
        return "TwoFactorAddEvent(type=" + (Object)((Object)this.getType()) + ", player=" + this.getPlayer() + ", playerId=" + this.getPlayerId() + ", playerName=" + this.getPlayerName() + ", account=" + this.getAccount() + ")";
    }
}

