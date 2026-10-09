package com.nickuc.login.api.event.bungee.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeCancellableEvent;
import javax.annotation.Nullable;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LoginEvent
extends BungeeCancellableEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;
    @Nullable
    private final String password;

    public LoginEvent(ProxiedPlayer player, @Nullable String password) {
        this.player = player;
        this.password = password;
    }

    public ProxiedPlayer getPlayer() {
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

