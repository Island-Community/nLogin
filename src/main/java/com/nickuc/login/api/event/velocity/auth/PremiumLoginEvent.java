package com.nickuc.login.api.event.velocity.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.velocitypowered.api.proxy.Player;


public class PremiumLoginEvent
implements EventWithPlayer {
    private final Player player;

    public PremiumLoginEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String toString() {
        return "PremiumLoginEvent(player=" + this.getPlayer() + ")";
    }
}

