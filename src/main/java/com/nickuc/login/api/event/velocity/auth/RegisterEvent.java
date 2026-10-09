package com.nickuc.login.api.event.velocity.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.velocity.VelocityCancellableEvent;
import com.velocitypowered.api.proxy.Player;


public class RegisterEvent
extends VelocityCancellableEvent
implements EventWithPlayer {
    private final Player player;
    private final String password;

    public RegisterEvent(Player player, String password) {
        this.player = player;
        this.password = password;
    }

    public Player getPlayer() {
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

