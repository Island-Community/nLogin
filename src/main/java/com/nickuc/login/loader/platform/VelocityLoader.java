package com.nickuc.login.loader.platform;

import com.nickuc.login.loader.LoaderBootstrap;
import com.nickuc.login.loader.MemClassLoader;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.File;
import java.nio.file.Path;
import org.slf4j.Logger;

public abstract class VelocityLoader {
    private final LoaderBootstrap plugin;
    private final ProxyServer server;
    private final Logger logger;
    private final PluginContainer pluginContainer;
    private final File dataDirectory;

    public VelocityLoader(String bootstrapClass, ProxyServer server, Logger logger, PluginContainer pluginContainer, Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.pluginContainer = pluginContainer;
        this.dataDirectory = dataDirectory.toFile();
        MemClassLoader loader = new MemClassLoader(this.getClass().getClassLoader());
        this.plugin = loader.createLoader(bootstrapClass, VelocityLoader.class, this);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.plugin.load();
        this.plugin.enable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.plugin.disable();
    }

    public ProxyServer getServer() {
        return this.server;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public PluginContainer getPluginContainer() {
        return this.pluginContainer;
    }

    public File getDataDirectory() {
        return this.dataDirectory;
    }

    public abstract String getVersion();
}

