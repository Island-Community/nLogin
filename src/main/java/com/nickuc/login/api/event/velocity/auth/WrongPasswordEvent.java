package com.nickuc.login.api.event.velocity.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.velocity.VelocityCancellableEvent;
import com.velocitypowered.api.proxy.Player;


public class WrongPasswordEvent
extends VelocityCancellableEvent
implements EventWithPlayer {
    private final Player player;

    public WrongPasswordEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public String toString() {
        return "WrongPasswordEvent(player=" + this.getPlayer() + ")";
    }
}

