package com.nickuc.login.api.event.velocity.command;

import com.nickuc.login.api.enums.event.CommandType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.velocity.VelocityCancellableEvent;
import com.velocitypowered.api.proxy.Player;
import java.util.Arrays;


public class PreCommandExecuteEvent
extends VelocityCancellableEvent
implements EventWithPlayer {
    private final Player player;
    private final CommandType type;
    private final String command;
    private final String[] extraArgs;

    public PreCommandExecuteEvent(Player player, CommandType type, String command, String[] extraArgs) {
        this.player = player;
        this.type = type;
        this.command = command;
        this.extraArgs = extraArgs;
    }

    public Player getPlayer() {
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

