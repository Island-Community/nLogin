package com.nickuc.login.api.event.bungee.twofactor;

import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeEvent;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TwoFactorRemoveEvent
extends BungeeEvent
implements EventWithPlayer {
    private final TwoFactorType type;
    private final ProxiedPlayer player;
    private final String account;

    public TwoFactorRemoveEvent(TwoFactorType type, ProxiedPlayer player, String account) {
        this.type = type;
        this.player = player;
        this.account = account;
    }

    public TwoFactorType getType() {
        return this.type;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public String getAccount() {
        return this.account;
    }

    public String toString() {
        return "TwoFactorRemoveEvent(type=" + (Object)((Object)this.getType()) + ", player=" + this.getPlayer() + ", account=" + this.getAccount() + ")";
    }
}

