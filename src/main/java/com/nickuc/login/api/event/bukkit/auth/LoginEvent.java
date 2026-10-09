package com.nickuc.login.api.event.bukkit.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bukkit.BukkitCancellableEvent;
import javax.annotation.Nullable;

import org.bukkit.entity.Player;

public class LoginEvent
extends BukkitCancellableEvent
implements EventWithPlayer {
    private final Player player;
    @Nullable
    private final String password;

    public LoginEvent(Player player, @Nullable String password) {
        super(true);
        this.player = player;
        this.password = password;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Nullable
    public String getPassword() {
        return this.password;
    }

    @Override
    public String toString() {
        return "LoginEvent(player=" + this.getPlayer() + ", password=" + this.getPassword() + ")";
    }
}

