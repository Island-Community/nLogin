package com.nickuc.login.api.event.internal.velocity;

import com.nickuc.login.api.enums.event.LockableEventAction;
import com.nickuc.login.api.event.internal.LockableEvent;
import com.nickuc.login.api.event.internal.velocity.VelocityCancellableEvent;
import com.nickuc.login.api.event.internal.velocity.VelocityLockableNewActionEvent;
import com.nickuc.login.api.nLoginAPI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;

public abstract class VelocityLockableEvent
extends VelocityCancellableEvent
implements LockableEvent {
    private final AtomicInteger lock = new AtomicInteger();
    private final AtomicBoolean finished = new AtomicBoolean();

    protected VelocityLockableEvent() {
    }

    protected abstract VelocityLockableNewActionEvent<?> createNewActionEvent(Object target, LockableEventAction secret);

    protected abstract void internalCall(int target);

    public void lock(@Nonnull Object owner) {
        if (this.checkConditions(owner)) {
            this.lock.incrementAndGet();
            nLoginAPI.getApi().internal().lockableNewAction(this.createNewActionEvent(owner, LockableEventAction.LOCK));
        }
    }

    public void unlock(@Nonnull Object owner) {
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

    protected void tryFinish() {
        if (this.lock.get() == 0 && !this.finished.getAndSet(true)) {
            this.internalCall(2);
        }
    }

    private boolean checkConditions(Object plugin) {
        return true;
    }
}

