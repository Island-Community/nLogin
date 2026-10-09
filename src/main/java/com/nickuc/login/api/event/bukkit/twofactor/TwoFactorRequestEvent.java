package com.nickuc.login.api.event.bukkit.twofactor;

import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bukkit.BukkitEvent;
import javax.annotation.Nonnull;

import org.bukkit.entity.Player;

public class TwoFactorRequestEvent
extends BukkitEvent
implements EventWithPlayer {
    private final TwoFactorType type;
    private final Player player;
    private final String account;

    public TwoFactorRequestEvent(TwoFactorType type, Player player, String account) {
        super(true);
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

