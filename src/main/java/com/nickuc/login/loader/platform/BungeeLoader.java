package com.nickuc.login.loader.platform;

import com.nickuc.login.loader.LoaderBootstrap;
import com.nickuc.login.loader.MemClassLoader;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class BungeeLoader
extends Plugin {
    private final LoaderBootstrap plugin;

    public BungeeLoader(String bootstrapClass) {
        MemClassLoader loader = new MemClassLoader(((Object)((Object)this)).getClass().getClassLoader());
        this.plugin = loader.createLoader(bootstrapClass, BungeeLoader.class, this);
    }

    public void onLoad() {
        this.plugin.load();
    }

    public void onEnable() {
        this.plugin.enable();
    }

    public void onDisable() {
        this.plugin.disable();
    }

    public abstract String getVersion();
}

