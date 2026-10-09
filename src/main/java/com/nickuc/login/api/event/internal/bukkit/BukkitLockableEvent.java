package com.nickuc.login.api.event.internal.bukkit;

import com.nickuc.login.api.enums.event.LockableEventAction;
import com.nickuc.login.api.event.internal.LockableEvent;
import com.nickuc.login.api.event.internal.bukkit.BukkitCancellableEvent;
import com.nickuc.login.api.event.internal.bukkit.BukkitLockableNewActionEvent;
import com.nickuc.login.api.nLoginAPI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import org.bukkit.plugin.Plugin;

public abstract class BukkitLockableEvent
extends BukkitCancellableEvent
implements LockableEvent {
    private final AtomicInteger lock = new AtomicInteger();
    private final AtomicBoolean finished = new AtomicBoolean();

    protected BukkitLockableEvent() {
    }

    protected BukkitLockableEvent(boolean async) {
        super(async);
    }

    protected abstract BukkitLockableNewActionEvent<?> createNewActionEvent(Plugin target, LockableEventAction secret);

    protected abstract void internalCall(int target);

    public void lock(@Nonnull Plugin owner) {
        if (this.checkConditions(owner)) {
            this.lock.incrementAndGet();
            nLoginAPI.getApi().internal().lockableNewAction(this.createNewActionEvent(owner, LockableEventAction.LOCK));
        }
    }

    public void unlock(@Nonnull Plugin owner) {
        if (this.lock.get() > 0 && this.checkConditions(owner)) {
            this.lock.decrementAndGet();
            nLoginAPI.getApi().internal().lockableNewAction(this.createNewActionEvent(owner, LockableEventAction.UNLOCK));
            this.tryFinish();
        }
    }

    public int count() {
        return this.lock.get();
    }

    @Deprecated
    public byte getLock() {
        return (byte)this.count();
    }

    @Override
    public boolean callEvt() {
        this.internalCall(1);
        this.tryFinish();
        return !this.isCancelled();
    }

    private void tryFinish() {
        if (this.lock.get() == 0 && !this.finished.getAndSet(true)) {
            this.internalCall(2);
        }
    }

    private boolean checkConditions(Plugin plugin) {
        return plugin.isEnabled() && !"nLogin".equalsIgnoreCase(plugin.getName());
    }
}

