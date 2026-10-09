package com.nickuc.login.api.event.internal;

import com.nickuc.login.api.enums.event.LockableEventAction;

public interface LockableNewActionEvent<T> {
    public T getEvent();

    public LockableEventAction getAction();
}

