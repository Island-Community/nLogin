package com.nickuc.login.api.event.bungee.auth.request;

import com.nickuc.login.api.enums.event.LockableEventAction;
import com.nickuc.login.api.event.bungee.auth.request.LoginRequestChangeEvent;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeLockableEvent;
import com.nickuc.login.api.nLoginAPI;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class LoginRequestEvent
extends BungeeLockableEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;

    public LoginRequestEvent(ProxiedPlayer player) {
        this.player = player;
    }

    protected LoginRequestChangeEvent createNewActionEvent(Plugin owner, LockableEventAction action) {
        return new LoginRequestChangeEvent(this, owner, action);
    }

    @Override
    protected void internalCall(int id) {
        nLoginAPI.getApi().internal().lockableEvent(this, (byte)0, (byte)id);
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    @Override
    public String toString() {
        return "LoginRequestEvent(player=" + this.getPlayer() + ")";
    }
}

