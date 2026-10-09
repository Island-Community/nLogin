package com.nickuc.login.api.event.velocity.command;

import com.nickuc.login.api.enums.event.ChangePasswordSource;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.velocity.VelocityCancellableEvent;
import com.velocitypowered.api.proxy.Player;
import java.util.UUID;
import javax.annotation.Nullable;


public class ChangePasswordEvent
extends VelocityCancellableEvent
implements EventWithPlayer {
    private final Player player;
    private final UUID playerId;
    private final String playerName;
    private final ChangePasswordSource source;

    public ChangePasswordEvent(Player player, UUID playerId, String playerName, ChangePasswordSource source) {
        this.player = player;
        this.playerId = playerId;
        this.playerName = playerName;
        this.source = source;
    }

    @Nullable
    public Player getPlayer() {
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

