package com.nickuc.login.api.event.bukkit.auth.request;

import com.nickuc.login.api.enums.event.LockableEventAction;
import com.nickuc.login.api.event.bukkit.auth.request.LoginRequestChangeEvent;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bukkit.BukkitLockableEvent;
import com.nickuc.login.api.nLoginAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class LoginRequestEvent
extends BukkitLockableEvent
implements EventWithPlayer {
    private final Player player;

    public LoginRequestEvent(Player player) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.player = player;
    }

    protected LoginRequestChangeEvent createNewActionEvent(Plugin owner, LockableEventAction action) {
        return new LoginRequestChangeEvent(this, owner, action);
    }

    @Override
    protected void internalCall(int id) {
        nLoginAPI.getApi().internal().lockableEvent(this, (byte)0, (byte)id);
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public String toString() {
        return "LoginRequestEvent(player=" + this.getPlayer() + ")";
    }
}

