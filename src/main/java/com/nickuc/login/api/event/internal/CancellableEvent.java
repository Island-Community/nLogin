package com.nickuc.login.api.event.internal;

public interface CancellableEvent {
    public boolean isCancelled();

    public void setCancelled(boolean target);
}

