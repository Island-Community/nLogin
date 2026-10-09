package com.nickuc.login.api.event.internal.bungee;

import com.nickuc.login.api.enums.event.LockableEventAction;
import com.nickuc.login.api.event.internal.LockableNewActionEvent;
import com.nickuc.login.api.event.internal.bungee.BungeeEvent;
import javax.annotation.Nonnull;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLockableNewActionEvent<T>
extends BungeeEvent
implements LockableNewActionEvent<T> {
    private final T event;
    private final Plugin plugin;
    private final LockableEventAction action;

    public BungeeLockableNewActionEvent(T event, Plugin plugin, LockableEventAction action) {
        this.event = event;
        this.plugin = plugin;
        this.action = action;
    }

    @Override
    public T getEvent() {
        return this.event;
    }

    @Nonnull
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    @Nonnull
    public LockableEventAction getAction() {
        return this.action;
    }

    public String toString() {
        return "BungeeLockableNewActionEvent(event=" + this.getEvent() + ", plugin=" + this.getPlugin() + ", action=" + (Object)((Object)this.getAction()) + ")";
    }
}

