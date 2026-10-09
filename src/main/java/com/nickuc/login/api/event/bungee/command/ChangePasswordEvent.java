package com.nickuc.login.api.event.bungee.command;

import com.nickuc.login.api.enums.event.ChangePasswordSource;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeCancellableEvent;
import java.util.UUID;
import javax.annotation.Nullable;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChangePasswordEvent
extends BungeeCancellableEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;
    private final UUID playerId;
    private final String playerName;
    private final ChangePasswordSource source;

    public ChangePasswordEvent(ProxiedPlayer player, UUID playerId, String playerName, ChangePasswordSource source) {
        this.player = player;
        this.playerId = playerId;
        this.playerName = playerName;
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

    public ChangePasswordSource getSource() {
        return this.source;
    }

    @Override
    public String toString() {
        return "ChangePasswordEvent(player=" + this.getPlayer() + ", playerId=" + this.getPlayerId() + ", playerName=" + this.getPlayerName() + ", source=" + (Object)((Object)this.getSource()) + ")";
    }
}

