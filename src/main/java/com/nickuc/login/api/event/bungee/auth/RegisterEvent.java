package com.nickuc.login.api.event.bungee.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeCancellableEvent;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RegisterEvent
extends BungeeCancellableEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;
    private final String password;

    public RegisterEvent(ProxiedPlayer player, String password) {
        this.player = player;
        this.password = password;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String toString() {
        return "RegisterEvent(player=" + this.getPlayer() + ", password=" + this.getPassword() + ")";
    }
}

