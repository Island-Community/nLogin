package com.nickuc.login.api.event.internal.bungee;

import com.nickuc.login.api.event.internal.CancellableEvent;
import com.nickuc.login.api.event.internal.bungee.BungeeEvent;

import net.md_5.bungee.api.plugin.Cancellable;

public class BungeeCancellableEvent
extends BungeeEvent
implements Cancellable,
CancellableEvent {
    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String toString() {
        return "BungeeCancellableEvent(cancelled=" + this.isCancelled() + ")";
    }
}

