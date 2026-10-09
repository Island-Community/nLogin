package com.nickuc.login.api.event.bungee.command;

import com.nickuc.login.api.enums.event.UnregisterSource;
import com.nickuc.login.api.event.internal.bungee.BungeeCancellableEvent;
import java.util.UUID;
import javax.annotation.Nullable;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UnregisterEvent
extends BungeeCancellableEvent {
    private final ProxiedPlayer player;
    private final UUID playerId;
    private final String playerName;
    private final UnregisterSource source;

    public UnregisterEvent(ProxiedPlayer player, UUID playerId, String playerName, UnregisterSource source) {
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

    public UnregisterSource getSource() {
        return this.source;
    }

    @Override
    public String toString() {
        return "UnregisterEvent(player=" + this.getPlayer() + ", playerId=" + this.getPlayerId() + ", playerName=" + this.getPlayerName() + ", source=" + (Object)((Object)this.getSource()) + ")";
    }
}

