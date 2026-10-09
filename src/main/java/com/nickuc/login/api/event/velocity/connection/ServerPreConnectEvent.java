package com.nickuc.login.api.event.velocity.connection;

import com.nickuc.login.api.enums.ServerConnectType;
import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.nickuc.login.api.event.internal.velocity.VelocityCancellableEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import javax.annotation.Nonnull;


public class ServerPreConnectEvent
extends VelocityCancellableEvent
implements EventWithPlayer {
    private final Player player;
    private final ServerConnectType serverConnectType;
    private final RegisteredServer originalServer;
    private RegisteredServer server;

    public ServerPreConnectEvent(Player player, ServerConnectType serverConnectType, RegisteredServer originalServer) {
        this.player = player;
        this.serverConnectType = serverConnectType;
        this.originalServer = originalServer;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ServerConnectType getServerConnectType() {
        return this.serverConnectType;
    }

    public RegisteredServer getOriginalServer() {
        return this.originalServer;
    }

    public RegisteredServer getServer() {
        return this.server != null ? this.server : this.originalServer;
    }

    public void setServer(@Nonnull RegisteredServer server) {
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

