package com.nickuc.login.api.event.velocity.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.velocitypowered.api.proxy.Player;


public class SessionLoginEvent
implements EventWithPlayer {
    private final Player player;

    public SessionLoginEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String toString() {
        return "SessionLoginEvent(player=" + this.getPlayer() + ")";
    }
}

