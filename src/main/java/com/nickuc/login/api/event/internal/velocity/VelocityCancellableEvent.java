package com.nickuc.login.api.event.internal.velocity;

import com.nickuc.login.api.event.internal.CancellableEvent;


public class VelocityCancellableEvent
implements CancellableEvent {
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
        return "VelocityCancellableEvent(cancelled=" + this.isCancelled() + ")";
    }
}

