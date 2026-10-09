package com.nickuc.login.api.event.internal.bukkit;

import com.nickuc.login.api.enums.event.LockableEventAction;
import com.nickuc.login.api.event.internal.LockableNewActionEvent;
import com.nickuc.login.api.event.internal.bukkit.BukkitEvent;
import javax.annotation.Nonnull;

import org.bukkit.plugin.Plugin;

public class BukkitLockableNewActionEvent<T>
extends BukkitEvent
implements LockableNewActionEvent<T> {
    private final T event;
    private final Plugin plugin;
    private final LockableEventAction action;

    public BukkitLockableNewActionEvent(T event, Plugin plugin, LockableEventAction action) {
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
        return "BukkitLockableNewActionEvent(event=" + this.getEvent() + ", plugin=" + this.getPlugin() + ", action=" + (Object)((Object)this.getAction()) + ")";
    }
}

