package com.nickuc.login.api.event.velocity.connection;

import com.nickuc.login.api.event.internal.EventWithPlayer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class DefineAuthServerEvent
implements EventWithPlayer {
    private final Player player;
    private final RegisteredServer originalServer;
    private RegisteredServer server;

    public DefineAuthServerEvent(Player player, @Nullable RegisteredServer originalServer) {
        this.player = player;
        this.originalServer = originalServer;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Optional<RegisteredServer> getOriginalServer() {
        return Optional.ofNullable(this.originalServer);
    }

    public Optional<RegisteredServer> getServer() {
        return Optional.ofNullable(this.server != null ? this.server : this.originalServer);
    }

    public void setServer(@Nonnull RegisteredServer server) {
        if (server == null) {
            throw new IllegalArgumentException("The server cannot be null!");
        }
        this.server = server;
    }

    public String toString() {
        return "DefineAuthServerEvent(player=" + this.getPlayer() + ", originalServer=" + this.getOriginalServer() + ", server=" + this.getServer() + ")";
    }
}

