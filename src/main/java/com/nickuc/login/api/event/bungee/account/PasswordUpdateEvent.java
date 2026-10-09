package com.nickuc.login.api.event.bungee.account;

import com.nickuc.login.api.enums.event.UpdatePasswordSource;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeEvent;
import java.util.UUID;
import javax.annotation.Nullable;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PasswordUpdateEvent
extends BungeeEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;
    private final UUID playerId;
    private final String playerName;
    private final String newPassword;
    private final UpdatePasswordSource source;

    public PasswordUpdateEvent(ProxiedPlayer player, UUID playerId, String playerName, String newPassword, UpdatePasswordSource source) {
        this.player = player;
        this.playerId = playerId;
        this.playerName = playerName;
        this.newPassword = newPassword;
        this.source = source;
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

    @Nullable
    public String getNewPassword() {
        return this.newPassword;
    }

    public UpdatePasswordSource getSource() {
        return this.source;
    }

    public String toString() {
        return "PasswordUpdateEvent(player=" + this.getPlayer() + ", playerId=" + this.getPlayerId() + ", playerName=" + this.getPlayerName() + ", newPassword=" + this.getNewPassword() + ", source=" + (Object)((Object)this.getSource()) + ")";
    }
}

