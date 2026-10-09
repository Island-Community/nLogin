package com.nickuc.login.api.event.bukkit.auth;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bukkit.BukkitEvent;

import org.bukkit.entity.Player;

public class BedrockLoginEvent
extends BukkitEvent
implements EventWithPlayer {
    private final Player player;

    public BedrockLoginEvent(Player player) {
        super(true);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String toString() {
        return "BedrockLoginEvent(player=" + this.getPlayer() + ")";
    }
}

