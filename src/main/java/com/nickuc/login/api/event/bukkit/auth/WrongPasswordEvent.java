package com.nickuc.login.api.event.bukkit.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bukkit.BukkitCancellableEvent;

import org.bukkit.entity.Player;

public class WrongPasswordEvent
extends BukkitCancellableEvent
implements EventWithPlayer {
    private final Player player;

    public WrongPasswordEvent(Player player) {
        super(true);
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

