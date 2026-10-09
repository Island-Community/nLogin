package com.nickuc.login.api.event.internal.velocity;

import com.nickuc.login.api.enums.event.LockableEventAction;
import com.nickuc.login.api.event.internal.LockableNewActionEvent;
import javax.annotation.Nonnull;


public class VelocityLockableNewActionEvent<T>
implements LockableNewActionEvent<T> {
    private final T event;
    private final Object plugin;
    private final LockableEventAction action;

    public VelocityLockableNewActionEvent(T event, Object plugin, LockableEventAction action) {
        this.event = event;
        this.plugin = plugin;
        this.action = action;
    }

    @Override
    public T getEvent() {
        return this.event;
    }

    @Nonnull
    public Object getPlugin() {
        return this.plugin;
    }

    @Override
    @Nonnull
    public LockableEventAction getAction() {
        return this.action;
    }

    public String toString() {
        return "VelocityLockableNewActionEvent(event=" + this.getEvent() + ", plugin=" + this.getPlugin() + ", action=" + (Object)((Object)this.getAction()) + ")";
    }
}

