package com.nickuc.login.api.event.bungee.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeEvent;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AuthenticateEvent
extends BungeeEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;

    public AuthenticateEvent(ProxiedPlayer player) {
        this.player = player;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public String toString() {
        return "AuthenticateEvent(player=" + this.getPlayer() + ")";
    }
}

