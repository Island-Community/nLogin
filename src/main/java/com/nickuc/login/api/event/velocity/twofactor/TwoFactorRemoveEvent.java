package com.nickuc.login.api.event.velocity.twofactor;

import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.velocitypowered.api.proxy.Player;


public class TwoFactorRemoveEvent
implements EventWithPlayer {
    private final TwoFactorType type;
    private final Player player;
    private final String account;

    public TwoFactorRemoveEvent(TwoFactorType type, Player player, String account) {
        this.type = type;
        this.player = player;
        this.account = account;
    }

    public TwoFactorType getType() {
        return this.type;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getAccount() {
        return this.account;
    }

    public String toString() {
        return "TwoFactorRemoveEvent(type=" + (Object)((Object)this.getType()) + ", player=" + this.getPlayer() + ", account=" + this.getAccount() + ")";
    }
}

