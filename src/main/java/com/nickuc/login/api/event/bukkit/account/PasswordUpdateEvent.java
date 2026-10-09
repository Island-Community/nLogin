package com.nickuc.login.api.event.bukkit.account;

import com.nickuc.login.api.enums.event.UpdatePasswordSource;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bukkit.BukkitEvent;
import java.util.UUID;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

public class PasswordUpdateEvent
extends BukkitEvent
implements EventWithPlayer {
    private final Player player;
    private final UUID playerId;
    private final String playerName;
    private final String newPassword;
    private final UpdatePasswordSource source;

    public PasswordUpdateEvent(Player player, UUID playerId, String playerName, String newPassword, UpdatePasswordSource source) {
        super(true);
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

