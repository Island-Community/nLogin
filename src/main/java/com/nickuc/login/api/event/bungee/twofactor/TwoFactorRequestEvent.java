package com.nickuc.login.api.event.bungee.twofactor;

import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeEvent;
import javax.annotation.Nonnull;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TwoFactorRequestEvent
extends BungeeEvent
implements EventWithPlayer {
    private final TwoFactorType type;
    private final ProxiedPlayer player;
    private final String account;

    public TwoFactorRequestEvent(TwoFactorType type, ProxiedPlayer player, String account) {
        this.type = type;
        this.player = player;
        this.account = account;
    }

    @Nonnull
    public TwoFactorType getType() {
        return this.type;
    }

    @Nonnull
    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    @Nonnull
    public String getAccount() {
        return this.account;
    }

    public String toString() {
        return "TwoFactorRequestEvent(type=" + (Object)((Object)this.getType()) + ", player=" + this.getPlayer() + ", account=" + this.getAccount() + ")";
    }
}

