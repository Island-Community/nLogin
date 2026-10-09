package com.nickuc.login.api.event.velocity.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.velocity.VelocityCancellableEvent;
import com.velocitypowered.api.proxy.Player;
import javax.annotation.Nullable;


public class LoginEvent
extends VelocityCancellableEvent
implements EventWithPlayer {
    private final Player player;
    @Nullable
    private final String password;

    public LoginEvent(Player player, @Nullable String password) {
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

