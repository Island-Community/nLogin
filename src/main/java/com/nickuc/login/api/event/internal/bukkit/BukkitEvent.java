package com.nickuc.login.api.event.internal.bukkit;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class BukkitEvent
extends Event {
    private static final HandlerList handlerList = new HandlerList();

    public BukkitEvent() {
    }

    public BukkitEvent(boolean async) {
        super(async);
    }

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}

