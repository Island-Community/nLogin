package com.nickuc.login.api.event.bukkit.twofactor;

import com.nickuc.login.api.enums.TwoFactorType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bukkit.BukkitEvent;

import org.bukkit.entity.Player;

public class TwoFactorAuthEvent
extends BukkitEvent
implements EventWithPlayer {
    private final TwoFactorType type;
    private final Player player;
    private final String account;

    public TwoFactorAuthEvent(TwoFactorType type, Player player, String account) {
        super(true);
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
        return "TwoFactorAuthEvent(type=" + (Object)((Object)this.getType()) + ", player=" + this.getPlayer() + ", account=" + this.getAccount() + ")";
    }
}

