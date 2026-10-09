package com.nickuc.login.api.event.bungee.connection;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeEvent;
import java.util.Optional;
import javax.annotation.Nullable;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class DefineAuthServerEvent
extends BungeeEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;
    private final ServerInfo originalServer;
    private ServerInfo server;

    public DefineAuthServerEvent(ProxiedPlayer player, @Nullable ServerInfo originalServer) {
        this.player = player;
        this.originalServer = originalServer;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public Optional<ServerInfo> getOriginalServer() {
        return Optional.ofNullable(this.originalServer);
    }

    public Optional<ServerInfo> getServer() {
        return Optional.ofNullable(this.server != null ? this.server : this.originalServer);
    }

    public void setServer(ServerInfo server) {
        if (server == null) {
            throw new IllegalArgumentException("The server cannot be null!");
        }
        this.server = server;
    }

    public String toString() {
        return "DefineAuthServerEvent(player=" + this.getPlayer() + ", originalServer=" + this.getOriginalServer() + ", server=" + this.getServer() + ")";
    }
}

