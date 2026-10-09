package com.nickuc.login.api.event.bukkit.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bukkit.BukkitCancellableEvent;

import org.bukkit.entity.Player;

public class RegisterEvent
extends BukkitCancellableEvent
implements EventWithPlayer {
    private final Player player;
    private final String password;

    public RegisterEvent(Player player, String password) {
        super(true);
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

