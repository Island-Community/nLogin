package com.nickuc.login.api.event.velocity.account;

import com.nickuc.login.api.enums.event.UpdatePasswordSource;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.velocitypowered.api.proxy.Player;
import java.util.UUID;
import javax.annotation.Nullable;


public class PasswordUpdateEvent
implements EventWithPlayer {
    private final Player player;
    private final UUID playerId;
    private final String playerName;
    private final String newPassword;
    private final UpdatePasswordSource source;

    public PasswordUpdateEvent(Player player, UUID playerId, String playerName, String newPassword, UpdatePasswordSource source) {
        this.player = player;
        this.playerId = playerId;
        this.playerName = playerName;
        this.newPassword = newPassword;
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

