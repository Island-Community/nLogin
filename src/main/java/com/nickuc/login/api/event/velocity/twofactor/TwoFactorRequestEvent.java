package com.nickuc.login.api.event.velocity.twofactor;

import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.velocitypowered.api.proxy.Player;
import javax.annotation.Nonnull;


public class TwoFactorRequestEvent
implements EventWithPlayer {
    private final TwoFactorType type;
    private final Player player;
    private final String account;

    public TwoFactorRequestEvent(TwoFactorType type, Player player, String account) {
        this.type = type;
        this.player = player;
        this.account = account;
    }

    @Nonnull
    public TwoFactorType getType() {
        return this.type;
    }

    @Nonnull
    public Player getPlayer() {
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

