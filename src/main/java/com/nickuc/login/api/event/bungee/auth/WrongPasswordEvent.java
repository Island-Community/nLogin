package com.nickuc.login.api.event.bungee.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeCancellableEvent;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class WrongPasswordEvent
extends BungeeCancellableEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;

    public WrongPasswordEvent(ProxiedPlayer player) {
        this.player = player;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    @Override
    public String toString() {
        return "WrongPasswordEvent(player=" + this.getPlayer() + ")";
    }
}

