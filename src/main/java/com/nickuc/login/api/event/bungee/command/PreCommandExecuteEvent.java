package com.nickuc.login.api.event.bungee.command;

import com.nickuc.login.api.enums.event.CommandType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeCancellableEvent;
import java.util.Arrays;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PreCommandExecuteEvent
extends BungeeCancellableEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;
    private final CommandType type;
    private final String command;
    private final String[] extraArgs;

    public PreCommandExecuteEvent(ProxiedPlayer player, CommandType type, String command, String[] extraArgs) {
        this.player = player;
        this.type = type;
        this.command = command;
        this.extraArgs = extraArgs;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public CommandType getType() {
        return this.type;
    }

    public String getCommand() {
        return this.command;
    }

    public String[] getExtraArgs() {
        return this.extraArgs;
    }

    @Override
    public String toString() {
        return "PreCommandExecuteEvent(player=" + this.getPlayer() + ", type=" + (Object)((Object)this.getType()) + ", command=" + this.getCommand() + ", extraArgs=" + Arrays.deepToString(this.getExtraArgs()) + ")";
    }
}

