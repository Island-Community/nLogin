package com.nickuc.login.api.event.internal.bukkit;

import com.nickuc.login.api.event.internal.CancellableEvent;
import com.nickuc.login.api.event.internal.bukkit.BukkitEvent;

import org.bukkit.event.Cancellable;

public class BukkitCancellableEvent
extends BukkitEvent
implements Cancellable,
CancellableEvent {
    private boolean cancelled;

    public BukkitCancellableEvent() {
    }

    public BukkitCancellableEvent(boolean async) {
        super(async);
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String toString() {
        return "BukkitCancellableEvent(cancelled=" + this.isCancelled() + ")";
    }
}

