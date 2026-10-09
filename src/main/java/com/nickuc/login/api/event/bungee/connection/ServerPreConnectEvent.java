package com.nickuc.login.api.event.bungee.connection;

import com.nickuc.login.api.enums.ServerConnectType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.bungee.BungeeCancellableEvent;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ServerPreConnectEvent
extends BungeeCancellableEvent
implements EventWithPlayer {
    private final ProxiedPlayer player;
    private final ServerConnectType serverConnectType;
    private final ServerInfo originalServer;
    private ServerInfo server;

    public ServerPreConnectEvent(ProxiedPlayer player, ServerConnectType serverConnectType, ServerInfo originalServer) {
        this.player = player;
        this.serverConnectType = serverConnectType;
        this.originalServer = originalServer;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public ServerConnectType getServerConnectType() {
        return this.serverConnectType;
    }

    public ServerInfo getOriginalServer() {
        return this.originalServer;
    }

    public ServerInfo getServer() {
        return this.server != null ? this.server : this.originalServer;
    }

    public void setServer(ServerInfo server) {
        if (server == null) {
            throw new IllegalArgumentException("The server cannot be null!");
        }
        this.server = server;
    }

    @Override
    public String toString() {
        return "ServerPreConnectEvent(player=" + this.getPlayer() + ", serverConnectType=" + (Object)((Object)this.getServerConnectType()) + ", originalServer=" + this.getOriginalServer() + ", server=" + this.getServer() + ")";
    }
}

